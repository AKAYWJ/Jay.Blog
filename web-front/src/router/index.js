import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

// ========== 全局修复路由报错（重复导航 + 重定向报错）==========
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    // 忽略重复导航、守卫重定向两类正常错误，其他异常正常抛出
    if (err.name === 'NavigationDuplicated' || err.name === 'RedirectedError') return
    return Promise.reject(err)
  })
}

const originalReplace = VueRouter.prototype.replace
VueRouter.prototype.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (err.name === 'NavigationDuplicated' || err.name === 'RedirectedError') return
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
  mode: 'history',
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

export function loadAsyncMenus() {
  const menuList = store.getters.getMenus
  if (menuList.length === 0) return
  const adminLayout = router.options.routes.find(r => r.path === '/admin')
  if (!adminLayout) return
  const asyncRoutes = generateAsyncRoutes(menuList)
  // 追加到父路由children，维持嵌套结构，Layout内部router-view渲染页面
  adminLayout.children = [...adminLayout.children, ...asyncRoutes]
  // 只重新注册父路由
  router.addRoute(adminLayout)
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