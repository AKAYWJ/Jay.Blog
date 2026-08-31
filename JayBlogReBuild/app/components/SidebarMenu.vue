<template>
  <div class="sidebar-container" :class="{ collapsed: isCollapsed, dark: isDarkMode }">
    <div class="window-controls">
      <span class="control-dot red" v-show="!isCollapsed"></span>
      <span class="control-dot yellow" v-show="!isCollapsed"></span>
      <span class="control-dot green" v-show="!isCollapsed"></span>
    </div>

    <div @click="router.push('/front')" class="logo-section">
      <div class="logo-icon"></div>
      <span class="logo-text" v-if="!isCollapsed">{{ t('admin.menu.management') }}</span>
    </div>

    <div class="menu-scroll-wrap">
      <el-menu :default-active="activeRoute" mode="vertical" :collapse="isCollapsed"
        :collapse-transition="false" background-color="transparent" text-color="#7A8286"
        active-text-color="#5B4FCF" @select="handleMenuSelect">
        <div class="menu-group" v-if="!isCollapsed">ANALYTICS</div>
        <el-menu-item index="/admin" @mouseenter="preloadPage('/admin')">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>{{ t('admin.menu.home') }}</template>
        </el-menu-item>
        <el-menu-item index="/admin/echart" @mouseenter="preloadPage('/admin/echart')">
          <el-icon><TrendCharts /></el-icon>
          <template #title>{{ t('admin.menu.dashboard') }}</template>
        </el-menu-item>

        <div class="menu-group" v-if="!isCollapsed">CONTENT</div>
        <el-menu-item v-for="(item, index) in menu" :key="index" :index="item.path"
          @mouseenter="preloadPage(item.path)">
          <el-icon><Menu /></el-icon>
          <template #title>{{ menuName(item) }}</template>
        </el-menu-item>
      </el-menu>
    </div>

    <div class="sidebar-footer">
      <el-button text @click="toggleCollapse" class="collapse-btn">
        <el-icon><Fold v-if="!isCollapsed" /><Expand v-else /></el-icon>
        <span v-if="!isCollapsed">{{ t('admin.menu.collapse') }}</span>
      </el-button>
      <el-button text @click="toggleDarkMode" class="theme-btn">
        <el-icon><Sunny v-if="isDarkMode" /><Moon v-else /></el-icon>
        <span v-if="!isCollapsed">{{ isDarkMode ? t('common.themeLight') : t('common.themeDark') }}</span>
      </el-button>
      <el-dropdown trigger="click" @command="(c: string) => setLocale(c as any)">
        <el-button text class="theme-btn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <path d="M2 12h20"></path>
            <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
          </svg>
          <span v-if="!isCollapsed">{{ locale === 'zh-CN' ? '中文' : 'EN' }}</span>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="zh-CN" :disabled="locale === 'zh-CN'">简体中文</el-dropdown-item>
            <el-dropdown-item command="en-US" :disabled="locale === 'en-US'">English</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'nuxt/app'
import { useUserStore } from '~/stores/store'
import {
  DataAnalysis, TrendCharts, Menu, Fold, Expand, Sunny, Moon
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { isDark: isDarkMode, toggle: toggleDarkMode } = useDarkMode()
const { locale, setLocale, t } = useI18n()

const isCollapsed = ref(typeof localStorage !== 'undefined' && localStorage.getItem('sidebarCollapsed') === 'true')

// 管理员登录后强制展示完整后台菜单（避免后端 menus 缺少某项时进不了页面）；
// 非管理员（普通用户即使能登录也走不到这里，守卫会拦截）以登录返回的 menus 为准。
const FULL_ADMIN_MENUS = [
  { code: 'article',  path: '/admin/article',  i18n: 'admin.menu.article',  icon: 'Document' },
  { code: 'category', path: '/admin/category', i18n: 'admin.menu.category', icon: 'Folder' },
  { code: 'tag',      path: '/admin/tag',      i18n: 'admin.menu.tag',      icon: 'PriceTag' },
  { code: 'comment',  path: '/admin/comment',  i18n: 'admin.menu.comment',  icon: 'ChatLineRound' },
  { code: 'users',    path: '/admin/users',    i18n: 'admin.menu.users',    icon: 'User' },
  { code: 'roles',    path: '/admin/roles',    i18n: 'admin.menu.roles',    icon: 'UserFilled' },
  { code: 'system',   path: '/admin/system',   i18n: 'admin.menu.system',   icon: 'Setting' }
]

const storeMenus = userStore.getMenus || []
const roleId = userStore.getRoleId
const isAdminRole = String(roleId) === '1' || String(roleId) === '2'
const menu = ref(isAdminRole ? FULL_ADMIN_MENUS : storeMenus)

// 菜单名翻译：优先 i18n key，否则用后端返回的 name
const menuName = (m: any) => m.i18n ? t(m.i18n) : (m.name || '')

const activeRoute = computed(() => route.path)

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
  localStorage.setItem('sidebarCollapsed', isCollapsed.value)
  document.body.classList.toggle('collapsed-sidebar', isCollapsed.value)
}

