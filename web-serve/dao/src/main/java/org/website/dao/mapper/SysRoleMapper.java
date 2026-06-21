package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.website.dao.mapper.entity.SysPermission;
import org.website.dao.mapper.entity.SysRole;

import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 根据角色ID查询权限列表
     */
    List<SysPermission> selectPermissionsByRoleId(@Param("roleId") Integer roleId);
}