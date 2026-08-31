import { request } from './request'
import type { ApiResult } from './request'

/** 分类实体 */
export interface Category {
  id?: number
  name?: string
  sort?: number
  status?: number
  createTime?: string
  updateTime?: string
}

/** 后台：分页分类（需 category:list） */
export const getCategoryAdminList = (params: { pageNum?: number; pageSize?: number; name?: string; status?: number }) =>
  request<ApiResult<{ records: Category[]; total: number }>>({ url: '/category/list', method: 'get', params })

/** 后台：根据ID查询（需 category:list） */
export const getCategoryById = (id: number) =>
  request<ApiResult<Category>>({ url: `/category/get/${id}`, method: 'get' })

/** 后台：新增（需 category:add） */
export const addCategory = (data: Category) =>
  request<ApiResult<string>>({ url: '/category/save', method: 'post', data })

/** 后台：修改（需 category:edit） */
export const updateCategory = (data: Category) =>
  request<ApiResult<string>>({ url: '/category/update', method: 'put', data })

/** 后台：删除（需 category:delete） */
export const deleteCategory = (id: number) =>
  request<ApiResult<string>>({ url: `/category/${id}`, method: 'delete' })

/** 后台：批量删除（需 category:delete） */
export const batchDeleteCategory = (ids: number[]) =>
  request<ApiResult<string>>({ url: '/category/batch', method: 'delete', data: ids })

/** 全部分类（下拉框，公开） */
export const getCategoryAll = () =>
  request<ApiResult<Category[]>>({ url: '/category/all', method: 'get' })

/** 前台：分类列表 + 已发布文章数（公开） */
export const getFrontCategoryList = () =>
  request<ApiResult<Array<{ id: number; name: string; articleCount: number }>>>({ url: '/category/front/list', method: 'get' })

/** 兼容旧命名：前台分类列表（同 getFrontCategoryList） */
export const getCategoryList = getFrontCategoryList

/** 校验分类名称重复 */
export const checkCategoryName = (name: string, id?: number) =>
  request<ApiResult<boolean>>({ url: '/category/checkName', method: 'get', params: { name, id } })
