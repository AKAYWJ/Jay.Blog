package org.website.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Article;
import org.website.dao.mapper.entity.Category;
import org.website.service.ArticleService;
import org.website.service.CategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类管理控制器
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;

    // 分页条件查询
    @GetMapping("/list")
    @RequiresPermission("category:list")
    public Result<Page<Category>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name, Integer status) {

        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) wrapper.like("name", name);
        if (status != null) wrapper.eq("status", status);

        Page<Category> page = categoryService.page(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(page);
    }
    /**
     * 前台：获取全部分类 + 对应已发布文章数量
     * 公开访问，无需登录
     */
    @GetMapping("/front/list")
    public Result<List<Map<String, Object>>> getFrontCategoryList() {
        // 1. 查询所有启用的分类，按排序升序
        QueryWrapper<Category> categoryWrapper = new QueryWrapper<>();
        categoryWrapper.eq("status", 1)
                .orderByAsc("sort");
        List<Category> categoryList = categoryService.list(categoryWrapper);

        // 2. 组装返回数据，补充每个分类的文章数量
        List<Map<String, Object>> result = new ArrayList<>();
        for (Category category : categoryList) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", category.getId());
            item.put("name", category.getName());

            // 统计该分类下「已发布」的文章数量
            QueryWrapper<Article> articleWrapper = new QueryWrapper<>();
            articleWrapper.eq("category_id", category.getId())
                    .eq("status", 2); // 2=已发布，和你文章表状态值保持一致
            int articleCount = (int) articleService.count(articleWrapper);

            item.put("articleCount", articleCount);
            result.add(item);
        }

        return Result.success(result);
    }
    // 根据ID查询
    @GetMapping("/get/{id}")
    @RequiresPermission("category:list")
    public Result<Category> getById(@PathVariable Integer id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    // 新增分类
    @PostMapping("/save")
    @RequiresPermission("category:add")
    public Result<String> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("新增成功");
    }

    // 修改分类
    @PutMapping("/update")
    @RequiresPermission("category:edit")
    public Result<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("修改成功");
    }

    // 单条删除
    @DeleteMapping("/{id}")
    @RequiresPermission("category:delete")
    public Result<String> delete(@PathVariable Integer id) {
        categoryService.removeById(id);
        return Result.success("删除成功");
    }

    // 批量删除
    @DeleteMapping("/batch")
    @RequiresPermission("category:delete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        categoryService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    // 查询全部分类（下拉框使用，不分页）
    @GetMapping("/all")
    public Result<List<Category>> all() {
        List<Category> list = categoryService.list();
        return Result.success(list);
    }

    // 校验分类名称重复
    @GetMapping("/checkName")
    public Result<Boolean> checkName(String name, Integer id) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);
        if (id != null) {
            wrapper.ne("id", id);
        }
        long count = categoryService.count(wrapper);
        return Result.success(count > 0);
    }
}