package org.website.controller.role;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.dto.PermissionTreeVO;
import org.website.common.result.Result;
import org.website.service.RolePermissionService;
import org.website.service.SysPermissionService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private RolePermissionService rolePermissionService;
    /**
     * 【前端需要】获取权限树形结构
     * GET /permission/tree
     */
    @GetMapping("/tree")
    public Result<List<PermissionTreeVO>> getPermissionTree() {
        List<PermissionTreeVO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }

    @PostMapping("/assignPerm")
    public Result assignPerm(@RequestBody Map<String, Object> params) {
        Integer roleId = (Integer) params.get("roleId");
        List<Integer> permissionIds = (List<Integer>) params.get("permissionIds");

        rolePermissionService.assignRolePermission(roleId, permissionIds);
        return Result.success("权限分配成功");
    }

    // 新增：查询角色已有权限接口
    @GetMapping("/permIds/{roleId}")
    public Result getRolePermIds(@PathVariable Integer roleId) {
        Set<Integer> permIds = rolePermissionService.getPermissionIdsByRoleId(roleId);
        return Result.success(permIds);
    }
}