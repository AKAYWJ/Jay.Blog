import { request } from './request'
import type { ApiResult } from './request'

/** 文章实体（含视图字段） */
export interface Article {
  id?: number
  userId?: number
  categoryId?: number
  tagIds?: string
  title?: string
  summary?: string
  cover?: string
  contentMd?: string
  contentHtml?: string
  viewCount?: number
  likeCount?: number
  isTop?: number
  status?: number
  createTime?: string
  updateTime?: string
  categoryName?: string
  tagIdsStr?: string
  tagNamesStr?: string
  tagIdList?: number[]
  tagNameList?: string[]
}

/** 文章列表分页参数 */
export interface ArticleListParams {
  pageNum?: number
  pageSize?: number
  title?: string
  categoryId?: number
  status?: number
}

/** 后台：分页文章（需 article:list） */
export const getArticleList = (params: ArticleListParams) =>
  request<ApiResult<{ records: Article[]; total: number }>>({ url: '/article/list', method: 'get', params })

/** 后台：根据ID查询（需 article:list） */
export const getArticleById = (id: number) =>
  request<ApiResult<Article>>({ url: `/article/${id}`, method: 'get' })

/** 后台：新增（需 article:add） */
export const addArticle = (data: Article) =>
  request<ApiResult<string>>({ url: '/article/save', method: 'post', data })

/** 后台：修改（需 article:edit） */
export const updateArticle = (data: Article) =>
  request<ApiResult<string>>({ url: '/article/update', method: 'put', data })

/** 后台：删除（需 article:delete） */
export const deleteArticle = (id: number) =>
  request<ApiResult<string>>({ url: `/article/${id}`, method: 'delete' })

/** 后台：批量删除（需 article:batchDelete） */
export const batchDeleteArticle = (ids: number[]) =>
  request<ApiResult<string>>({ url: '/article/batch', method: 'delete', data: ids })

/** 前台：文章详情（浏览量自增，公开） */
export const getFrontArticleDetail = (id: number | string) =>
  request<ApiResult<Article>>({ url: `/article/front/detail/${id}`, method: 'get' })

/** 前台：分页文章（仅已发布，公开） */
export const getFrontArticleList = (params: ArticleListParams) =>
  request<ApiResult<{ records: Article[]; total: number }>>({ url: '/article/front/list', method: 'get', params })

/** 前台：热门文章（浏览量前2，公开） */
export const getHotArticle = () =>
  request<ApiResult<Article[]>>({ url: '/article/front/hot', method: 'get' })

/** 前台：点赞状态（需登录才会返回真实 isLiked） */
export const getLikeStatus = (articleId: number) =>
  request<ApiResult<{ likeCount: number; isLiked: boolean }>>({ url: '/article/front/like/status', method: 'get', params: { articleId } })

/** 前台：点赞/取消点赞（需登录） */
export const likeArticle = (data: { articleId: number; isLike: boolean }) =>
  request<ApiResult<{ likeCount: number; isLiked: boolean }>>({ url: '/article/front/like', method: 'post', data })
