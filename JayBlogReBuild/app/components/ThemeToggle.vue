<template>
  <ClientOnly>
    <button
      class="theme-toggle"
      :title="isDark ? t('common.themeDark') : t('common.themeLight')"
      @click="toggle"
    >
      <!-- 太阳（亮色模式 → 显示，点击切暗） -->
      <svg v-if="!isDark" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
        stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="4"></circle>
        <path d="M12 2v2"></path>
        <path d="M12 20v2"></path>
        <path d="m4.93 4.93 1.41 1.41"></path>
        <path d="m17.66 17.66 1.41 1.41"></path>
        <path d="M2 12h2"></path>
        <path d="M20 12h2"></path>
        <path d="m6.34 17.66-1.41 1.41"></path>
        <path d="m19.07 4.93-1.41 1.41"></path>
      </svg>
      <!-- 月亮（暗色模式 → 显示，点击切亮） -->
      <svg v-else width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
        stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
      </svg>
    </button>
    <template #fallback>
      <div class="theme-toggle" style="opacity: 0"></div>
    </template>
  </ClientOnly>
</template>

<script setup lang="ts">
const { isDark, toggle } = useDarkMode()
const { t } = useI18n()
</script>

<style scoped>
.theme-toggle {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid transparent;
  border-radius: 50%;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.18s ease;
  padding: 0;
}

.theme-toggle:hover {
  background: var(--color-primary-light-bg);
  color: var(--color-primary);
  border-color: var(--color-border);
}

.theme-toggle:active {
  transform: scale(0.92);
}

.theme-toggle svg {
  transition: transform 0.3s ease;
}

.theme-toggle:hover svg {
  transform: rotate(15deg);
}
</style>
