<template>
  <div class="layout-container">
    <ClientOnly>
      <SidebarMenu />
      <template #fallback>
        <!-- 占位 sidebar 宽度，避免主内容跳动 -->
        <div class="sidebar-skeleton"></div>
      </template>
    </ClientOnly>
    <div class="main-container">
      <NuxtPage />
    </div>
  </div>
</template>

<script setup>
import SidebarMenu from '~/components/SidebarMenu.vue'
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}

.sidebar-skeleton {
  position: fixed;
  top: 0;
  left: 0;
  width: var(--sidebar-width);
  height: 100vh;
  background: var(--liquid-glass-bg);
  border-right: 1px solid var(--color-border);
  z-index: 1000;
}

.main-container {
  margin-left: var(--sidebar-width);
  padding: var(--spacing-page);
  min-height: 100vh;
  transition: margin-left var(--transition-base);
  background: var(--color-bg);
}

.main-container.collapsed {
  margin-left: var(--sidebar-collapsed-width);
}

body.collapsed-sidebar .main-container {
  margin-left: var(--sidebar-collapsed-width);
}
</style>
