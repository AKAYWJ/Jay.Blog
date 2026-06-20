import request from './request'

// 获取角色列表
export function getRoleList(params) {
    return request({ url: '/role/list', method: 'get', params })
}

// 新增角色
export function addRole(data) {
    return request({ url: '/role/add', method: 'post', data })
}

// 修改角色
export function updateRole(data) {
    return request({ url: '/role/update', method: 'put', data })
}

// 删除角色
export function deleteRole(id) {
    return request({ url: `/role/delete/${id}`, method: 'delete' })
}

// 批量删除角色
export function deleteBatchRoles(ids) {
    return request({ url: `/role/deleteBatch`, method: 'delete', data: ids })
}

// 获取权限树形结构
export function getPermTree() {
    return request({ url: '/permission/tree', method: 'get' })
}

export function getRolePermIds(roleId) {
    return request({
        url: `/permission/permIds/${roleId}`,
        method: 'get'
    })
}

// 分配角色权限
export function assignRolePerm(data) {
    return request({ url: '/permission/assignPerm', method: 'post', data })
}