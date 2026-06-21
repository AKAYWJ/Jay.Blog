package org.website.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl; // 新增
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.website.common.dto.PermissionTreeVO;
import org.website.dao.mapper.SysPermissionMapper;
import org.website.dao.mapper.entity.SysPermission;
import org.website.service.SysPermissionService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// 关键：继承 ServiceImpl<SysPermissionMapper, SysPermission>

/**
 * @author AKAYWJ
 * @version 1.0
 * @description:
 */

public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
        implements SysPermissionService {

    // 移除 private final SysPermissionMapper permissionMapper; 依赖注入由父类完成

    /**
     * 构建权限树形结构
     */
    @Override
    public List<PermissionTreeVO> getPermissionTree() {
        // 1. 查询所有有效权限（现在可以正常使用 lambdaQuery）
        List<SysPermission> allPermissions = lambdaQuery()
                .eq(SysPermission::getIsValid, "Y")
                .orderByAsc(SysPermission::getSort)
                .list();

        // 2. 递归构建树形
        return buildTree(0, allPermissions);
    }

    /**
     * 递归生成树形结构
     */
    private List<PermissionTreeVO> buildTree(Integer parentId, List<SysPermission> allList) {
        List<PermissionTreeVO> treeList = new ArrayList<>();

        for (SysPermission permission : allList) {
            if (parentId.equals(permission.getParentId())) {
                PermissionTreeVO vo = new PermissionTreeVO();
                vo.setId(permission.getId());
                vo.setName(permission.getName());
                vo.setParentId(permission.getParentId());

                // 递归子节点
                List<PermissionTreeVO> children = buildTree(permission.getId(), allList);
                vo.setChildren(children);

                treeList.add(vo);
            }
        }
        return treeList;
    }
}