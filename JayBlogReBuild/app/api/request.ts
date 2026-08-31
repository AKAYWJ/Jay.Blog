/**
 * 统一请求封装
 *
 * 使用 ~/plugins/fetch-interceptor 导出的 apiFetch 实例（带拦截器）
 * - onRequest: 自动注入 Authorization（从 Pinia store 读 token）
 * - onResponse: 业务 code === 401 时弹窗 + 退出登录
 *
 * SSR 端直接用全局 $fetch（无 token、无弹窗）
 */

import { apiFetch } from '~/plugins/fetch-interceptor'

/** 后端统一响应结构 */
export interface ApiResult<T = any> {
  code: number
  msg: string
  data: T
}

/** 统一请求入口 */
export const request = <T = any>(config: {
  url: string
  method?: 'get' | 'post' | 'put' | 'delete'
  params?: Record<string, any>
  data?: any
  headers?: Record<string, string>
}): Promise<T> => {
  const runtimeConfig = useRuntimeConfig()
  // 客户端：走 /api（被 nitro devProxy / 生产 nginx 转发到后端）
  // SSR：直接打后端完整地址（Node 端没有 nitro devProxy）
  const baseURL = import.meta.client
    ? ((runtimeConfig.public.apiPrefix as string) || '/api')
    : ((runtimeConfig.public.apiBase as string) || 'http://localhost:8086')

  // 客户端用 apiFetch（带拦截器），SSR 用全局 $fetch
  const fetchInstance = import.meta.client ? apiFetch : $fetch

  return fetchInstance<T>(config.url, {
    baseURL,
    method: config.method || 'get',
    params: config.params,
    body: config.data,
    headers: config.headers
  }) as Promise<T>
}
