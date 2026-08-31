import { request } from './request'
import type { ApiResult } from './request'

/** 访客实体 */
export interface Visitor {
  id?: number
  ip?: string
  address?: string
  visitTime?: string
}

/** 后台：访客列表（需 visitor:list） */
export const getVisitorList = (params: { pageNum?: number; pageSize?: number; ip?: string }) =>
  request<ApiResult<{ records: Visitor[]; total: number }>>({ url: '/visitor/list', method: 'get', params })

/** 前台：记录访客（公开，同一IP同一天一条） */
export const addVisitorRecord = () =>
  request<ApiResult<string>>({ url: '/visitor/front/add', method: 'post' })

/** 每日访客折线数据 */
export const getDailyVisitorData = () =>
  request<ApiResult<Array<{ day: string; count: number }>>>({ url: '/visitor/front/daily', method: 'get' })
