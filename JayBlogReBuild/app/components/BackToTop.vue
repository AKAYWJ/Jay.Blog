<template>
  <transition name="back-to-top">
    <button
      v-if="visible"
      class="back-to-top"
      aria-label="回到顶部"
      title="回到顶部"
      @click="scrollToTop"
    >
      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
        stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
        <line x1="12" y1="19" x2="12" y2="5"></line>
        <polyline points="5 12 12 5 19 12"></polyline>
      </svg>
    </button>
  </transition>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const props = defineProps({
  threshold: { type: Number, default: 400 }
})

const visible = ref(false)

const onScroll = () => {
  visible.value = (typeof window !== 'undefined') && window.scrollY > props.threshold
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true })
  onScroll()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
.back-to-top {
  position: fixed;
  right: 28px;
  bottom: 28px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--liquid-glass-bg);
  backdrop-filter: var(--liquid-glass-blur);
  -webkit-backdrop-filter: var(--liquid-glass-blur);
  border: var(--liquid-glass-border);
  border-radius: 50%;
  color: var(--color-primary);
  cursor: pointer;
  box-shadow: var(--liquid-glass-shadow);
  z-index: 900;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.back-to-top:hover {
  transform: translateY(-2px);
  background: var(--color-primary);
  color: #fff;
  box-shadow: 0 8px 24px rgba(91, 79, 207, 0.32);
}

.back-to-top:active {
  transform: translateY(0);
}

.back-to-top-enter-active,
.back-to-top-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.back-to-top-enter-from,
.back-to-top-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

@media screen and (max-width: 640px) {
  .back-to-top {
    right: 16px;
    bottom: 16px;
    width: 40px;
    height: 40px;
  }
}
</style>
