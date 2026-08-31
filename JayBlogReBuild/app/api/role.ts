import { request } from './request'
import type { ApiResult } from './request'

/** 角色实体 */
export interface SysRole {
  id?: number
  roleName?: string
  roleCode?: string
  description?: string
  createTime?: string
  updateTime?: string
}

/** 权限实体 */
export interface SysPermission {
  id: number
  parentId?: number
  name?: string
  code?: string
  type?: string
  path?: string
  component?: string
  icon?: string
  sort?: number
  isValid?: string
}

/** 权限树节点 */
export interface PermissionTreeVO {
  id: number
  name?: string
  parentId?: number
  children?: PermissionTreeVO[]
}

/** 角色列表（需 role:list） */
export const getRoleList = (params: { pageNum?: number; pageSize?: number; roleName?: string }) =>
  request<ApiResult<{ records: SysRole[]; total: number }>>({ url: '/role/list', method: 'get', params })

/** 根据ID查询角色（需 role:list） */
export const getRoleById = (id: number) =>
  request<ApiResult<SysRole>>({ url: `/role/get/${id}`, method: 'get' })

/** 新增角色（需 role:add） */
export const addRole = (data: SysRole) =>
  request<ApiResult<string>>({ url: '/role/add', method: 'post', data })

/** 修改角色（需 role:edit） */
export const updateRole = (data: SysRole) =>
  request<ApiResult<string>>({ url: '/role/update', method: 'put', data })

/** 删除角色（需 role:delete） */
export const deleteRole = (id: number) =>
  request<ApiResult<string>>({ url: `/role/delete/${id}`, method: 'delete' })

/** 批量删除角色（需 role:delete） */
export const deleteBatchRoles = (ids: number[]) =>
  request<ApiResult<string>>({ url: '/role/deleteBatch', method: 'delete', data: ids })

/** 权限树（需 role:list） */
export const getPermTree = () =>
  request<ApiResult<PermissionTreeVO[]>>({ url: '/permission/tree', method: 'get' })

/** 查询角色已有权限ID（需 role:edit） */
export const getRolePermIds = (roleId: number) =>
  request<ApiResult<number[]>>({ url: `/permission/permIds/${roleId}`, method: 'get' })

/** 分配角色权限（需 role:edit） */
export const assignRolePerm = (data: { roleId: number; permissionIds: number[] }) =>
  request<ApiResult<string>>({ url: '/permission/assignPerm', method: 'post', data })
