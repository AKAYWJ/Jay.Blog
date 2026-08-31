package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.website.dao.mapper.SysRolePermissionMapper;
import org.website.dao.mapper.entity.SysPermission;
import org.website.dao.mapper.entity.SysRolePermission;
import org.website.service.RolePermissionService;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 核心：角色分配权限（先删后加，保证原子性）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermission(Integer roleId, List<Integer> permissionIds) {
        // 1. 删除旧权限
        sysRolePermissionMapper.deleteByRoleId(roleId);

        // 2. 空权限直接返回
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }

        // 3. 批量插入新权限
        sysRolePermissionMapper.batchInsert(roleId, permissionIds);
    }

    @Override
    public Set<Integer> getPermissionIdsByRoleId(Integer roleId) {
        List<Integer> permissionIds = sysRolePermissionMapper.selectPermissionIdsByRoleId(roleId);
        return permissionIds.stream().collect(Collectors.toSet());
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Integer roleId) {
        return sysRolePermissionMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public Set<Integer> getPermissionIdsByRoleIds(List<Integer> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }
        List<Integer> permissionIds = sysRolePermissionMapper.selectPermissionIdsByRoleIds(roleIds);
        return permissionIds.stream().collect(Collectors.toSet());
    }

    @Override
    public boolean hasPermission(Integer roleId, Integer permissionId) {
        return sysRolePermissionMapper.countByRoleIdAndPermissionId(roleId, permissionId) > 0;
    }
}