<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">{{ error.statusCode }}</div>
      <h1 class="error-title">{{ title }}</h1>
      <p class="error-message">{{ message }}</p>

      <div class="error-actions">
        <button class="primary-btn" @click="handleBack">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
            stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <line x1="19" y1="12" x2="5" y2="12"></line>
            <polyline points="12 19 5 12 12 5"></polyline>
          </svg>
          {{ t('error.back') }}
        </button>
        <button class="secondary-btn" @click="handleHome">{{ t('error.home') }}</button>
      </div>

      <p v-if="countdown > 0" class="countdown">
        {{ t('error.countdown', { count: countdown }) }}
      </p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { clearError, navigateTo } from 'nuxt/app'
import { useI18n } from '~/composables/useI18n'

const { t } = useI18n()

const props = defineProps({ error: Object })

const countdown = ref(5)
let timer: ReturnType<typeof setInterval> | null = null

const title = computed(() => {
  if (props.error.statusCode === 404) return t('error.title.404')
  if (props.error.statusCode === 500) return t('error.title.500')
  return t('error.title.default')
})

const message = computed(() => {
  if (props.error.statusCode === 404) return t('error.message.404')
  if (props.error.statusCode === 500) return t('error.message.500')
  return props.error.message || t('error.message.default')
})

const handleHome = () => {
  stopCountdown()
  clearError({ redirect: '/' })
}

const handleBack = () => {
  stopCountdown()
  // SSR 端无 history，用 navigateTo('/')
  if (import.meta.server) {
    clearError({ redirect: '/' })
  } else {
    if (window.history.length > 1) {
      clearError()
      window.history.back()
    } else {
      clearError({ redirect: '/' })
    }
  }
}

const stopCountdown = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

const startCountdown = () => {
  // 倒计时结束自动返回上一页
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      stopCountdown()
      handleBack()
    }
  }, 1000)
}

onMounted(() => {
  // 只在 404 时启动倒计时（其他错误不自动跳）
  if (props.error.statusCode === 404) {
    startCountdown()
  }
})

onBeforeUnmount(() => {
  stopCountdown()
})
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  padding: 40px 20px;
}

.error-content {
  text-align: center;
  max-width: 480px;
}

.error-code {
  font-size: 120px;
  font-weight: 800;
  line-height: 1;
  color: var(--color-text-primary);
  margin-bottom: 16px;
  letter-spacing: -4px;
  font-feature-settings: 'tnum';
}

.error-title {
  font-size: 28px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin: 0 0 12px;
  letter-spacing: -0.5px;
}

.error-message {
  font-size: 15px;
  color: var(--color-text-muted);
  margin: 0 0 32px;
  line-height: 1.6;
}

.error-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.primary-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 11px 24px;
  background: var(--color-text-primary);
  color: var(--color-bg);
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.15s ease;
}

.primary-btn:hover {
  opacity: 0.85;
}

.secondary-btn {
  display: inline-flex;
  align-items: center;
  padding: 11px 24px;
  background: transparent;
  color: var(--color-text-primary);
  border: 1px solid var(--color-border);
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s ease;
}

.secondary-btn:hover {
  background: var(--color-primary-light-bg);
}

.countdown {
  font-size: 13px;
  color: var(--color-text-muted);
  margin: 0;
}

/* 暗黑模式 */
body.dark-mode .error-code {
  color: var(--color-dark-text-regular);
}

body.dark-mode .error-title {
  color: var(--color-dark-text-regular);
}

body.dark-mode .error-message {
  color: var(--color-dark-text-muted);
}

body.dark-mode .primary-btn {
  background: var(--color-dark-text-regular);
  color: var(--color-dark-bg);
}

body.dark-mode .secondary-btn {
  border-color: rgba(255, 255, 255, 0.1);
  color: var(--color-dark-text-regular);
}

body.dark-mode .secondary-btn:hover {
  background: rgba(123, 111, 232, 0.12);
}

body.dark-mode .countdown {
  color: var(--color-dark-text-muted);
}
</style>
