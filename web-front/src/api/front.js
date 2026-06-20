import request from './request'

// 首页分页文章（仅已发布）
export function getFrontArticleList(params) {
  return request({
    url: '/article/front/list',
    method: 'get',
    params
  })
}

// 获取全部分类（附带分类下文章数量）
export function getFrontCategory() {
  return request({
    url: '/category/all',
    method: 'get'
  })
}

// 热门文章TOP8（按浏览量排序）
export function getHotArticle() {
  return request({
    url: '/article/front/hot',
    method: 'get'
  })
}

// 全站统计：文章、访客、评论总数
export function getBlogStat() {
  return request({
    url: '/stat/front',
    method: 'get'
  })
}

// 站点基础配置（关于、QQ、Github）
export function getBlogConfig() {
  return request({
    url: '/blogConfig/get',
    method: 'get'
  })
}
// 修改博客配置
export function updateBlogConfig(data) {
  return request({
    url: '/blogConfig/update',
    method: 'put',
    data
  })
}

// 文章详情（浏览量自增）
export function getArticleDetail(id) {
  return request({
    url: `/article/${id}`,
    method: 'get'
  })
}

// 发表评论
export function addComment(data) {
  return request({
    url: '/comment/add',
    method: 'post',
    data
  })
}

// 获取文章评论（仅审核通过）
export function getArticleComment(articleId) {
  return request({
    url: '/comment/list',
    method: 'get',
    params: { articleId }
  })
}