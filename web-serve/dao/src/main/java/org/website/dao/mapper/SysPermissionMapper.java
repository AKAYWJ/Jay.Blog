package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.website.dao.mapper.entity.SysPermission;

import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    // 根据父ID查询子权限
    List<SysPermission> selectByParentId(@Param("parentId") Integer parentId);
}