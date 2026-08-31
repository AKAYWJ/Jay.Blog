import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

// ========== 全局修复路由报错（重复导航 + 重定向报错）==========
// 路由错误判定：NavigationDuplicated 有专属 name；Redirected 在 vue-router3 里是普通 Error，
// 需要通过 _isRouter 标记 + 消息内容判断
function isIgnoredRouterError(err) {
  if (!err) return false
  if (err.name === 'NavigationDuplicated') return true
  if (err._isRouter && /Redirected when going from/.test(err.message || '')) return true
  return false
}

const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (isIgnoredRouterError(err)) return
    return Promise.reject(err)
  })
}

const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (isIgnoredRouterError(err)) return
    return Promise.reject(err)
  })
}

const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/admin/home',
    children: [
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('@/views/HomeView.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'echart',
        name: 'AdminEchart',
        component: () => import('@/views/Echart.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/front',
    name: 'FrontLayout',
    component: () => import('@/views/front/FrontLayout.vue'),
    redirect: '/front/index',
    children: [
      {
        path: 'index',
        name: 'FrontIndex',
        component: () => import('@/views/front/Index.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'category/all',
        name: 'FrontCategoryAll',
        component: () => import('@/views/front/Category.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'category',
        name: 'FrontCategory',
        component: () => import('@/views/front/Category.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'about',
        name: 'FrontAbout',
        component: () => import('@/views/front/About.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'profile',
        name: 'FrontProfile',
        component: () => import('@/views/front/Profile.vue'),
        meta: { requiresAuth: false }
      },
      {
        path: 'timeline',
        name: 'TimeLine',
        component: () => import('@/views/front/TimeLine.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/article',
    name: 'FrontArticle',
    component: () => import('@/views/front/ArticleDetail.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/articlelist',
    name: 'Articlelist',
    component: () => import('@/views/front/ArticleList.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/front/index'
  },
  { path: '*', redirect: '/front/index' }
]

const router = new VueRouter({
  // mode: 'history',
  mode: 'hash',
  base: process.env.BASE_URL,
  routes: constantRoutes
})

// 动态路由单独函数保留，但守卫不再自动加载
function generateAsyncRoutes(menuList) {
  return menuList
    .filter(item => item.type === 'M' && item.isValid === 'Y')
    .map(menu => ({
      // 只写后半段，不要 /admin
      path: menu.path,
      name: menu.component,
      component: () => import(`@/views/${menu.component}.vue`),
      meta: {
        requiresAuth: true,
        permissionCode: menu.code,
        menuName: menu.name,
        icon: menu.icon
      }
    }))
}

let asyncRoutesLoaded = false

export function loadAsyncMenus() {
  if (asyncRoutesLoaded) return false
  const menuList = store.getters.getMenus
  if (menuList.length === 0) return false
  const adminLayout = router.options.routes.find(r => r.path === '/admin')
  if (!adminLayout) return false
  const asyncRoutes = generateAsyncRoutes(menuList)
  adminLayout.children = [...adminLayout.children, ...asyncRoutes]
  router.addRoute(adminLayout)
  asyncRoutesLoaded = true
  return true
}

router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters.isLoggedIn
  const roleId = store.getters.getRoleId || ''
  // ========== 修复：统一转字符串判断，兼容数字/字符串两种存储格式 ==========
  const isAdminRole = String(roleId) === '1' || String(roleId) === '2'
  const isAdminRoute = to.path.startsWith('/admin')

  // 管理员：所有页面全部放行
  if (isAdminRole) {
    if (to.path === '/login') {
      next('/admin/home')
      return
    }
    // 刷新页面后动态路由会丢失：仅当目标路由未匹配（说明动态路由丢了）时，
    // 补注册并重走到目标路由；目标本就存在（如 /admin/home）则直接放行，避免多余重定向
    if (isAdminRoute && to.matched.length === 0 && loadAsyncMenus()) {
      next({ path: to.path, query: to.query, replace: true })
      return
    }
    next()
    return
  }

  // 普通用户：拦截所有后台路径，强制跳转前台首页
  if (isAdminRoute) {
    next('/front/index')
  } else {
    next()
  }
})

export default router