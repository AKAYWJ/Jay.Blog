import { request } from './request'
import type { ApiResult } from './request'

/** 博客全局配置 */
export interface BlogConfig {
  id?: number
  blogName?: string
  blogAvatar?: string
  blogSignature?: string
  aboutMe?: string
  github?: string
  qq?: string
  email?: string
  allowComment?: number
  createTime?: string
  updateTime?: string
}

/** 全站统计 */
export interface BlogStat {
  articleTotal: number
  visitorTotal: number
  commentTotal: number
}

/** 获取博客配置（公开） */
export const getBlogConfig = () =>
  request<ApiResult<BlogConfig>>({ url: '/blogConfig/get', method: 'get' })

/** 修改博客配置（需 config:edit） */
export const updateBlogConfig = (data: BlogConfig) =>
  request<ApiResult<string>>({ url: '/blogConfig/update', method: 'put', data })

/** 全站统计（公开） */
export const getBlogStat = () =>
  request<ApiResult<BlogStat>>({ url: '/stat/front', method: 'get' })
