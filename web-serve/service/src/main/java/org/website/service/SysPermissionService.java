package org.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.website.common.dto.PermissionTreeVO;
import org.website.dao.mapper.entity.SysPermission;

import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

public interface SysPermissionService extends IService<SysPermission> {
    // 获取权限树形结构
    List<PermissionTreeVO> getPermissionTree();
}