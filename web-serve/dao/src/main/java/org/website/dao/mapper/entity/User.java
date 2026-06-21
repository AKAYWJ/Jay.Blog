package org.website.dao.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

@Data
public class User extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String no;
    private String name;
    private String password;
    private Integer age;
    private Integer sex;
    private String phone;
    // 新增头像
    private String avatar;
    // 新增邮箱
    private String email;
    private Integer roleId;
    private String isValid;
}