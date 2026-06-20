package org.website.controller.blog;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.Tag;
import org.website.service.TagService;

import java.util.List;

/**
 * 标签管理控制器
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

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