package org.website.controller.role;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.SysRole;
import org.website.service.SysRoleService;

import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    // 分页列表
    @GetMapping("/list")
    @RequiresPermission("role:list")
    public Result<Page<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String roleName) {
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        if (roleName != null && !"".equals(roleName)) {
            wrapper.like("role_name", roleName);
        }
        Page<SysRole> page = sysRoleService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    // 根据ID查询
    @GetMapping("/get/{id}")
    @RequiresPermission("role:list")
    public Result<SysRole> get(@PathVariable Integer id) {
        SysRole role = sysRoleService.getById(id);
        return Result.success(role);
    }

    // 新增
    @PostMapping("/add")
    @RequiresPermission("role:add")
    public Result<String> add(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.success("新增成功");
    }

    // 修改
    @PutMapping("/update")
    @RequiresPermission("role:edit")
    public Result<String> update(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.success("修改成功");
    }

    // 删除
    @DeleteMapping("/delete/{id}")
    @RequiresPermission("role:delete")
    public Result<String> delete(@PathVariable Integer id) {
        sysRoleService.removeById(id);
        return Result.success("删除成功");
    }

    // 批量删除
    @DeleteMapping("/deleteBatch")
    @RequiresPermission("role:delete")
    public Result<String> deleteBatch(@RequestBody List<Integer> ids) {
        sysRoleService.removeByIds(ids);
        return Result.success("批量删除成功");
    }
}