package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.website.dao.mapper.VisitorMapper;
import org.website.dao.mapper.entity.Visitor;
import org.website.service.VisitorService;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Service
public class VisitorServiceImpl extends ServiceImpl<VisitorMapper, Visitor> implements VisitorService {
}