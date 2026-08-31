<template>
  <aside v-if="headings.length > 0" class="toc">
    <div class="toc-title">目录</div>
    <ul class="toc-list">
      <li
        v-for="h in headings"
        :key="h.id"
        :class="['toc-item', `toc-h${h.level}`, { active: activeId === h.id }]"
      >
        <a :href="`#${h.id}`" @click.prevent="scrollTo(h.id)">{{ h.text }}</a>
      </li>
    </ul>
  </aside>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

interface Heading {
  id: string
  text: string
  level: number
}

const props = defineProps<{
  containerSelector?: string  // 选择器，指向包含 markdown-body 的容器
}>()

const headings = ref<Heading[]>([])
const activeId = ref<string>('')

const slugify = (text: string) =>
  text.toLowerCase()
    .replace(/[^\u4e00-\u9fa5a-z0-9\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .replace(/^-|-$/g, '')
    .slice(0, 60) || `heading-${Math.random().toString(36).slice(2, 8)}`

let observer: IntersectionObserver | null = null

const build = () => {
  const container = props.containerSelector
    ? document.querySelector(props.containerSelector)
    : document
  if (!container) return

  const nodes = container.querySelectorAll('h1, h2, h3, h4, h5, h6')
  const list: Heading[] = []

  nodes.forEach((node, idx) => {
    // 跳过第一个 h1（一般是文章标题，不在 TOC 内）
    if (node.tagName === 'H1' && idx === 0) return
    const text = node.textContent?.trim() || ''
    if (!text) return
    let id = node.id
    if (!id) {
      id = slugify(text)
      // 避免重复 id
      let suffix = 1
      const baseId = id
      while (container.querySelector('#' + CSS.escape(id))) {
        id = `${baseId}-${suffix++}`
      }
      node.id = id
    }
    list.push({
      id,
      text,
      level: parseInt(node.tagName.substring(1), 10)
    })
  })

  headings.value = list

  // 用 IntersectionObserver 监听当前章节
  if (observer) observer.disconnect()
  if (list.length === 0) return

  observer = new IntersectionObserver(
    (entries) => {
      // 取距离顶部最近的可见 heading
      const visible = entries
        .filter(e => e.isIntersecting)
        .sort((a, b) => a.boundingClientRect.top - b.boundingClientRect.top)
      if (visible[0]) {
        activeId.value = visible[0].target.id
      }
    },
    {
      rootMargin: '-80px 0px -70% 0px',
      threshold: 0
    }
  )

  list.forEach(h => {
    const el = document.getElementById(h.id)
    if (el) observer!.observe(el)
  })
}

const scrollTo = (id: string) => {
  const el = document.getElementById(id)
  if (!el) return
  const top = el.getBoundingClientRect().top + window.scrollY - 80
  window.scrollTo({ top, behavior: 'smooth' })
  activeId.value = id
}

onMounted(() => {
  // markdown-body 异步渲染，等 nextTick 后才扫描
  setTimeout(build, 100)
  // 兜底：每隔 500ms 扫描一次（应付延迟渲染）
  let tries = 0
  const timer = setInterval(() => {
    if (headings.value.length > 0 || tries > 10) {
      clearInterval(timer)
      return
    }
    build()
    tries++
  }, 500)
})

onBeforeUnmount(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.toc {
  position: sticky;
  top: 88px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  padding-left: 8px;
  border-left: 1px solid var(--color-border);
  font-size: 13px;
}

.toc-title {
  font-size: 11px;
  font-weight: 500;
  color: var(--color-text-muted);
  letter-spacing: 1px;
  margin-bottom: 12px;
  text-transform: uppercase;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.toc-item {
  margin: 0;
  line-height: 1.6;
}

.toc-item a {
  display: block;
  padding: 4px 10px;
  color: var(--color-text-muted);
  text-decoration: none;
  border-left: 2px solid transparent;
  margin-left: -10px;
  transition: color 0.15s ease, border-color 0.15s ease;
}

.toc-item a:hover {
  color: var(--color-text-primary);
}

.toc-item.active a {
  color: var(--color-primary);
  border-left-color: var(--color-primary);
  font-weight: 500;
}

/* 缩进 */
.toc-h1 { padding-left: 0; }
.toc-h2 { padding-left: 0; }
.toc-h3 { padding-left: 12px; }
.toc-h4 { padding-left: 24px; }
.toc-h5 { padding-left: 36px; }
.toc-h6 { padding-left: 48px; }

/* 滚动条美化 */
.toc::-webkit-scrollbar {
  width: 4px;
}
.toc::-webkit-scrollbar-thumb {
  background: var(--color-border);
  border-radius: 2px;
}

/* 暗黑模式 */
body.dark-mode .toc {
  border-left-color: rgba(255, 255, 255, 0.08);
}

body.dark-mode .toc-item.active a {
  color: #b794f4;
  border-left-color: #b794f4;
}

/* 移动端隐藏 */
@media screen and (max-width: 1100px) {
  .toc {
    display: none;
  }
}
</style>
