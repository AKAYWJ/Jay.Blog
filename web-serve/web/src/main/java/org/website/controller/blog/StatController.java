package org.website.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Article;
import org.website.dao.mapper.entity.Comment;
import org.website.dao.mapper.entity.Visitor;
import org.website.service.ArticleService;
import org.website.service.CommentService;
import org.website.service.VisitorService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@RestController
@RequestMapping("/stat")
public class StatController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private VisitorService visitorService;

    /**
     * 全站首页统计：文章总数、访客总数、评论总数
     */
    @GetMapping("/front")
    public Result<Map<String, Long>> getBlogStat() {
        // 已发布文章 status=2
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, 2);
        Long articleTotal = articleService.count(articleWrapper);

        // 全部访客
        Long visitorTotal = visitorService.count();

        // 全部评论（可加审核状态过滤，按需修改）
        Long commentTotal = commentService.count();

        Map<String, Long> map = new HashMap<>();
        map.put("articleTotal", articleTotal);
        map.put("visitorTotal", visitorTotal);
        map.put("commentTotal", commentTotal);
        return Result.success(map);
    }
}