const toggleDark = () => {
  // 兼容旧名（已废弃，请用 toggleDarkMode）
  toggleDarkMode()
}
if (isCollapsed.value) document.body.classList.add('collapsed-sidebar')

const handleMenuSelect = (index) => {
  let targetPath = index
  if (!targetPath.startsWith('/admin')) {
    targetPath = '/admin/' + targetPath
  }
  if (targetPath !== route.path) {
    router.push(targetPath)
  }
}

// 鼠标悬停菜单时预加载对应路由组件（chunk），
// 用户真正点击时组件已就绪，进入"瞬时切换"的体验。
const preloaded = new Set<string>()
const preloadPage = (path: string) => {
  if (preloaded.has(path)) return
  preloaded.add(path)
  // path 形如 /admin/article -> 异步导入对应 page chunk
  const segments = path.split('/').filter(Boolean) // ['admin', 'article']
  if (segments.length < 2) return
  preloadComponents([`~/pages/${segments.join('/')}.vue`]).catch(() => { })
}

// 进入后台后立即预取全部 7 个业务页面（避免后续点击延迟）
onMounted(() => {
  if (route.path.startsWith('/admin')) {
    preloadComponents([
      '~/pages/admin/article.vue',
      '~/pages/admin/category.vue',
      '~/pages/admin/tag.vue',
      '~/pages/admin/comment.vue',
      '~/pages/admin/users.vue',
      '~/pages/admin/roles.vue',
      '~/pages/admin/system.vue'
    ]).catch(() => { })
  }
})
</script>

<style scoped>
.sidebar-container {
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--color-white);
  border-right: 1px solid var(--color-border);
  display: flex;
  flex-direction: column;
  transition: var(--transition-base);
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  overflow: hidden;
}

.sidebar-container.collapsed {
  width: var(--sidebar-collapsed-width);
}

.sidebar-container.dark {
  background: var(--color-dark-surface);
  border-right-color: var(--color-dark-border);
}

.window-controls {
  display: flex;
  gap: 8px;
  padding: 20px 24px;
  flex-shrink: 0;
}

.control-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.control-dot.red { background: var(--color-danger); }
.control-dot.yellow { background: var(--color-warning); }
.control-dot.green { background: var(--color-success); }

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px 32px;
  flex-shrink: 0;
  cursor: pointer;
}

.logo-icon {
  background-image: url("~/assets/logo.png");
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  width: 48px;
  height: 48px;
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.logo-text {
  font-size: var(--font-size-card-title);
  font-weight: 600;
  color: var(--sidebar-logo-text);
}

.dark .logo-text { color: var(--color-dark-text); }

.menu-scroll-wrap {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.menu-scroll-wrap::-webkit-scrollbar { width: 4px; }
.menu-scroll-wrap::-webkit-scrollbar-track { background: transparent; }
.menu-scroll-wrap::-webkit-scrollbar-thumb { background: #d0d0d0; border-radius: 4px; }
.dark .menu-scroll-wrap::-webkit-scrollbar-thumb { background: #444654; }

.menu-group {
  font-size: var(--font-size-caption);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--color-text-muted);
  padding: 24px 24px 12px;
}

.dark .menu-group { color: var(--color-dark-text-muted); }

:deep(.el-menu) {
  background: transparent;
  padding: 0 12px;
}

:deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin-bottom: 4px;
  transition: var(--transition-fast);
}

:deep(.el-menu-item:hover) {
  background-color: var(--color-bg-hover) !important;
  color: var(--color-primary) !important;
}

:deep(.el-menu-item.is-active) {
  background-color: var(--color-primary-light-bg) !important;
  color: var(--color-primary) !important;
  font-weight: 600;
}

.dark :deep(.el-menu-item) { color: var(--color-dark-text-muted); }
.dark :deep(.el-menu-item:hover) { background-color: var(--color-dark-border) !important; color: #A89FF0 !important; }
.dark :deep(.el-menu-item.is-active) { background-color: var(--color-dark-border) !important; color: #A89FF0 !important; }

:deep(.el-menu-item .el-icon) { margin-right: 12px; font-size: 16px; }

.sidebar-footer {
  display: flex;
  flex-direction: column;
  padding: 20px 12px;
  border-top: 1px solid var(--color-border);
  flex-shrink: 0;
}

.dark .sidebar-footer { border-top-color: var(--color-dark-border); }

.collapse-btn,
.theme-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  border-radius: var(--radius-md);
  margin-bottom: 4px;
  color: var(--color-text-secondary);
}

.collapse-btn:hover,
.theme-btn:hover {
  background-color: var(--color-bg-hover);
  color: var(--color-primary);
}

.dark .collapse-btn,
.dark .theme-btn { color: var(--color-dark-text-muted); }
.dark .collapse-btn:hover,
.dark .theme-btn:hover { background-color: var(--color-dark-border); color: #A89FF0; }
.el-button+.el-button {
    margin-left: 0px;
}
</style>
