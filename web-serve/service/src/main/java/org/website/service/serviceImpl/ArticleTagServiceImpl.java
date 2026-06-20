package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import org.website.dao.mapper.ArticleTagMapper;
import org.website.dao.mapper.entity.ArticleTag;
import org.website.service.ArticleTagService;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}