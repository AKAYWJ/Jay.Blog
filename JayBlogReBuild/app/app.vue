<template>
  <NuxtLoadingIndicator color="#5B4FCF" :height="3" :duration="200" />
  <NuxtLayout>
    <NuxtPageTransition>
      <NuxtPage>
        <template #fallback>
          <!-- 客户端路由切换时的过渡骨架 -->
          <div class="page-skeleton">
            <div class="skeleton-bar" style="width: 40%"></div>
            <div class="skeleton-bar" style="width: 80%"></div>
            <div class="skeleton-bar" style="width: 65%"></div>
            <div class="skeleton-bar" style="width: 75%"></div>
          </div>
        </template>
      </NuxtPage>
    </NuxtPageTransition>
  </NuxtLayout>
  <PwaUpdatePrompt />
</template>

<script setup>
import { onMounted, watch } from 'vue'
import { buildSeoMeta, SITE_NAME, SITE_URL, SITE_DESCRIPTION } from '~/utils/seo'

// 全局默认 SEO meta（子页面 useSeoMeta 会覆盖）
useSeoMeta(buildSeoMeta({}))

// 默认主题色（影响浏览器 UI 颜色）
useHead({
  htmlAttrs: { lang: 'zh-CN' },
  meta: [
    { name: 'theme-color', content: '#5B4FCF' },
    { name: 'format-detection', content: 'telephone=no' }
  ],
  link: [
    { rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' },
    { rel: 'alternate', type: 'application/rss+xml', title: SITE_NAME, href: `${SITE_URL}/rss.xml` }
  ],
  script: [
    {
      type: 'application/ld+json',
      innerHTML: JSON.stringify({
        '@context': 'https://schema.org',
        '@type': 'WebSite',
        name: SITE_NAME,
        url: SITE_URL,
        description: SITE_DESCRIPTION,
        potentialAction: {
          '@type': 'SearchAction',
          target: `${SITE_URL}/front?key={search_term_string}`,
          'query-input': 'required name=search_term_string'
        }
      })
    }
  ]
})

// 暗色模式：全局初始化 + 监听系统主题变化
const { isDark, init, setDark } = useDarkMode()
// i18n：本地存储 > 浏览器语言
const { init: initI18n } = useI18n()
onMounted(() => {
  init()
  initI18n()
  if (typeof window !== 'undefined') {
    const mq = window.matchMedia('(prefers-color-scheme: dark)')
    mq.addEventListener('change', (e) => {
      // 仅在用户未显式设置过时才跟随系统
      if (localStorage.getItem('darkMode') === null) setDark(e.matches, false)
    })
  }
})

// 主题色 meta 同步切换
watch(isDark, (val) => {
  useHead({ meta: [{ name: 'theme-color', content: val ? '#1a1726' : '#5B4FCF' }] }
)
}, { immediate: false })

// 派发首屏加载完成事件（卸载 server 注入的 loader）
if (typeof window !== 'undefined') {
  onMounted(() => {
    // 等下一帧，确保 Vue 已接管 DOM
    requestAnimationFrame(() => {
      window.dispatchEvent(new Event('app:mounted'))
    })
  })
}

// 全局键盘快捷键
useShortcuts()

// 监听主题切换事件
onMounted(() => {
  window.addEventListener('shortcut:toggleTheme', () => {
    setDark(!isDark.value)
  })
})
</script>

<style>
/* 路由切换动画：fade + 轻微上移，让用户感觉是连续的，不再有"白屏等待" */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>

<style>
/* 全局无 scoped 样式，全局通用变量、重置样式在这里写 */
:root {
  --color-bg: #fff;
  --glass-bg: rgba(255, 255, 255, 0.72);
  --glass-blur: 12px;
  --glass-border: rgba(0, 0, 0, 0.08);
  --color-primary: #5b4fcf;
  --color-primary-gradient: linear-gradient(135deg, #6b5cf0, #5b4fcf);
  --color-primary-light-bg: rgba(91, 79, 207, 0.08);
  --color-text-primary: #111;
  --color-text-regular: #444;
  --color-text-muted: #888;
  --color-border: #eee;
  --color-danger: #f56c6c;
  --transition-fast: 0.2s ease;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: system-ui, -apple-system, sans-serif;
}

a {
  text-decoration: none;
}
</style>