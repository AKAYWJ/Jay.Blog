import request from './request'

// 分页查询评论
export function getCommentList(params) {
  return request({
    url: '/comment/list',
    method: 'get',
    params
  })
}

//前台评论
export function getFrontCommentList(params) {
  return request({
    url: '/comment/front/list',
    method: 'get',
    params
  })
}

// 根据id获取评论详情
export function getCommentById(id) {
  return request({
    url: `/comment/get/${id}`,
    method: 'get'
  })
}

// 更新评论（审核状态）
export function updateComment(data) {
  return request({
    url: '/comment/update',
    method: 'put',
    data
  })
}

// 删除单条评论
export function deleteComment(id) {
  return request({
    url: `/comment/${id}`,
    method: 'delete'
  })
}

// 批量删除评论
export function batchDeleteComment(ids) {
  return request({
    url: '/comment/batch',
    method: 'delete',
    data: ids
  })
}

// 发表一级评论
export function addComment(data) {
  return request({
    url: '/comment/front/add',
    method: 'post',
    data
  })
}

// 发表回复
export function addReply(data) {
  return request({
    url: '/comment/front/add',
    method: 'post',
    data
  })
}
// 删除自己的评论
export function deleteMyComment(id) {
  return request({
    url: `/comment/front/delete/${id}`,
    method: 'delete'
  })
}

