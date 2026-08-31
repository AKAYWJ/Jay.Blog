<template>
  <ClientOnly>
    <el-dropdown trigger="click" @command="onSelect">
      <button class="lang-switch" :title="t('common.language')">
        <!-- 地球 SVG -->
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
          stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <path d="M2 12h20"></path>
          <path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"></path>
        </svg>
        <span class="lang-label">{{ locale === 'zh-CN' ? '中' : 'EN' }}</span>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="zh-CN" :disabled="locale === 'zh-CN'">
            <span class="lang-item">
              <span class="lang-flag">中</span>
              <span>简体中文</span>
              <svg v-if="locale === 'zh-CN'" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </span>
          </el-dropdown-item>
          <el-dropdown-item command="en-US" :disabled="locale === 'en-US'">
            <span class="lang-item">
              <span class="lang-flag">EN</span>
              <span>English</span>
              <svg v-if="locale === 'en-US'" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <template #fallback>
      <div class="lang-switch" style="opacity: 0">
        <svg width="18" height="18"><circle cx="12" cy="12" r="10"></circle></svg>
      </div>
    </template>
  </ClientOnly>
</template>

<script setup lang="ts">
import type { Locale } from '~/composables/useI18n'

const { locale, setLocale, t } = useI18n()

const onSelect = (cmd: string) => {
  setLocale(cmd as Locale)
}
</script>

<style scoped>
.lang-switch {
  height: 34px;
  padding: 0 10px;
  display: flex;
  align-items: center;
  gap: 5px;
  border: 1px solid var(--color-border);
  border-radius: 999px;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  transition: all 0.18s ease;
  font-size: 13px;
  font-weight: 500;
}

.lang-switch:hover {
  background: var(--color-primary-light-bg);
  color: var(--color-primary);
  border-color: var(--color-primary-light);
}

.lang-switch:active {
  transform: scale(0.96);
}

.lang-label {
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: 0.5px;
}

.lang-item {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 120px;
}

.lang-flag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 22px;
  height: 22px;
  border-radius: 4px;
  background: var(--color-primary-light-bg);
  color: var(--color-primary);
  font-size: 11px;
  font-weight: 600;
  font-family: 'JetBrains Mono', monospace;
}

/* dropdown 颜色覆盖：去掉 element-plus 的彩色 */
:deep(.el-dropdown-menu__item:not(.is-disabled):hover) {
  background: var(--color-primary-light-bg);
  color: var(--color-primary);
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: var(--color-primary);
  font-weight: 500;
}
</style>
