package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.website.dao.mapper.entity.Article;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    // 分页查询视图 v_article_full
    Page<Article> selectArticlePage(Page<Article> page, @Param("article") Article query);
    // 根据ID查询视图单条数据
    Article selectArticleById(@Param("id") Long id);
}