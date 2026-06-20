package org.website.dao.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Role extends BaseEntity{

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String roleName;   // 角色名：超级管理员/管理员/普通用户
    private String roleCode;   // 标识：admin/manager/user
    private String description;
}