package org.website.dao.mapper.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Data
@TableName("blog_config")
public class BlogConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String blogName;
    private String blogAvatar;
    private String blogSignature;
    private String aboutMe;
    private String github;
    private String qq;
    private String email;
    private Integer allowComment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}