package org.website.dao.mapper.entity;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */


@Data
public class BaseEntity {
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
