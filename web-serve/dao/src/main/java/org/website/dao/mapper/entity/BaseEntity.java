package org.website.dao.mapper.entity;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description: TODO
 * @date 2026/6/9 08:31
 */

@Data
public class BaseEntity {
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
