package org.website.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Article;
import org.website.dao.mapper.entity.Tag;
import org.website.service.ArticleService;
import org.website.service.TagService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 标签管理控制器
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleService articleService;

    /**
     * 前台：获取全部标签 + 对应已发布文章数量（公开）
     * 与 /category/front/list 对称，供仪表盘统计使用
     */
    @GetMapping("/front/list")
    public Result<List<Map<String, Object>>> getFrontTagList() {
        List<Tag> tagList = tagService.list();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tag tag : tagList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", tag.getId());
            item.put("name", tag.getName());

            // tagIds 字段在数据库里是逗号分隔字符串，例如 "1,3,5"
            // 用 FIND_IN_SET 比 like 更准确（避免 id=1 匹配到 id=10/11）
            QueryWrapper<Article> articleWrapper = new QueryWrapper<>();
            articleWrapper.eq("status", 2)
                    .apply("FIND_IN_SET({0}, tag_ids) > 0", tag.getId());
            int articleCount = (int) articleService.count(articleWrapper);
            item.put("articleCount", articleCount);

            result.add(item);
        }
        return Result.success(result);
    }

    // 分页查询
    @GetMapping("/list")
    @RequiresPermission("tag:list")
    public Result<Page<Tag>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name) {

        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like("name", name);

        Page<Tag> page = tagService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }

    // 根据ID查询
    @GetMapping("/get/{id}")
    @RequiresPermission("tag:list")
    public Result<Tag> getById(@PathVariable Integer id) {
        Tag tag = tagService.getById(id);
        return Result.success(tag);
    }

    // 新增
    @PostMapping("/save")
    @RequiresPermission("tag:add")
    public Result<String> add(@RequestBody Tag tag) {
        tagService.save(tag);
        return Result.success("新增成功");
    }

    // 修改
    @PutMapping("/update")
    @RequiresPermission("tag:edit")
    public Result<String> update(@RequestBody Tag tag) {
        tagService.updateById(tag);
        return Result.success("修改成功");
    }

    // 单删
    @DeleteMapping("/{id}")
    @RequiresPermission("tag:delete")
    public Result<String> delete(@PathVariable Integer id) {
        tagService.removeById(id);
        return Result.success("删除成功");
    }

    // 批量删除
    @DeleteMapping("/batch")
    @RequiresPermission("tag:delete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        tagService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    // 全部标签下拉
    @GetMapping("/all")
    public Result<List<Tag>> all() {
        List<Tag> list = tagService.list();
        return Result.success(list);
    }
}