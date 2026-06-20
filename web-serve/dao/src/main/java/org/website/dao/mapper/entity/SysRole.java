package org.website.dao.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 角色实体类（完全适配你现有的role表）
 */
@Data
@TableName("role") // 对应你的表名
public class SysRole {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    // 对应你的role_name字段（MyBatis-Plus自动驼峰转下划线）
    private String roleName;
    
    // 对应你的role_code字段
    private String roleCode;
    
    private String description;
    private Date createTime;
    private Date updateTime;
}