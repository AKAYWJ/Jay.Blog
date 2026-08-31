import { request } from './request'
import type { ApiResult } from './request'

/** 后台首页聚合数据 */
export interface HomeStats {
  recentArticles: Array<{
    id: number
    title: string
    status: number
    createTime: string
    updateTime?: string
  }>
  recentComments: Array<{
    id: number
    articleId: number
    content: string
    status: number
    createTime: string
  }>
  articleTotal: number
  commentTotal: number
}

/** 后台首页统计 + 最近活动（一次拿到，替代 4 个并发请求） */
export const getHomeStats = () =>
  request<ApiResult<HomeStats>>({ url: '/admin/home/stats', method: 'get' })

/** 当月日历事件日期 */
export const getMonthEvents = (year: number, month: number) =>
  request<ApiResult<{ articleDates: string[]; commentDates: string[] }>>({
    url: '/admin/home/events',
    method: 'get',
    params: { year, month }
  })
