/**
 * 全局键盘快捷键
 * - Ctrl/Cmd + K      聚焦搜索
 * - Ctrl/Cmd + Shift + L  切换暗色模式
 * - g + h             跳首页
 * - g + a             跳管理后台
 * - Esc               关闭打开的弹窗/搜索栏
 */
import { onMounted, onBeforeUnmount } from 'vue'
import { navigateTo } from 'nuxt/app'

export const useShortcuts = () => {
  let lastKey = ''
  let lastKeyTime = 0

  const isInInput = () => {
    const el = document.activeElement as HTMLElement | null
    if (!el) return false
    const tag = el.tagName
    return tag === 'INPUT' || tag === 'TEXTAREA' || el.isContentEditable
  }

  const handleKeydown = (e: KeyboardEvent) => {
    // 跳过输入框内的 Cmd/Ctrl 组合（不与系统保留冲突）
    const meta = e.metaKey || e.ctrlKey

    // ===== Ctrl/Cmd + K → 搜索 =====
    if (meta && e.key.toLowerCase() === 'k') {
      e.preventDefault()
      // 派发自定义事件，front layout 监听
      window.dispatchEvent(new CustomEvent('shortcut:search'))
      return
    }

    // ===== Ctrl/Cmd + Shift + L → 切换暗色 =====
    if (meta && e.shiftKey && e.key.toLowerCase() === 'l') {
      e.preventDefault()
      window.dispatchEvent(new CustomEvent('shortcut:toggleTheme'))
      return
    }

    // 后续快捷键在输入框中不响应
    if (isInInput()) return

    // ===== Esc → 关闭搜索 =====
    if (e.key === 'Escape') {
      window.dispatchEvent(new CustomEvent('shortcut:escape'))
      return
    }

    // ===== g + h → 首页；g + a → 管理后台 =====
    const now = Date.now()
    if (lastKey === 'g' && now - lastKeyTime < 800) {
      if (e.key === 'h') { e.preventDefault(); navigateTo('/front'); lastKey = ''; return }
      if (e.key === 'a') { e.preventDefault(); navigateTo('/admin'); lastKey = ''; return }
    }
    if (e.key === 'g') {
      lastKey = 'g'
      lastKeyTime = now
    } else {
      lastKey = ''
    }
  }

  onMounted(() => {
    if (typeof window === 'undefined') return
    window.addEventListener('keydown', handleKeydown)
  })

  onBeforeUnmount(() => {
    if (typeof window === 'undefined') return
    window.removeEventListener('keydown', handleKeydown)
  })
}
