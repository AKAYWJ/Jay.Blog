package org.website.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.common.utils.PasswordUtil;
import org.website.dao.mapper.entity.User;
import org.website.dao.mapper.util.UserContext;
import org.website.service.UserService;

import java.util.List;

/**
 * 用户管理控制器（带注解权限控制）
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ====================== 1. 分页 + 条件查询 ======================
    @GetMapping("/list")
    @RequiresPermission("user:list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name, String phone, Integer roleId) {

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like("name", name);
        if (phone != null && !phone.isEmpty()) wrapper.like("phone", phone);
        if (roleId != null) wrapper.eq("role_id", roleId);
        wrapper.eq("is_valid", "Y");

        Page<User> page = userService.page(new Page<>(pageNum, pageSize), wrapper);
        // 不向前端返回密码哈希
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(page);
    }

    // ====================== 2. 根据ID查询 ======================
    @GetMapping("/get/{id}")
    @RequiresPermission("user:list")
    public Result<User> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user != null) user.setPassword(null);
        return Result.success(user);
    }

    // ====================== 3. 新增 ======================
    @PostMapping("/add")
    @RequiresPermission("user:add")
    public Result<String> add(@RequestBody User user) {
        if (user.getName() == null || user.getPassword() == null) {
            return Result.fail(400, "姓名和密码不能为空");
        }
        user.setIsValid("Y");
        // ✅ 新增：密码加密（和注册保持一致）
        user.setPassword(PasswordUtil.encode(user.getPassword()));
        userService.save(user);
        return Result.success("新增成功");
    }

    // ====================== 4. 修改 ======================
    @PutMapping("/update")
    @RequiresPermission("user:edit")
    public Result<String> update(@RequestBody User user) {
        // ✅ 优化：修改时如果密码不为空，才加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        }
        userService.updateById(user);
        return Result.success("修改成功");
    }

    // ====================== 前台：修改自己的资料（无需 user:edit 权限，仅允许改本人） ======================
    @PutMapping("/self/update")
    public Result<String> updateSelf(@RequestBody User user) {
        User loginUser = UserContext.getUser();
        if (loginUser == null) {
            return Result.fail(401, "请先登录");
        }

        // 用户名重复校验（排除自己）
        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("name", user.getName()).ne("id", loginUser.getId());
            if (userService.count(wrapper) > 0) {
                return Result.fail(400, "用户名已被占用");
            }
        }

        // 只允许更新白名单字段，roleId/isValid/phone 等一律忽略，防止越权改角色
        User update = new User();
        update.setId(loginUser.getId());
        update.setName(user.getName());
        update.setEmail(user.getEmail());
        update.setAge(user.getAge());
        update.setSex(user.getSex());
        update.setAvatar(user.getAvatar());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            update.setPassword(PasswordUtil.encode(user.getPassword()));
        }
        userService.updateById(update);
        return Result.success("修改成功");
    }

    // ====================== 5. 根据ID删除 ======================
    @DeleteMapping("/delete/{id}")
    @RequiresPermission("user:delete")
    public Result<String> delete(@PathVariable Integer id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    // ====================== 6. 批量删除 ======================
    @DeleteMapping("/deleteBatch")
    @RequiresPermission("user:delete")
    public Result<String> deleteBatch(@RequestBody List<Integer> ids) {
        userService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

}