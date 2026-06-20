package org.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.website.dao.mapper.entity.SysPermission;
import org.website.dao.mapper.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByRoleId(Integer roleId);
}