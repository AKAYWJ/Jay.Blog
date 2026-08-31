<template>
  <div class="reading-progress" :style="{ width: progress + '%' }"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

const progress = ref(0)

const update = () => {
  const scrollTop = window.scrollY || document.documentElement.scrollTop
  const docHeight = (document.documentElement.scrollHeight || document.body.scrollHeight) - window.innerHeight
  progress.value = docHeight > 0 ? Math.min(100, Math.max(0, (scrollTop / docHeight) * 100)) : 0
}

onMounted(() => {
  update()
  window.addEventListener('scroll', update, { passive: true })
  window.addEventListener('resize', update)
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', update)
  window.removeEventListener('resize', update)
})
</script>

<style scoped>
/*
 * 阅读进度条：参考 Medium / Stripe Docs
 * 极简风格——纯色 + 极细 + 无渐变 + 无阴影
 * 跟随主题：亮色用深灰，暗色用浅色，保留品牌色但克制
 */
.reading-progress {
  position: fixed;
  top: 0;
  left: 0;
  height: 2px;
  background: #111;
  z-index: 9999;
  transition: width 0.05s linear;
  pointer-events: none;
}

body.dark-mode .reading-progress {
  background: #f5f5f7;
}
</style>
