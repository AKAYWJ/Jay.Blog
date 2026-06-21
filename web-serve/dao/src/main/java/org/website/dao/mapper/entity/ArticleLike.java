package org.website.dao.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Data
@TableName("article_like")
public class ArticleLike {
    @TableId(type = IdType.AUTO)
    private Long id;

    // 文章id
    private Long articleId;
    // 用户id
    private Long userId;
    // 点赞时间
    private LocalDateTime createTime;
}