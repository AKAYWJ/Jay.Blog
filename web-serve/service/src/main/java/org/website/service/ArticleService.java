package org.website.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.website.dao.mapper.entity.Article;

public interface ArticleService extends IService<Article> {
    // 视图分页
    Page<Article> getArticleViewPage(Page<Article> page, Article query);
    // 视图单条详情
    Article getArticleViewById(Long id);
}