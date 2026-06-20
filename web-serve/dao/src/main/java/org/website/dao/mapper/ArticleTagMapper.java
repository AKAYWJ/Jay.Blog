package org.website.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.website.dao.mapper.entity.ArticleTag;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}