package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.website.dao.mapper.RoleMapper;
import org.website.dao.mapper.entity.Role;
import org.website.service.RoleService;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}