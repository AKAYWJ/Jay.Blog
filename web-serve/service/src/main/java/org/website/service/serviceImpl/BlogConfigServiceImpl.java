package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.website.dao.mapper.BlogConfigMapper;
import org.website.dao.mapper.entity.BlogConfig;
import org.website.service.BlogConfigService;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Service
public class BlogConfigServiceImpl extends ServiceImpl<BlogConfigMapper, BlogConfig> implements BlogConfigService {
}