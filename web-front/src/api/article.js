import request from './request'

/**
 * 分页查询文章列表
 * @param {Object} params pageNum,pageSize,title,categoryId,status
 */
export function getArticleList(params) {
  return request({
    url: '/article/list',
    method: 'get',
    params
  })
}

/**
 * 新增文章
 * @param {Object} data 文章表单
 */
export function addArticle(data) {
  return request({
    url: '/article/save',
    method: 'post',
    data
  })
}

/**
 * 编辑更新文章
 * @param {Object} data 文章表单
 */
export function updateArticle(data) {
  return request({
    url: '/article/update',
    method: 'put',
    data
  })
}

/**
 * 删除单篇文章
 * @param {Number} id 文章id
 */
export function deleteArticle(id) {
  return request({
    url: `/article/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除文章
 * @param {Array} ids id数组
 */
export function batchDeleteArticle(ids) {
  return request({
    url: '/article/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 根据id获取文章详情（编辑回显）
 * @param {Number} id
 */
export function getArticleById(id) {
  return request({
    url: `/article/front/detail/${id}`,
    method: 'get'
  })
}

// 获取点赞状态
export function getLikeStatus(articleId) {
  return request({
    url: '/article/front/like/status',
    method: 'get',
    params: { articleId }
  })
}

// 点赞/取消点赞
export function likeArticle(data) {
  return request({
    url: '/article/front/like',
    method: 'post',
    data: data
  })
}