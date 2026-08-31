import { request } from './request'
import type { ApiResult } from './request'

/** 标签实体 */
export interface Tag {
  id?: number
  name?: string
  createTime?: string
  updateTime?: string
}

/** 后台：分页标签（需 tag:list） */
export const getTagList = (params: { pageNum?: number; pageSize?: number; name?: string }) =>
  request<ApiResult<{ records: Tag[]; total: number }>>({ url: '/tag/list', method: 'get', params })

/** 后台：根据ID查询（需 tag:list） */
export const getTagById = (id: number) =>
  request<ApiResult<Tag>>({ url: `/tag/get/${id}`, method: 'get' })

/** 后台：新增（需 tag:add） */
export const addTag = (data: Tag) =>
  request<ApiResult<string>>({ url: '/tag/save', method: 'post', data })

/** 后台：修改（需 tag:edit） */
export const updateTag = (data: Tag) =>
  request<ApiResult<string>>({ url: '/tag/update', method: 'put', data })

/** 后台：删除（需 tag:delete） */
export const deleteTag = (id: number) =>
  request<ApiResult<string>>({ url: `/tag/${id}`, method: 'delete' })

/** 后台：批量删除（需 tag:delete） */
export const batchDeleteTag = (ids: number[]) =>
  request<ApiResult<string>>({ url: '/tag/batch', method: 'delete', data: ids })

/** 全部标签（下拉，公开） */
export const getTagAll = () =>
  request<ApiResult<Tag[]>>({ url: '/tag/all', method: 'get' })

/** 前台：标签列表 + 已发布文章数（公开） */
export const getFrontTagList = () =>
  request<ApiResult<Array<{ id: number; name: string; articleCount: number }>>>({ url: '/tag/front/list', method: 'get' })
