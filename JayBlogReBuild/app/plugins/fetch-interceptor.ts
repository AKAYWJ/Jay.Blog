import { ElMessageBox } from 'element-plus'
import { useUserStore } from '~/stores/store'

/**
 * 全局 fetch 拦截器（提供 apiFetch 实例供业务层使用）
 *
 * 由于 Nuxt 全局 $fetch 一旦替换会破坏 SSR 行为，
 * 我们创建一个独立的 apiFetch 实例，所有业务请求走这个实例。
 * apiFetch 自动注入 Authorization，业务 code 401 自动弹窗。
 */

let isReloginShowing = false

// 模块顶层导出：业务层用 import { apiFetch } from '~/plugins/fetch-interceptor'
export const apiFetch = $fetch.create({
  onRequest({ options }) {
    if (typeof window === 'undefined') return
    try {
      const userStore = useUserStore()
      const token = userStore.token
      if (token) {
        options.headers = new Headers(options.headers || {})
        options.headers.set('Authorization', `Bearer ${token}`)
      }
    } catch {
      // store 未初始化时忽略
    }
  },
  onResponse({ response }) {
    const body: any = response?._data
    if (body && typeof body === 'object' && body.code === 401 && typeof window !== 'undefined') {
      handle401(body.msg || '请先登录')
    }
  },
  onResponseError({ response }) {
    if (response?.status === 401 && typeof window !== 'undefined') {
      const body: any = response?._data
      const msg = (body && body.msg) || '请先登录'
      handle401(msg)
    }
  }
})

async function handle401(msg: string) {
  if (isReloginShowing) return
  isReloginShowing = true
  try {
    await ElMessageBox.confirm(msg + '，是否前往登录？', '提示', {
      confirmButtonText: '去登录',
      cancelButtonText: '取消',
      type: 'warning'
    })
    try {
      const userStore = useUserStore()
      userStore.clearUserState()
    } catch { /* ignore */ }
    await navigateTo('/login')
  } catch {
    // 用户取消
  } finally {
    isReloginShowing = false
  }
}

export default defineNuxtPlugin(() => {
  // 该插件仅用于注册副作用（如有），不需要额外操作
})
