package org.website.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.result.Result;
import org.website.common.utils.JwtUtil;
import org.website.common.utils.PasswordUtil;
import org.website.dao.mapper.entity.SysPermission;
import org.website.dao.mapper.entity.User;
import org.website.dao.mapper.util.UserContext;
import org.website.service.SysRoleService;
import org.website.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // ✅ 新增：注入角色权限服务
    @Autowired
    private SysRoleService roleService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        // 1. 非空校验
        if (user.getName() == null || user.getPassword() == null) {
            return Result.fail(400, "用户名和密码不能为空");
        }

        // 2. 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", user.getName());
        User existUser = userService.getOne(queryWrapper);
        if (existUser != null) {
            return Result.fail(400, "用户名已存在");
        }

        // 3. 自动填充默认值 + 生成唯一 no（≤20 位）
        user.setIsValid("Y");
        user.setRoleId(3); // 默认访客角色

        if (user.getNo() == null || user.getNo().trim().isEmpty()) {
            // 最多重试 5 次，防止极端冲突
            String newNo = null;
            for (int i = 0; i < 5; i++) {
                // 格式：U + 13位时间戳后9位 + 3位随机数 → 总长 1+9+3=13 ≤20
                long ts = System.currentTimeMillis();
                String tsStr = String.valueOf(ts).substring(4); // 截掉前4位，留9位
                String rndStr = String.format("%03d", (int) (Math.random() * 1000));
                newNo = "U" + tsStr + rndStr;

                // 查库确认是否已存在
                QueryWrapper<User> noWrapper = new QueryWrapper<>();
                noWrapper.eq("no", newNo);
                if (userService.getOne(noWrapper) == null) {
                    break; // 唯一，可用
                }
            }
            if (newNo == null) {
                return Result.fail(500, "生成账号失败，请重试");
            }
            user.setNo(newNo);
        }

        // 4. 密码加密
        String encryptPassword = PasswordUtil.encode(user.getPassword());
        user.setPassword(encryptPassword);

        // 5. 保存
        boolean save = userService.save(user);
        if (save) {
            return Result.success("注册成功");
        } else {
            return Result.fail(500, "注册失败");
        }
    }

    /**
     * 用户登录（仅支持手机号登录）
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User loginRequest) {
        // 1. 获取参数
        String phone = loginRequest.getPhone();
        String password = loginRequest.getPassword();

        // 2. 参数非空校验 + 手机号格式校验
        if (phone == null || phone.trim().isEmpty()) {
            return Result.fail(400, "手机号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.fail(400, "密码不能为空");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.fail(400, "请输入正确的11位手机号");
        }

        // 3. 查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userService.getOne(queryWrapper);

        // 4. 手机号未注册
        if (user == null) {
            return Result.fail(401, "该手机号未注册");
        }

        // 5. 账号已被禁用
        if (!"Y".equals(user.getIsValid())) {
            return Result.fail(403, "账号已被禁用，请联系管理员");
        }

        // 6. 密码验证
        if (!PasswordUtil.match(password, user.getPassword())) {
            return Result.fail(401, "密码错误");
        }

        // 7. 生成JWT token
        String token = JwtUtil.generateToken(Long.valueOf(user.getId()), user.getName());

        // ✅ 新增：查询用户权限和菜单
        List<SysPermission> permissions = roleService.getPermissionsByRoleId(user.getRoleId());
        // 提取权限标识列表（前端按钮权限用）
        List<String> permissionCodes = permissions.stream()
                .map(SysPermission::getCode)
                .collect(Collectors.toList());
        // 提取菜单列表（前端动态菜单用）
        List<SysPermission> menus = permissions.stream()
                .filter(p -> "M".equals(p.getType()))
                .collect(Collectors.toList());

        // 8. 返回完整用户信息（包含权限）
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("phone", user.getPhone());
        resultMap.put("userId", user.getId());
        resultMap.put("username", user.getName());
        resultMap.put("roleId", user.getRoleId());
        resultMap.put("permissions", permissionCodes); // 权限列表
        resultMap.put("menus", menus); // 菜单列表

        return Result.success(resultMap);
    }

    @GetMapping("/info")
    public Result<User> getCurrentUserInfo() {
        User currentUser = UserContext.getUser();
        return Result.success(currentUser);
    }

    @GetMapping("/id")
    public Result<Long> getCurrentUserId() {
        Long userId = Long.valueOf(UserContext.getUserId());
        return Result.success(userId);
    }
}