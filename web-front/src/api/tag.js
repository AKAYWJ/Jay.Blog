import request from './request'

// 分页查询标签
export function getTagList(params) {
  return request({
    url: '/tag/list',
    method: 'get',
    params
  })
}

// 新增标签
export function addTag(data) {
  return request({
    url: '/tag/save',
    method: 'post',
    data
  })
}

// 编辑标签
export function updateTag(data) {
  return request({
    url: '/tag/update',
    method: 'put',
    data
  })
}

// 删除单个标签
export function deleteTag(id) {
  return request({
    url: `/tag/${id}`,
    method: 'delete'
  })
}

// 批量删除标签
export function batchDeleteTag(ids) {
  return request({
    url: '/tag/batch',
    method: 'delete',
    data: ids
  })
}

// 获取全部标签（下拉使用）
export function getTagAll() {
  return request({
    url: '/tag/all',
    method: 'get'
  })
}