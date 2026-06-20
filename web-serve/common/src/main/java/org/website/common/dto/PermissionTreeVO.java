package org.website.common.dto;

import lombok.Data;
import java.util.List;

/**
 * 权限树形结构VO  适配前端 el-tree
 */
@Data
public class PermissionTreeVO {
    private Integer id;             // 权限ID
    private String name;            // 权限名称
    private Integer parentId;       // 父ID
    private List<PermissionTreeVO> children; // 子节点
}