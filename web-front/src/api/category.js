import request from './request'

// 分页查询分类列表
export function getCategoryAdminList(params) {
  return request({
    url: '/category/list',
    method: 'get',
    params
  })
}

// 获取全部分类（下拉框用，不分页）
export function getCategoryAll() {
  return request({
    url: '/category/all',
    method: 'get'
  })
}

// 分页查询分类列表
export function getCategoryList(params) {
  return request({
    url: '/category/front/list',
    method: 'get',
    params
  })
}

// 新增分类
export function addCategory(data) {
  return request({
    url: '/category/save',
    method: 'post',
    data
  })
}

// 修改分类
export function updateCategory(data) {
  return request({
    url: '/category/update',
    method: 'put',
    data
  })
}

// 删除单个分类
export function deleteCategory(id) {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}

// 批量删除分类
export function batchDeleteCategory(ids) {
  return request({
    url: '/category/batch',
    method: 'delete',
    data: ids
  })
}

// 根据id获取分类详情（编辑回显）
export function getCategoryById(id) {
  return request({
    url: `/category/${id}`,
    method: 'get'
  })
}