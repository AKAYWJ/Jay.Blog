// 根路径重定向到前台首页
export default defineNuxtRouteMiddleware(() => {
  return navigateTo('/front', { redirectCode: 302 })
})
