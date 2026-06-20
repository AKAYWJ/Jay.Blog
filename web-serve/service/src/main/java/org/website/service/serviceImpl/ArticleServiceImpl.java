package org.website.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.website.dao.mapper.ArticleMapper;
import org.website.dao.mapper.entity.Article;
import org.website.service.ArticleService;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {

    @Override
    public Page<Article> getArticleViewPage(Page<Article> page, Article query) {
        return baseMapper.selectArticlePage(page, query);
    }

    @Override
    public Article getArticleViewById(Long id) {
        return baseMapper.selectArticleById(id);
    }
}