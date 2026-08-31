import { request } from './request'
import type { ApiResult } from './request'

/** 获取图形验证码 */
export const getCaptcha = () =>
  request<ApiResult<{ id: string; svg: string }>>({ url: '/auth/captcha', method: 'get' })
