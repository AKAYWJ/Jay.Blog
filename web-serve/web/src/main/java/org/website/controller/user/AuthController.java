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
import org.website.service.RolePermissionService;
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

    @Autowired
    private RolePermissionService rolePermissionService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        if (user.getName() == null || user.getPassword() == null) {
            return Result.fail(400, "用户名和密码不能为空");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", user.getName());
        User existUser = userService.getOne(queryWrapper);
        if (existUser != null) {
            return Result.fail(400, "用户名已存在");
        }

        // 手机号唯一性校验（登录按手机号查询，重复会串号）
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone", user.getPhone());
            if (userService.getOne(phoneWrapper) != null) {
                return Result.fail(400, "该手机号已被注册");
            }
        }

        user.setIsValid("Y");
        user.setRoleId(3);

        if (user.getNo() == null || user.getNo().trim().isEmpty()) {
            String newNo = null;
            for (int i = 0; i < 5; i++) {
                long ts = System.currentTimeMillis();
                String tsStr = String.valueOf(ts).substring(4);
                String rndStr = String.format("%03d", (int) (Math.random() * 1000));
                newNo = "U" + tsStr + rndStr;

                QueryWrapper<User> noWrapper = new QueryWrapper<>();
                noWrapper.eq("no", newNo);
                if (userService.getOne(noWrapper) == null) {
                    break;
                }
            }
            if (newNo == null) {
                return Result.fail(500, "生成账号失败，请重试");
            }
            user.setNo(newNo);
        }

        String encryptPassword = PasswordUtil.encode(user.getPassword());
        user.setPassword(encryptPassword);

        boolean save = userService.save(user);
        if (save) {
            return Result.success("注册成功");
        } else {
            return Result.fail(500, "注册失败");
        }
    }

    /**
     * 用户登录（手机号登录，返回头像+邮箱）
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody User loginRequest) {
        String phone = loginRequest.getPhone();
        String password = loginRequest.getPassword();

        // 1. 校验图形验证码
        String captchaId = loginRequest.getCaptchaId();
        String captchaCode = loginRequest.getCaptcha();
        if (captchaId == null || captchaCode == null || captchaCode.trim().isEmpty()) {
            return Result.fail(400, "请输入验证码");
        }
        if (!CaptchaController.verify(captchaId, captchaCode)) {
            return Result.fail(400, "验证码错误或已过期");
        }

        if (phone == null || phone.trim().isEmpty()) {
            return Result.fail(400, "手机号不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Result.fail(400, "密码不能为空");
        }
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Result.fail(400, "请输入正确的11位手机号");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        User user = userService.getOne(queryWrapper);

        if (user == null) {
            return Result.fail(401, "该手机号未注册");
        }
        if (!"Y".equals(user.getIsValid())) {
            return Result.fail(403, "账号已被禁用，请联系管理员");
        }
        if (!PasswordUtil.match(password, user.getPassword())) {
            return Result.fail(401, "密码错误");
        }

        String token = JwtUtil.generateToken(user.getId().longValue(), user.getName());
        List<SysPermission> permissions = rolePermissionService.getPermissionsByRoleId(user.getRoleId());
        List<String> permissionCodes = permissions.stream()
                .map(SysPermission::getCode)
                .collect(Collectors.toList());
        List<SysPermission> menus = permissions.stream()
                .filter(p -> "M".equals(p.getType()))
                .collect(Collectors.toList());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("userId", user.getId());
        resultMap.put("username", user.getName());
        resultMap.put("roleId", user.getRoleId());
        resultMap.put("phone", user.getPhone());
        // 新增返回头像、邮箱
        resultMap.put("avatar", user.getAvatar());
        resultMap.put("email", user.getEmail());
        resultMap.put("permissions", permissionCodes);
        resultMap.put("menus", menus);

        return Result.success(resultMap);
    }

    @GetMapping("/info")
    public Result<User> getCurrentUserInfo() {
        User currentUser = UserContext.getUser();
        if (currentUser != null) {
            // 不向前端返回密码哈希
            currentUser.setPassword(null);
        }
        return Result.success(currentUser);
    }

    @GetMapping("/id")
    public Result<Long> getCurrentUserId() {
        Long userId = UserContext.getUserId().longValue();
        return Result.success(userId);
    }
}