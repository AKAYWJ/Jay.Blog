package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.website.dao.mapper.ArticleLikeMapper;
import org.website.dao.mapper.entity.ArticleLike;
import org.website.service.ArticleLikeService;
import org.springframework.stereotype.Service;

@Service
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLike>
        implements ArticleLikeService {
}