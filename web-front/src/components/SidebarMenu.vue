<template>
  <div class="sidebar-container" :class="{ collapsed: isCollapsed, dark: isDarkMode }">
    <!-- 窗口控制条 -->
    <div class="window-controls">
      <span class="control-dot red" v-show="!isCollapsed"></span>
      <span class="control-dot yellow" v-show="!isCollapsed"></span>
      <span class="control-dot green" v-show="!isCollapsed"></span>
    </div>

    <!-- Logo区域 -->
    <div @click="$router.push('/front')" class="logo-section">
      <div class="logo-icon"></div>
      <span class="logo-text" v-if="!isCollapsed">管理系统</span>
    </div>

    <!-- 菜单滚动容器：核心新增 -->
    <div class="menu-scroll-wrap">
      <el-menu :default-active="activeRoute" mode="vertical" :collapse="isCollapsed" :collapse-transition="false"
        background-color="transparent" text-color="#7A8286" active-text-color="#5B4FCF" @select="handleMenuSelect">
        <!-- 第一组：数据分析 -->
        <div class="menu-group" v-if="!isCollapsed">ANALYTICS</div>
        <el-menu-item index="/admin/home">
          <i class="el-icon-menu"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/echart">
          <i class="el-icon-data-line"></i>
          <span slot="title">仪表盘</span>
        </el-menu-item>

        <!-- 第二组：内容管理 -->
        <div class="menu-group" v-if="!isCollapsed">CONTENT</div>
        <el-menu-item v-for="(item, index) in menu" :index='item.path' :key="index">
          <i :class="item.icon"></i>
          <span slot="title">{{ item.name }}</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 底部操作区：固定在底部不滚动 -->
    <div class="sidebar-footer">
      <el-button type="text" icon="el-icon-s-fold" @click="toggleCollapse" class="collapse-btn">
        <span v-if="!isCollapsed">收起菜单</span>
      </el-button>
      <el-button type="text" :icon="isDarkMode ? 'el-icon-sunny' : 'el-icon-moon'" @click="isDarkMode = !isDarkMode"
        class="theme-btn">
        <span v-if="!isCollapsed">{{ isDarkMode ? '浅色模式' : '深色模式' }}</span>
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SidebarMenu',
  data() {
    return {
      isCollapsed: localStorage.getItem('sidebarCollapsed') === 'true',
      isDarkMode: localStorage.getItem('darkMode') === 'true',
      menu: []
    }
  },
  computed: {
    activeRoute() {
      return this.$route.path
    }
  },
  watch: {
    isCollapsed(val) {
      localStorage.setItem('sidebarCollapsed', val)
      this.$emit('update:collapse', val)
    },
    isDarkMode(val) {
      localStorage.setItem('darkMode', val)
      val ? document.body.classList.add('dark-mode') : document.body.classList.remove('dark-mode')
    }
  },
  created() {
    if (this.isDarkMode) document.body.classList.add('dark-mode')
    this.menu = this.$store.getters.getMenus;
  },
  methods: {
    handleMenuSelect(index) {
      let targetPath = index
      if (!targetPath.startsWith('/admin')) {
        targetPath = '/admin/' + targetPath
      }
      if (targetPath !== this.$route.path) {
        this.$router.push(targetPath)
      }
    },
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed
    }
  }
}
</script>

<style scoped>
/* 侧边栏容器 */
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
  /* 隐藏容器滚动，交给内部滚动容器 */
}

/* 折叠状态 */
.sidebar-container.collapsed {
  width: var(--sidebar-collapsed-width);
}

/* 暗黑模式 */
.sidebar-container.dark {
  background: var(--color-dark-surface);
  border-right-color: var(--color-dark-border);
}

/* 窗口装饰条 */
.window-controls {
  display: flex;
  gap: 8px;
  padding: 20px 24px;
  flex-shrink: 0;
  /* 禁止压缩 */
}

.control-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.control-dot.red {
  background: var(--color-danger);
}

.control-dot.yellow {
  background: var(--color-warning);
}

.control-dot.green {
  background: var(--color-success);
}

/* Logo区域 */
.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 24px 32px;
  flex-shrink: 0;
  /* 禁止压缩 */
}

.logo-icon {
  background-image: url("../assets/logo.png");
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

.dark .logo-text {
  color: var(--color-dark-text);
}

/* ========== 新增：菜单滚动容器 ========== */
.menu-scroll-wrap {
  flex: 1;
  /* 自动占满剩余高度 */
  overflow-y: auto;
  /* 垂直滚动 */
  overflow-x: hidden;
  /* 禁止横向滚动 */
  /* 美化滚动条（浅色） */
}

.menu-scroll-wrap::-webkit-scrollbar {
  width: 4px;
}

.menu-scroll-wrap::-webkit-scrollbar-track {
  background: transparent;
}

.menu-scroll-wrap::-webkit-scrollbar-thumb {
  background: #d0d0d0;
  border-radius: 4px;
}

/* 暗黑滚动条 */
.dark .menu-scroll-wrap::-webkit-scrollbar-thumb {
  background: #444654;
}

/* 菜单分组标题 */
.menu-group {
  font-size: var(--font-size-caption);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--color-text-muted);
  padding: 24px 24px 12px;
}

.dark .menu-group {
  color: var(--color-dark-text-muted);
}

/* 菜单样式 */
/deep/ .el-menu {
  background: transparent;
  padding: 0 12px;
}

/deep/ .el-menu-item {
  height: 48px;
  line-height: 48px;
  margin-bottom: 4px;
  transition: var(--transition-fast);
}

/deep/ .el-menu-item:hover {
  background-color: var(--color-bg-hover) !important;
  color: var(--color-primary) !important;
}

/deep/ .el-menu-item.is-active {
  background-color: var(--color-primary-light-bg) !important;
  color: var(--color-primary) !important;
  font-weight: 600;
}

/* 暗黑模式菜单样式 */
.dark /deep/ .el-menu-item {
  color: var(--color-dark-text-muted);
}

.dark /deep/ .el-menu-item:hover {
  background-color: var(--color-dark-border) !important;
  color: #A89FF0 !important;
}

.dark /deep/ .el-menu-item.is-active {
  background-color: var(--color-dark-border) !important;
  color: #A89FF0 !important;
}

/* 菜单图标 */
/deep/ .el-menu-item [class^="el-icon-"] {
  margin-right: 12px;
  font-size: var(--font-size-card-title);
}

.collapsed /deep/ .el-menu-item [class^="el-icon-"] {
  margin-right: 0;
  text-align: center;
  width: 100%;
}

/* 底部操作区：flex-shrink:0 固定高度不压缩 */
.sidebar-footer {
  display: flex;
  flex-direction: column;
  padding: 20px 12px;
  border-top: 1px solid var(--color-border);
  flex-shrink: 0;
}

.dark .sidebar-footer {
  border-top-color: var(--color-dark-border);
}

.collapse-btn,
.theme-btn {
  text-align: center;
  height: 40px;
  align-items: center;
  justify-content: center;
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
.dark .theme-btn {
  color: var(--color-dark-text-muted);
}

.dark .collapse-btn:hover,
.dark .theme-btn:hover {
  background-color: var(--color-dark-border);
  color: #A89FF0;
}
</style>