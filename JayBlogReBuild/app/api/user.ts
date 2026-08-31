import { request } from './request'
import type { ApiResult } from './request'

/** 登录入参 */
export interface LoginData {
  phone: string
  password: string
  captchaId: string
  captcha: string
}

/** 注册入参 */
export interface RegisterData {
  name: string
  password: string
  phone: string
  email?: string
  age?: number
  sex?: number
}

/** 登录/注册返回 */
export interface LoginResult {
  token: string
  userId: number
  username: string
  roleId: number
  phone: string
  avatar?: string
  email?: string
  permissions: string[]
  menus: any[]
}

/** 用户信息 */
export interface User {
  id?: number
  no?: string
  name?: string
  password?: string
  age?: number
  sex?: number
  phone?: string
  avatar?: string
  email?: string
  roleId?: number
  isValid?: string
  createTime?: string
  updateTime?: string
}

/** 用户列表分页参数 */
export interface UserListParams {
  pageNum?: number
  pageSize?: number
  name?: string
  phone?: string
  roleId?: number
}

/** 登录 */
export const login = (data: LoginData) =>
  request<ApiResult<LoginResult>>({ url: '/auth/login', method: 'post', data })

/** 注册 */
export const register = (data: RegisterData) =>
  request<ApiResult<string>>({ url: '/auth/register', method: 'post', data })

/** 获取当前登录用户信息 */
export const getAuthInfo = () =>
  request<ApiResult<User>>({ url: '/auth/info', method: 'get' })

/** 获取当前登录用户ID */
export const getAuthId = () =>
  request<ApiResult<number>>({ url: '/auth/id', method: 'get' })

/** 后台：用户列表（需 user:list） */
export const getUserList = (params: UserListParams) =>
  request<ApiResult<{ records: User[]; total: number }>>({ url: '/user/list', method: 'get', params })

/** 后台：新增用户（需 user:add） */
export const addUser = (data: User) =>
  request<ApiResult<string>>({ url: '/user/add', method: 'post', data })

/** 后台：修改用户（需 user:edit） */
export const updateUser = (data: User) =>
  request<ApiResult<string>>({ url: '/user/update', method: 'put', data })

/** 前台：修改自己的资料（仅需登录，不能改角色/手机号） */
export const updateSelf = (data: User) =>
  request<ApiResult<string>>({ url: '/user/self/update', method: 'put', data })

/** 后台：删除用户（需 user:delete） */
export const deleteUser = (id: number) =>
  request<ApiResult<string>>({ url: `/user/delete/${id}`, method: 'delete' })

/** 后台：批量删除用户（需 user:delete） */
export const deleteBatchUsers = (ids: number[]) =>
  request<ApiResult<string>>({ url: '/user/deleteBatch', method: 'delete', data: ids })
