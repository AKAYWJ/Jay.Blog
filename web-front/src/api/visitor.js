import request from './request'

/**
 * 分页查询访客列表（图表统计总数量依赖此接口）
 * @param {Object} params pageNum,pageSize,ip
 */
export function getVisitorList(params) {
  return request({
    url: '/visitor/list',
    method: 'get',
    params
  })
}

// 记录前台访客
export function addVisitorRecord() {
  return request({
    url: '/visitor/front/add',
    method: 'post'
  })
}

// 获取每日访客折线数据
export function getDailyVisitorData() {
  return request({
    url: '/visitor/front/daily',
    method: 'get'
  })
}
