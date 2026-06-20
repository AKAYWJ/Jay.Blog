package org.website.controller.blog;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.springframework.web.bind.annotation.*;
import org.website.dao.mapper.entity.ArticleTag;
import org.website.service.ArticleTagService;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/articleTag")
public class ArticleTagController {
    @Resource
    private ArticleTagService articleTagService;

    @GetMapping("/list")
    public Page<ArticleTag> list(Long pageNum, Long pageSize){
        return articleTagService.page(new Page<>(pageNum, pageSize), null);
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody ArticleTag at){
        return articleTagService.save(at);
    }

    @DeleteMapping("/batch")
    public Boolean batchDel(@RequestBody List<Long> ids){
        return articleTagService.removeByIds(ids);
    }
}