package org.website.common.dto;
import lombok.Data;

@Data
public class ArticleLikeDTO {
    private Long articleId;
    private Boolean isLike;
}