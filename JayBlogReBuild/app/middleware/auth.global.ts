import { useUserStore } from '~/stores/store'

/**
 * 全局路由守卫（对应 web-front 的 router.beforeEach）
 * - 管理员/普通登录用户访问 /admin 需登录
 * - 未登录访问后台跳登录页
 * - 非管理员（roleId 不是 1/2）访问后台跳前台
 * - 已登录管理员访问 /login 直接去后台首页
 */
export default defineNuxtRouteMiddleware((to) => {
  // SSR 阶段跳过（token 在客户端 localStorage）
  if (import.meta.server) return

  const store = useUserStore()
  const roleId = store.getRoleId
  const isAdminRole = String(roleId) === '1' || String(roleId) === '2'
  const isAdminRoute = to.path.startsWith('/admin')

  if (to.path === '/login') {
    if (isAdminRole) return navigateTo('/admin')
    return
  }

  if (isAdminRoute) {
    if (!store.isLoggedIn) {
      return navigateTo('/login')
    }
    if (!isAdminRole) {
      return navigateTo('/front')
    }
    return
  }

  return
})
