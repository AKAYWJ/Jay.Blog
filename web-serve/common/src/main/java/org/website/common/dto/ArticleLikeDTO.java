package org.website.common.dto;
import lombok.Data;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Data
public class ArticleLikeDTO {
    private Long articleId;
    private Boolean isLike;
}