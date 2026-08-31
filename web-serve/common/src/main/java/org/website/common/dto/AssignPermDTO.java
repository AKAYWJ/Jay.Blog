package org.website.common.dto;

import lombok.Data;

import java.util.List;

/**
 * @author AKAYWJ
 * @version 1.0
 * @description: 角色分配权限请求 DTO
 */

@Data
public class AssignPermDTO {
    private Integer roleId;
    private List<Integer> permissionIds;
}