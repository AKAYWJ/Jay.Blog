/**
 * 暗色模式全局 composable
 * - 状态提升到 useState（SSR 安全）
 * - 自动读取 localStorage + 系统偏好
 * - 通过 body.dark-mode 类切换样式
 */
export const useDarkMode = () => {
  const isDark = useState<boolean>('app:darkMode', () => false)

  const apply = (val: boolean) => {
    if (typeof document === 'undefined') return
    document.body.classList.toggle('dark-mode', val)
  }

  const setDark = (val: boolean, persist = true) => {
    isDark.value = val
    apply(val)
    if (persist && typeof localStorage !== 'undefined') {
      localStorage.setItem('darkMode', String(val))
    }
  }

  const toggle = () => setDark(!isDark.value)

  /** 客户端初始化：localStorage > 系统偏好 */
  const init = () => {
    if (typeof window === 'undefined') return
    let val: boolean
    const stored = localStorage.getItem('darkMode')
    if (stored !== null) {
      val = stored === 'true'
    } else {
      val = window.matchMedia?.('(prefers-color-scheme: dark)').matches ?? false
    }
    isDark.value = val
    apply(val)
  }

  return { isDark, setDark, toggle, init }
}
