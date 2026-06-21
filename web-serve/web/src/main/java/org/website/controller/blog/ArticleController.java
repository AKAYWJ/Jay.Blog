package org.website.controller.blog;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.dto.ArticleLikeDTO;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Article;
import org.website.dao.mapper.entity.ArticleLike;
import org.website.dao.mapper.entity.User;
import org.website.dao.mapper.util.UserContext;
import org.website.service.ArticleLikeService;
import org.website.service.ArticleService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleLikeService articleLikeService;

    // 拆分标签字符串工具
    private void splitTagStrToList(Article article) {
        if (StrUtil.isNotBlank(article.getTagIdsStr())) {
            List<Long> idList = Arrays.stream(article.getTagIdsStr().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            article.setTagIdList(idList);
        } else {
            article.setTagIdList(new ArrayList<>());
        }
        if (StrUtil.isNotBlank(article.getTagNamesStr())) {
            List<String> nameList = Arrays.stream(article.getTagNamesStr().split(","))
                    .collect(Collectors.toList());
            article.setTagNameList(nameList);
        } else {
            article.setTagNameList(new ArrayList<>());
        }
    }

    // ====================== 后台管理接口 ======================
    @GetMapping("/list")
    @RequiresPermission("article:list")
    public Result<Page<Article>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String title, Long categoryId, Integer status) {

        Page<Article> page = new Page<>(pageNum, pageSize);
        Article query = new Article();
        query.setTitle(title);
        query.setCategoryId(categoryId);
        query.setStatus(status);

        Page<Article> dataPage = articleService.getArticleViewPage(page, query);
        dataPage.getRecords().forEach(this::splitTagStrToList);
        return Result.success(dataPage);
    }

    @GetMapping("/{id}")
    @RequiresPermission("article:list")
    public Result<Article> getById(@PathVariable Long id) {
        Article article = articleService.getArticleViewById(id);
        if (article != null) splitTagStrToList(article);
        return Result.success(article);
    }

    @PostMapping("/save")
    @RequiresPermission("article:add")
    public Result<String> add(@RequestBody Article article) {
        if (article.getTagIdList() != null && !article.getTagIdList().isEmpty()) {
            String tagIds = article.getTagIdList().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            article.setTagIds(tagIds);
        } else {
            article.setTagIds("");
        }
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleService.save(article);
        return Result.success("新增成功");
    }

    @PutMapping("/update")
    @RequiresPermission("article:edit")
    public Result<String> update(@RequestBody Article article) {
        if (article.getTagIdList() != null && !article.getTagIdList().isEmpty()) {
            String tagIds = article.getTagIdList().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            article.setTagIds(tagIds);
        } else {
            article.setTagIds("");
        }
        article.setUpdateTime(LocalDateTime.now());
        articleService.updateById(article);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    @RequiresPermission("article:delete")
    public Result<String> delete(@PathVariable Long id) {
        articleService.removeById(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/batch")
    @RequiresPermission("article:batchDelete")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        articleService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    // ====================== 前台博客接口 ======================
    /**
     * 前台：文章详情（查询v_article_full视图，自带categoryName、tagIdsStr/tagNamesStr）
     */
    @GetMapping("/front/detail/{id}")
    public Result<Article> getFrontDetail(@PathVariable Long id) {
        Article article = articleService.getArticleViewById(id);
        // 校验：必须存在且状态=已发布2
        if (article == null || !Integer.valueOf(2).equals(article.getStatus())) {
            return Result.fail(404, "文章不存在");
        }
        // 拆分视图拼接标签字符串为 tagIdList / tagNameList
        splitTagStrToList(article);
        // 浏览量自增（单独更新，不改动文章主体数据）
        Article updateView = new Article();
        updateView.setId(id);
        updateView.setViewCount(article.getViewCount() + 1);
        articleService.updateById(updateView);
        // 返回前端时同步增加本地展示浏览量
        article.setViewCount(article.getViewCount() + 1);
        return Result.success(article);
    }

    @GetMapping("/front/like/status")
    public Result<Map<String, Object>> getLikeStatus(@RequestParam Long articleId) {
        Map<String, Object> result = new HashMap<>();
        // 统一使用视图查询，保证likeCount为聚合最新值
        Article article = articleService.getArticleViewById(articleId);
        // 统一校验：文章存在且已发布
        if (article == null || !Integer.valueOf(2).equals(article.getStatus())) {
            return Result.fail(403, "文章不存在");
        }
        result.put("likeCount", article.getLikeCount());
        boolean isLiked = false;
        User loginUser = UserContext.getUser();
        if (loginUser != null) {
            QueryWrapper<ArticleLike> likeWrapper = new QueryWrapper<>();
            likeWrapper.eq("article_id", articleId).eq("user_id", loginUser.getId());
            isLiked = articleLikeService.count(likeWrapper) > 0;
        }
        result.put("isLiked", isLiked);
        return Result.success(result);
    }

    @PostMapping("/front/like")
    public Result<Map<String, Object>> likeArticle(@RequestBody ArticleLikeDTO dto) {
        User loginUser = UserContext.getUser();
        if (loginUser == null) return Result.fail(401, "请先登录");
        Long articleId = dto.getArticleId();
        Boolean targetLike = dto.getIsLike();
        if (articleId == null || targetLike == null) return Result.fail(403, "参数缺失");
        Article article = articleService.getById(articleId);
        if (article == null || article.getStatus() != 2) return Result.fail(403, "文章不存在");

        QueryWrapper<ArticleLike> likeWrapper = new QueryWrapper<>();
        likeWrapper.eq("article_id", articleId).eq("user_id", loginUser.getId());
        boolean hasLike = articleLikeService.count(likeWrapper) > 0;

        if (targetLike) {
            if (hasLike) return Result.fail(403, "您已点赞过该文章");
            ArticleLike record = new ArticleLike();
            record.setArticleId(articleId);
            record.setUserId(Long.valueOf(loginUser.getId()));
            record.setCreateTime(LocalDateTime.now());
            articleLikeService.save(record);
            article.setLikeCount(article.getLikeCount() + 1);
        } else {
            if (!hasLike) return Result.fail(403, "尚未点赞，无法取消");
            articleLikeService.remove(likeWrapper);
            article.setLikeCount(Math.max(0, article.getLikeCount() - 1));
        }
        articleService.updateById(article);
        Map<String, Object> res = new HashMap<>();
        res.put("likeCount", article.getLikeCount());
        res.put("isLiked", targetLike);
        return Result.success(res);
    }

    @GetMapping("/front/list")
    public Result<Page<Article>> getFrontArticleList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String title, Long categoryId) {

        Page<Article> page = new Page<>(pageNum, pageSize);
        Article query = new Article();
        query.setTitle(title);
        query.setCategoryId(categoryId);
        query.setStatus(2); // 前台只查已发布
        Page<Article> dataPage = articleService.getArticleViewPage(page, query);
        dataPage.getRecords().forEach(this::splitTagStrToList);
        return Result.success(dataPage);
    }

    /**
     * 前台：热门文章（浏览量前2）
     */
    @GetMapping("/front/hot")
    public Result<List<Article>> getHotArticle() {
        Article query = new Article();
        query.setStatus(2);
        // 只取前2条
        Page<Article> page = new Page<>(1, 2);
        Page<Article> dataPage = articleService.getArticleViewPage(page, query);
        List<Article> records = dataPage.getRecords();
        records.forEach(this::splitTagStrToList);
        return Result.success(records);
    }
}