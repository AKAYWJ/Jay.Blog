<template>
  <transition name="pwa-update">
    <div v-if="needRefresh" class="pwa-toast">
      <span>有新版本可用</span>
      <button @click="update">立即更新</button>
      <button class="dismiss" @click="dismiss">稍后</button>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const needRefresh = ref(false)
let updateFn: ((reload?: boolean) => Promise<void>) | null = null

onMounted(async () => {
  if (typeof window === 'undefined') return
  try {
    const { useRegisterSW } = await import('virtual:pwa-register/vue')
    const reg = useRegisterSW({
      immediate: true,
      onNeedRefresh() {
        needRefresh.value = true
      },
      onOfflineReady() {
        // 静默：不做 toast
      }
    })
    updateFn = reg.updateServiceWorker
  } catch {
    // PWA 未配置或开发模式
  }
})

const update = async () => {
  if (updateFn) await updateFn(true)
}

const dismiss = () => {
  needRefresh.value = false
}
</script>

<style scoped>
.pwa-toast {
  position: fixed;
  bottom: 24px;
  right: 24px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  background: var(--color-text-primary);
  color: var(--color-bg);
  border-radius: 10px;
  font-size: 13px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  z-index: 9999;
}

.pwa-toast button {
  border: none;
  background: transparent;
  color: var(--color-bg);
  font-size: 13px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 6px;
  transition: background 0.15s ease;
}

.pwa-toast button:hover {
  background: rgba(255, 255, 255, 0.12);
}

.pwa-toast button.dismiss {
  opacity: 0.7;
  font-size: 12px;
}

.pwa-update-enter-active,
.pwa-update-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.pwa-update-enter-from,
.pwa-update-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

body.dark-mode .pwa-toast {
  background: var(--color-dark-text-regular);
  color: var(--color-dark-bg);
}
</style>
