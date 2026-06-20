package org.website.service;

import org.website.dao.mapper.entity.SysPermission;

import java.util.List;
import java.util.Set;

/**
 * 角色权限关联服务接口
 * 负责角色与权限的关联管理，遵循RBAC权限模型设计
 */
public interface RolePermissionService {

    /**
     * 核心方法：为角色分配权限（先删后加，保证原子性）
     * @param roleId 角色ID（必传，非空）
     * @param permissionIds 权限ID列表（空列表表示清空权限）
     */
    void assignRolePermission(Integer roleId, List<Integer> permissionIds);

    /**
     * 查询角色已拥有的权限ID集合
     * @param roleId 角色ID
     * @return 权限ID集合
     */
    Set<Integer> getPermissionIdsByRoleId(Integer roleId);

    /**
     * 查询角色已拥有的权限列表（含权限详情）
     * @param roleId 角色ID
     * @return 权限对象列表
     */
    List<SysPermission> getPermissionsByRoleId(Integer roleId);

    /**
     * 批量查询多个角色的权限ID集合
     * @param roleIds 角色ID列表
     * @return 权限ID集合（去重）
     */
    Set<Integer> getPermissionIdsByRoleIds(List<Integer> roleIds);

    /**
     * 检查角色是否拥有指定权限
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return true-拥有权限，false-无权限
     */
    boolean hasPermission(Integer roleId, Integer permissionId);
}