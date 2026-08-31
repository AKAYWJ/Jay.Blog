import { request } from './request'
import type { ApiResult } from './request'

/** 评论实体 */
export interface Comment {
  id?: number
  articleId?: number
  userId?: number
  parentId?: number | null
  content?: string
  status?: number
  createTime?: string
  updateTime?: string
}

/** 前台评论树形返回 */
export interface FrontComment {
  id: number
  userId: number
  userName: string
  content: string
  createTime: string
  replyList: Array<{ id: number; userId: number; userName: string; content: string; createTime: string }>
}

/** 前台：文章评论列表（公开） */
export const getFrontCommentList = (params: { pageNum?: number; pageSize?: number; articleId: number }) =>
  request<ApiResult<{ records: FrontComment[]; total: number }>>({ url: '/comment/front/list', method: 'get', params })

/** 前台：发表评论/回复（需登录） */
export const addFrontComment = (data: { articleId: number; parentId?: number | null; content: string }) =>
  request<ApiResult<string>>({ url: '/comment/front/add', method: 'post', data })

/** 前台：删除自己的评论（需登录） */
export const deleteMyComment = (id: number) =>
  request<ApiResult<string>>({ url: `/comment/front/delete/${id}`, method: 'delete' })

/** 后台：分页全量评论（需 comment:list） */
export const getCommentList = (params: { pageNum?: number; pageSize?: number; articleId?: number; status?: number }) =>
  request<ApiResult<{ records: Comment[]; total: number }>>({ url: '/comment/list', method: 'get', params })

/** 后台：单条详情（需 comment:list） */
export const getCommentById = (id: number) =>
  request<ApiResult<Comment>>({ url: `/comment/get/${id}`, method: 'get' })

/** 后台：修改状态/审核（需 comment:audit） */
export const updateComment = (data: Comment) =>
  request<ApiResult<string>>({ url: '/comment/update', method: 'put', data })

/** 后台：删除（需 comment:delete） */
export const deleteComment = (id: number) =>
  request<ApiResult<string>>({ url: `/comment/${id}`, method: 'delete' })

/** 后台：批量删除（需 comment:delete） */
export const batchDeleteComment = (ids: number[]) =>
  request<ApiResult<string>>({ url: '/comment/batch', method: 'delete', data: ids })
