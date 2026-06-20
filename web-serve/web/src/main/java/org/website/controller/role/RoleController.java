package org.website.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Role;
import org.website.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // 分页列表
    @GetMapping("/list")
    public Result<Page<Role>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String roleName) {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        if (roleName != null && !"".equals(roleName)) {
            wrapper.like("role_name", roleName);
        }
        Page<Role> page = roleService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    // 根据ID查询
    @GetMapping("/get/{id}")
    public Result<Role> get(@PathVariable Integer id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    // 新增
    @PostMapping("/add")
    public Result<String> add(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("新增成功");
    }

    // 修改
    @PutMapping("/update")
    public Result<String> update(@RequestBody Role role) {
        roleService.updateById(role);
        return Result.success("修改成功");
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        roleService.removeById(id);
        return Result.success("删除成功");
    }

    // 批量删除
    @DeleteMapping("/deleteBatch")
    public Result<String> deleteBatch(@RequestBody List<Integer> ids) {
        roleService.removeByIds(ids);
        return Result.success("批量删除成功");
    }
}