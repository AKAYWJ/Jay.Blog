package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.website.dao.mapper.entity.SysPermission;
import org.website.dao.mapper.entity.SysRolePermission;

import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {
    /**
     * 删除角色原有权限
     */
    int deleteByRoleId(@Param("roleId") Integer roleId);

    /**
     * 批量插入新权限
     */
    int batchInsert(@Param("roleId") Integer roleId, @Param("permissionIds") List<Integer> permissionIds);

    /**
     * 查询角色的权限ID集合
     */
    List<Integer> selectPermissionIdsByRoleId(@Param("roleId") Integer roleId);

    /**
     * 批量查询多个角色的权限ID集合
     */
    List<Integer> selectPermissionIdsByRoleIds(@Param("roleIds") List<Integer> roleIds);

    /**
     * 统计角色是否拥有某个权限
     */
    int countByRoleIdAndPermissionId(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId);

    /**
     * 根据角色ID查询有效权限列表（联查 sys_permission）
     */
    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Integer roleId);
}