package org.website.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Comment;
import org.website.dao.mapper.entity.User;
import org.website.dao.mapper.util.UserContext;
import org.website.service.CommentService;
import org.website.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论控制器
 * 包含两部分接口：
 * 1. 后台管理接口（需权限）
 * 2. 前台博客接口（公开列表 + 登录发表）
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    // ====================== 前台博客公开接口 ======================

    /**
     * 前台：获取文章评论列表（分页 + 二级回复）
     * 公开访问，无需登录
     */
    @GetMapping("/front/list")
    public Result<Page<Map<String, Object>>> getFrontCommentList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam Long articleId) {

        // 1. 查询一级评论（parent_id 为 null，仅显示审核通过的）
        QueryWrapper<Comment> rootWrapper = new QueryWrapper<>();
        rootWrapper.eq("article_id", articleId)
                .eq("status", 1)
                .isNull("parent_id")
                .orderByDesc("create_time");

        Page<Comment> rootPage = commentService.page(new Page<>(pageNum, pageSize), rootWrapper);
        List<Comment> rootComments = rootPage.getRecords();

        // 2. 组装一级评论 + 对应回复列表
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Comment root : rootComments) {
            Map<String, Object> commentItem = new HashMap<>();
            commentItem.put("id", root.getId());
            commentItem.put("userId", root.getUserId());
            commentItem.put("userName", getUserName(root.getUserId()));
            commentItem.put("content", root.getContent());
            commentItem.put("createTime", root.getCreateTime());

            // 查询当前一级评论下的所有回复
            QueryWrapper<Comment> replyWrapper = new QueryWrapper<>();
            replyWrapper.eq("parent_id", root.getId())
                    .eq("status", 1)
                    .orderByAsc("create_time");
            List<Comment> replyList = commentService.list(replyWrapper);

            // 组装回复数据
            List<Map<String, Object>> replyResult = new ArrayList<>();
            for (Comment reply : replyList) {
                Map<String, Object> replyItem = new HashMap<>();
                replyItem.put("id", reply.getId());
                replyItem.put("userId", reply.getUserId());
                replyItem.put("userName", getUserName(reply.getUserId()));
                replyItem.put("content", reply.getContent());
                replyItem.put("createTime", reply.getCreateTime());
                replyResult.add(replyItem);
            }
            commentItem.put("replyList", replyResult);
            resultList.add(commentItem);
        }

        // 3. 封装分页结果返回
        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize, rootPage.getTotal());
        resultPage.setRecords(resultList);
        return Result.success(resultPage);
    }

    /**
     * 前台：发表评论/回复
     * 需要登录，无需特殊权限
     * 一级评论 parentId 传 null；回复 parentId 传对应主评论ID
     */
    @PostMapping("/front/add")
    public Result<String> addFrontComment(@RequestBody Comment comment) {
        User user = UserContext.getUser();
        if (user == null) {
            return Result.fail(401, "请先登录后发表评论");
        }

        // 参数校验
        if (comment.getArticleId() == null) {
            return Result.fail(403,"文章ID不能为空");
        }
        String content = comment.getContent();
        if (content == null || content.trim().isEmpty()) {
            return Result.fail(403,"评论内容不能为空");
        }

        // 保存评论
        Comment newComment = new Comment();
        newComment.setArticleId(comment.getArticleId());
        newComment.setUserId(Long.valueOf(user.getId()));
        newComment.setContent(content.trim());
        newComment.setParentId(comment.getParentId());
        newComment.setStatus(1); // 默认审核通过，如需人工审核可改为0
        newComment.setCreateTime(LocalDateTime.now());
        newComment.setUpdateTime(LocalDateTime.now());
        commentService.save(newComment);

        return Result.success("评论发表成功");
    }

    /**
     * 前台：删除本人发布的评论/回复
     * 只能删除自己的内容，删除主评论会级联删除所有回复
     */
    @DeleteMapping("/front/delete/{id}")
    public Result<String> deleteMyComment(@PathVariable Long id) {
        User user = UserContext.getUser();
        if (user == null) {
            return Result.fail(401, "请先登录");
        }

        Comment comment = commentService.getById(id);
        if (comment == null) {
            return Result.fail(403,"评论不存在");
        }

        // 核心校验：只能删除自己发布的评论
        if (comment.getUserId() != Integer.parseInt(String.valueOf(user.getId()))) {
            return Result.fail(403, "无权删除他人评论");
        }

        // 删除当前评论
        commentService.removeById(id);

        // 如果是主评论，级联删除其下所有回复
        QueryWrapper<Comment> replyWrapper = new QueryWrapper<>();
        replyWrapper.eq("parent_id", id);
        commentService.remove(replyWrapper);

        return Result.success("删除成功");
    }

    // ====================== 后台管理接口（需权限） ======================

    /**
     * 后台：分页查询全量评论（管理后台用）
     */
    @GetMapping("/list")
    @RequiresPermission("comment:list")
    public Result<Page<Comment>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Long articleId, Integer status) {

        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        if (articleId != null) wrapper.eq("article_id", articleId);
        if (status != null) wrapper.eq("status", status);
        wrapper.orderByDesc("create_time");

        Page<Comment> page = commentService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    /**
     * 后台：单条评论详情
     */
    @GetMapping("/get/{id}")
    @RequiresPermission("comment:list")
    public Result<Comment> getById(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        return Result.success(comment);
    }

    /**
     * 后台：修改评论状态（审核/禁用）
     */
    @PutMapping("/update")
    @RequiresPermission("comment:audit")
    public Result<String> update(@RequestBody Comment comment) {
        comment.setUpdateTime(LocalDateTime.now());
        commentService.updateById(comment);
        return Result.success("操作成功");
    }

    /**
     * 后台：删除单条评论（级联删除下属回复）
     */
    @DeleteMapping("/{id}")
    @RequiresPermission("comment:delete")
    public Result<String> delete(@PathVariable Long id) {
        // 删除主评论
        commentService.removeById(id);
        // 级联删除该评论下的所有回复
        QueryWrapper<Comment> replyWrapper = new QueryWrapper<>();
        replyWrapper.eq("parent_id", id);
        commentService.remove(replyWrapper);
        return Result.success("删除成功");
    }

    /**
     * 后台：批量删除评论
     */
    @DeleteMapping("/batch")
    @RequiresPermission("comment:delete")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        // 批量删除主评论
        commentService.removeByIds(ids);
        // 批量删除对应回复
        QueryWrapper<Comment> replyWrapper = new QueryWrapper<>();
        replyWrapper.in("parent_id", ids);
        commentService.remove(replyWrapper);
        return Result.success("批量删除成功");
    }

    // ====================== 私有工具方法 ======================

    /**
     * 根据用户ID获取用户名
     * 若评论表已冗余 userName 字段，可删除此方法直接取字段
     */
    private String getUserName(Long userId) {
        if (userId == null) return "匿名用户";
        User user = userService.getById(userId);
        return user != null ? user.getName() : "匿名用户";
    }
}