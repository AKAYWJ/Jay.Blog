import axios from 'axios'
import { Message, MessageBox, Loading } from 'element-ui'
import qs from 'qs'
import store from '../store' // ✅ 新增：导入Vuex store

// 创建Axios实例
const service = axios.create({
  baseURL: (process.env.VUE_APP_BASE_API || '') + '/api',
  timeout: 15000,
  withCredentials: true // 如果后端不需要携带Cookie可以删掉
})

// 全局loading计数器（防止多个请求同时发起时loading闪烁）
let loadingInstance = null
let loadingCount = 0

function showLoading() {
  if (loadingCount === 0) {
    loadingInstance = Loading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
  }
  loadingCount++
}

function hideLoading() {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0 // 防止负数
    loadingInstance.close()
  }
}

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 不需要loading的请求添加：headers: { noLoading: true }
    if (!config.headers.noLoading) {
      showLoading()
    }

    // ✅ 修正：从Vuex统一获取Token（和路由守卫保持一致）
    const token = store.getters.getToken
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 默认请求头（不会覆盖FormData等特殊格式的自动设置）
    config.headers['Content-Type'] = config.headers['Content-Type'] || 'application/json'

    // POST表单数据序列化
    if (config.method === 'post' && config.headers['Content-Type'] === 'application/x-www-form-urlencoded') {
      config.data = qs.stringify(config.data)
    }

    // GET请求添加时间戳防止IE缓存
    if (config.method === 'get') {
      config.params = { ...config.params, _t: Date.now() }
    }

    return config
  },
  error => {
    hideLoading()
    Message.error('请求发送失败，请检查网络连接')
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data

    // 后端统一返回格式：{ code: 200, data: {}, message: 'success' }
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')

      // 401：Token过期/无效
      if (res.code === 401) {
        MessageBox.confirm('登录已过期，请重新登录', '系统提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('logout')
        })
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    hideLoading()
    let msg = '网络连接失败，请稍后重试'

    if (error.message.includes('timeout')) {
      msg = '请求超时，请检查网络连接'
    } else if (error.response) {
      switch (error.response.status) {
        case 404: msg = '请求的接口不存在'; break
        case 500: msg = '服务器内部错误'; break
        case 403: msg = '权限不足，无法访问'; break
      }
    }

    Message.error(msg)
    return Promise.reject(error)
  }
)

export default service