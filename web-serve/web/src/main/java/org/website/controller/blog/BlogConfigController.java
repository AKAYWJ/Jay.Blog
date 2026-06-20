package org.website.controller.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.website.common.annotation.RequiresPermission;
import org.website.common.result.Result;
import org.website.dao.mapper.entity.BlogConfig;
import org.website.service.BlogConfigService;

/**
 * 博客全局配置控制器
 */
@RestController
@RequestMapping("/blogConfig")
public class BlogConfigController {

    @Autowired
    private BlogConfigService blogConfigService;

    // 获取全局配置（仅一条id=1）
    @GetMapping("/get")
    @RequiresPermission("config:view")
    public Result<BlogConfig> getConfig() {
        BlogConfig config = blogConfigService.getById(1L);
        return Result.success(config);
    }

    // 修改配置
    @PutMapping("/update")
    @RequiresPermission("config:edit")
    public Result<String> update(@RequestBody BlogConfig blogConfig) {
        blogConfig.setId(1L);
        blogConfigService.updateById(blogConfig);
        return Result.success("配置修改成功");
    }
}