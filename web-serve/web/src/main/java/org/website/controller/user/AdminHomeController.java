package org.website.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Article;
import org.website.dao.mapper.entity.Comment;
import org.website.service.ArticleService;
import org.website.service.CommentService;

import java.util.*;

/**
 * 后台首页聚合接口
 * 一次拿到首页需要的全部数据（替代前端发 4 个并发请求）
 *
 * 返回：
 * - recentArticles: 最新 5 篇文章
 * - recentComments: 最新 5 条评论
 * - articleEventDates: 当月有发布文章的日期集合（YYYY-MM-DD）
 * - commentEventDates: 当月有评论的日期集合（YYYY-MM-DD）
 * - total: 文章/评论总数
 */
@RestController
@RequestMapping("/admin")
public class AdminHomeController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/home/stats")
    public Result<Map<String, Object>> homeStats() {
        Map<String, Object> result = new HashMap<>();

        // 最新文章（最多 5 篇）
        Page<Article> articlePage = new Page<>(1, 5);
        IPage<Article> articles = articleService.page(articlePage,
                new QueryWrapper<Article>().orderByDesc("create_time"));
        List<Map<String, Object>> recentArticles = new ArrayList<>();
        for (Article a : articles.getRecords()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("title", a.getTitle());
            m.put("status", a.getStatus());
            m.put("createTime", a.getCreateTime());
            m.put("updateTime", a.getUpdateTime());
            recentArticles.add(m);
        }

        // 最新评论（最多 5 条）
        Page<Comment> commentPage = new Page<>(1, 5);
        IPage<Comment> comments = commentService.page(commentPage,
                new QueryWrapper<Comment>().orderByDesc("create_time"));
        List<Map<String, Object>> recentComments = new ArrayList<>();
        for (Comment c : comments.getRecords()) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("articleId", c.getArticleId());
            m.put("content", c.getContent());
            m.put("status", c.getStatus());
            m.put("createTime", c.getCreateTime());
            recentComments.add(m);
        }

        // 总数
        long articleTotal = articleService.count();
        long commentTotal = commentService.count();

        result.put("recentArticles", recentArticles);
        result.put("recentComments", recentComments);
        result.put("articleTotal", articleTotal);
        result.put("commentTotal", commentTotal);

        return Result.success(result);
    }

    /**
     * 当月有事件的日期（替代前端发大列表过滤）
     * @param year 年份（如 2026）
     * @param month 月份 1-12
     * @return 包含文章/评论事件的日期集合
     */
    @GetMapping("/home/events")
    public Result<Map<String, Object>> monthEvents(Integer year, Integer month) {
        if (year == null || month == null) {
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH) + 1;
        }
        String prefix = String.format("%d-%02d", year, month);

        Set<String> articleDates = new HashSet<>();
        for (Article a : articleService.list(
                new QueryWrapper<Article>().select("create_time"))) {
            if (a.getCreateTime() != null) {
                String day = a.getCreateTime().toString().substring(0, 10);
                if (day.startsWith(prefix)) articleDates.add(day);
            }
        }

        Set<String> commentDates = new HashSet<>();
        for (Comment c : commentService.list(
                new QueryWrapper<Comment>().select("create_time"))) {
            if (c.getCreateTime() != null) {
                String day = c.getCreateTime().toString().substring(0, 10);
                if (day.startsWith(prefix)) commentDates.add(day);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("articleDates", articleDates);
        result.put("commentDates", commentDates);
        return Result.success(result);
    }
}
