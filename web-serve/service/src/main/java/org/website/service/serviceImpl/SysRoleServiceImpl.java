package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.website.dao.mapper.SysRoleMapper;
import org.website.dao.mapper.entity.SysRole;
import org.website.service.SysRoleService;

/**
 * 角色服务实现类
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
}