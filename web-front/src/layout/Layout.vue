<!-- src/layout/Layout.vue -->
<template>
  <div class="layout-container">
    <!-- 接收子组件的状态同步 -->
    <SidebarMenu @update:collapse="handleCollapseChange" />

    <!-- 主内容区：实时跟随侧边栏状态变宽 -->
    <div class="main-container" :class="{ collapsed: isCollapsed }">
      <router-view />
    </div>
  </div>
</template>

<script>
import SidebarMenu from '@/components/SidebarMenu.vue'

export default {
  name: 'Layout',
  components: { SidebarMenu },
  data() {
    return {
      // 初始化读取本地状态
      isCollapsed: localStorage.getItem('sidebarCollapsed') === 'true'
    }
  },
  methods: {
    // 接收子组件的折叠状态，实时更新
    handleCollapseChange(val) {
      this.isCollapsed = val
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.main-container {
  margin-left: var(--sidebar-width);
  padding: var(--spacing-page);
  min-height: 100vh;
  transition: margin-left var(--transition-base);
  background: var(--color-bg);
}

/* 侧边栏收起 → 右侧自动变宽 */
.main-container.collapsed {
  margin-left: var(--sidebar-collapsed-width);
}
</style>