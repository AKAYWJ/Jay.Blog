// src/api/auth.js
import request from './request'

/**
 * 用户登录
 * @param {Object} data - 登录参数 { account: '用户名/手机号', password: '密码' }
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册参数 
 * { name: '用户名', password: '密码', phone: '手机号', age: 年龄, sex: '性别' }
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getauthInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

//+++++++++++++++++++++++++++ User ++++++++++++++++++++++++++++++
/**
 * 分页查询用户列表
 * @param {Object} params { pageNum, pageSize, name, phone, roleId }
 */
export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

/**
 * 新增用户
 */
export function addUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

/**
 * 修改用户（后台管理，需 user:edit 权限）
 */
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

/**
 * 前台：修改自己的资料（仅需登录，不能改角色/手机号等）
 */
export function updateSelf(data) {
  return request({
    url: '/user/self/update',
    method: 'put',
    data
  })
}

/**
 * 根据ID删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/user/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除用户
 */
export function deleteBatchUsers(ids) {
  return request({
    url: '/user/deleteBatch',
    method: 'delete',
    data: ids
  })
}