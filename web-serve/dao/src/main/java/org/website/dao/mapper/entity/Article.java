package org.website.dao.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long categoryId;

    // 新增：数据库存储标签ID逗号串
    private String tagIds;

    private String title;
    private String summary;
    private String cover;
    private String contentMd;
    private String contentHtml;
    private Long viewCount;
    private Long likeCount;
    private Integer isTop;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

    // ========== 视图返回临时字段（数据库不存在，仅查询用） ==========
    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String tagIdsStr;
    @TableField(exist = false)
    private String tagNamesStr;

    // 前后端交互数组（新增/编辑接收、页面展示）
    @TableField(exist = false)
    private List<Long> tagIdList;
    @TableField(exist = false)
    private List<String> tagNameList;
}