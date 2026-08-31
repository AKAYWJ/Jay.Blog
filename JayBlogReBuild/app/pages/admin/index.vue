<template>
  <div class="admin-home">
    <!-- 顶栏：用户身份 + 当前时间，像 Linear -->
    <header class="topbar">
      <div class="topbar-left">
        <h1 class="topbar-title">{{ todayLabel }}</h1>
        <p class="topbar-sub">{{ greeting }}，{{ username }}</p>
      </div>
      <div class="topbar-right">
        <span class="topbar-role">{{ roleLabel }}</span>
        <div class="topbar-avatar">{{ usernameInitial }}</div>
      </div>
    </header>

    <!-- 主区域：左日历 / 右信息栏，像 Apple iCloud -->
    <div class="layout">
      <!-- 日历 -->
      <section class="calendar-pane">
        <!-- 日历容器：浅色下白底深色下深底，避免格子溶进页面 -->
        <div class="calendar-card">
        <div class="pane-header">
          <div class="month-switcher">
            <button class="icon-btn" @click="prevMonth" :aria-label="t('admin.home.monthPrev')">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="1.5" stroke-linecap="round">
                <polyline points="15 18 9 12 15 6"></polyline>
              </svg>
            </button>
            <h2 class="month-label">{{ currentYear }}{{ t('admin.home.yearUnit') }}{{ currentMonth }}{{ t('admin.home.monthUnit') }}</h2>
            <button class="icon-btn" @click="nextMonth" :aria-label="t('admin.home.monthNext')">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                stroke-width="1.5" stroke-linecap="round">
                <polyline points="9 18 15 12 9 6"></polyline>
              </svg>
            </button>
          </div>
          <button class="today-btn" @click="goToday">{{ t('admin.home.goBackToday') }}</button>
        </div>

        <div class="week-row">
          <span v-for="w in weekLabels" :key="w">{{ w }}</span>
        </div>

        <div class="day-grid">
          <div
            v-for="d in dayList"
            :key="d.key"
            class="day"
            :class="{
              'is-other': d.disabled,
              'is-today': d.isToday,
              'is-selected': selectDate === d.fullDate
            }"
            @click="!d.disabled && pickDay(d)"
          >
            <span class="day-num">{{ d.day }}</span>
            <span v-if="d.hasEvent" class="event-dot"></span>
          </div>
        </div>

        <p class="calendar-hint">{{ t('admin.home.calendarHint') }}</p>
        </div>
      </section>

      <!-- 右侧栏 -->
      <aside class="side-pane">
        <!-- 账号 -->
        <section class="card account-card">
          <div class="account-row">
            <div class="account-avatar">{{ usernameInitial }}</div>
            <div class="account-info">
              <div class="account-name">{{ username }}</div>
              <div class="account-id">ID · {{ userId }}</div>
            </div>
          </div>
          <div class="account-meta">
            <div class="meta-row">
              <span>{{ t('admin.home.email') }}</span>
              <span>{{ email || t('admin.home.notSet') }}</span>
            </div>
            <div class="meta-row">
              <span>{{ t('admin.home.phone') }}</span>
              <span>{{ phone || t('admin.home.notSet') }}</span>
            </div>
            <div class="meta-row">
              <span>{{ t('admin.home.role') }}</span>
              <span>{{ roleLabel }}</span>
            </div>
          </div>
        </section>

        <!-- 最近活动：真实数据 -->
        <section class="card activity-card">
          <h3 class="card-h">{{ t('admin.home.recentActivity') }}</h3>
          <div v-if="activitiesLoading" class="empty">{{ t('admin.home.loading') }}</div>
          <ul v-else-if="activities.length" class="activity-list">
            <li v-for="a in activities" :key="a.id" class="activity-item">
              <span class="activity-dot" :style="{ background: a.color }"></span>
              <div class="activity-body">
                <div class="activity-title">{{ a.title }}</div>
                <div class="activity-meta">{{ a.meta }}</div>
              </div>
            </li>
          </ul>
          <div v-else class="empty">{{ t('admin.home.noActivity') }}</div>
        </section>

        <!-- 快捷链接 -->
        <section class="card quick-card">
          <h3 class="card-h">{{ t('admin.home.quickLinks') }}</h3>
          <NuxtLink to="/admin/article" class="quick-link">
            <span>{{ t('admin.home.writeArticle') }}</span>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="1.5" stroke-linecap="round">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
          </NuxtLink>
          <NuxtLink to="/admin/comment" class="quick-link">
            <span>{{ t('admin.home.reviewComments') }}</span>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="1.5" stroke-linecap="round">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
          </NuxtLink>
          <NuxtLink v-if="isAdmin" to="/admin/echart" class="quick-link">
            <span>{{ t('admin.home.dataOverview') }}</span>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
              stroke-width="1.5" stroke-linecap="round">
              <line x1="5" y1="12" x2="19" y2="12"></line>
              <polyline points="12 5 19 12 12 19"></polyline>
            </svg>
          </NuxtLink>
          <button class="quick-link logout" @click="handleLogout">
            <span>{{ t('admin.home.logout') }}</span>
          </button>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'nuxt/app'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '~/stores/store'
import { getHomeStats, getMonthEvents } from '~/api/admin-home'

const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const today = new Date()
const currentYear = ref(today.getFullYear())
const currentMonth = ref(today.getMonth() + 1)
const selectDate = ref('')
const dayList = ref([])
const weekLabels = computed(() => [
  t('admin.home.weekSun'), t('admin.home.weekMon'), t('admin.home.weekTue'), t('admin.home.weekWed'),
  t('admin.home.weekThu'), t('admin.home.weekFri'), t('admin.home.weekSat')
])

// 真实数据状态
const eventDateSet = ref(new Set<string>())  // 当月有文章/评论的日期（YYYY-MM-DD）
const activities = ref<Array<{ id: string; color: string; title: string; meta: string }>>([])
const activitiesLoading = ref(false)

const username = computed(() => userStore.getUsername || t('admin.home.notLogged'))
const userId = computed(() => userStore.getUserId || '-')
const roleId = computed(() => userStore.getRoleId)
const email = computed(() => userStore.getUserEmail)
const phone = computed(() => userStore.getUserPhone)
const isAdmin = computed(() => {
  const r = String(roleId.value)
  return r === '1' || r === '2'
})
const roleLabel = computed(() => {
  const r = String(roleId.value)
  return r === '1' ? t('admin.home.roleAdmin') : r === '2' ? t('admin.home.roleOperator') : t('admin.home.roleUser')
})
const usernameInitial = computed(() => username.value.charAt(0).toUpperCase())

const todayLabel = computed(() => {
  const weekDays = [t('admin.home.weekSun'), t('admin.home.weekMon'), t('admin.home.weekTue'), t('admin.home.weekWed'), t('admin.home.weekThu'), t('admin.home.weekFri'), t('admin.home.weekSat')]
  return `${today.getMonth() + 1}${t('admin.home.monthUnit')}${today.getDate()}${t('admin.home.dayUnit')} · ${weekDays[today.getDay()]}`
})

const greeting = computed(() => {
  const h = today.getHours()
  if (h < 6) return t('admin.home.greetingNight')
  if (h < 11) return t('admin.home.greetingMorning')
  if (h < 14) return t('admin.home.greetingNoon')
  if (h < 18) return t('admin.home.greetingAfternoon')
  return t('admin.home.greetingEvening')
})

// 把时间戳格式化为 "今天 HH:mm / 昨天 HH:mm / MM-DD HH:mm"
const formatActivityTime = (dateStr?: string) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  const isSameDay = (a: Date, b: Date) =>
    a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate()
  if (isSameDay(d, now)) return t('admin.home.today', { time: `${hh}:${mm}` })
  const yest = new Date(now.getTime() - 86400000)
  if (isSameDay(d, yest)) return t('admin.home.yesterday', { time: `${hh}:${mm}` })
  return `${d.getMonth() + 1}-${String(d.getDate()).padStart(2, '0')} ${hh}:${mm}`
}

const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
  renderCalendar()
  loadMonthEvents()
}

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
  renderCalendar()
  loadMonthEvents()
}

const goToday = () => {
  currentYear.value = today.getFullYear()
  currentMonth.value = today.getMonth() + 1
  selectDate.value = ''
  renderCalendar()
  loadMonthEvents()
}

const pickDay = (cell) => {
  selectDate.value = cell.fullDate
}

const renderCalendar = () => {
  const year = currentYear.value
  const month = currentMonth.value
  const firstDate = new Date(year, month - 1, 1)
  const totalDay = new Date(year, month, 0).getDate()
  const firstWeekIdx = firstDate.getDay()
  const lastPrevMonthDay = new Date(year, month - 1, 0).getDate()

  const days: any[] = []
  let keyId = 0

  for (let i = 0; i < firstWeekIdx; i++) {
    days.push({
      key: keyId++,
      day: lastPrevMonthDay - firstWeekIdx + i + 1,
      fullDate: '',
      disabled: true,
      isToday: false,
      hasEvent: false
    })
  }

  for (let d = 1; d <= totalDay; d++) {
    const full = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    days.push({
      key: keyId++,
      day: d,
      fullDate: full,
      disabled: false,
      isToday: today.getFullYear() === year && today.getMonth() === month - 1 && today.getDate() === d,
      hasEvent: eventDateSet.value.has(full)
    })
  }

  const fill = 42 - days.length
  for (let d = 1; d <= fill; d++) {
    days.push({
      key: keyId++,
      day: d,
      fullDate: '',
      disabled: true,
      isToday: false,
      hasEvent: false
    })
  }
  dayList.value = days
}

// 加载当月事件（单接口 + 后端过滤）
const loadMonthEvents = async () => {
  try {
    const res = await getMonthEvents(currentYear.value, currentMonth.value)
    const set = new Set<string>()
    const data: any = res.data || {}
    ;(data.articleDates || []).forEach((d: string) => set.add(d))
    ;(data.commentDates || []).forEach((d: string) => set.add(d))
    eventDateSet.value = set
  } catch {
    eventDateSet.value = new Set()
  }
}

// 加载最近活动（单接口）
const loadActivities = async () => {
  activitiesLoading.value = true
  try {
    const res = await getHomeStats()
    const data: any = res.data || {}
    const items: typeof activities.value = []

    // 文章（紫色）
    ;(data.recentArticles || []).forEach((a: any) => {
      const action = a.status === 2 ? t('admin.home.actionPublish') : a.status === 1 ? t('admin.home.actionEdit') : a.status === 3 ? t('admin.home.actionPrivate') : t('admin.home.actionUpdate')
      items.push({
        id: 'a-' + a.id,
        color: '#5b4fcf',
        title: `${action}${t('admin.home.articleTitle', { title: a.title || t('admin.home.untitled') })}`,
        meta: formatActivityTime(a.createTime || a.updateTime)
      })
    })

    // 评论（橙色）
    ;(data.recentComments || []).forEach((c: any) => {
      const content = (c.content || '').slice(0, 20)
      const action = c.status === 1 ? t('admin.home.actionCommented') : c.status === 2 ? t('admin.home.actionApproved') : t('admin.home.actionBlocked')
      items.push({
        id: 'c-' + c.id,
        color: '#f59e0b',
        title: `${action}${t('admin.home.commentQuote', { content: `${content}${content.length >= 20 ? '…' : ''}` })}`,
        meta: formatActivityTime(c.createTime)
      })
    })

    // 后端已按 create_time desc 返回，前端只需按 id 顺序（同类型内部就是 desc）
    activities.value = items.slice(0, 6)
  } catch {
    activities.value = []
  } finally {
    activitiesLoading.value = false
  }
}

const handleLogout = () => {
  ElMessageBox.confirm(t('admin.home.logoutConfirm'), t('admin.home.logoutTitle'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  renderCalendar()
  loadMonthEvents()
  loadActivities()
})
</script>

<style scoped>
.admin-home {
  min-height: 100vh;
  padding: 40px 48px 60px;
  max-width: 1280px;
  margin: 0 auto;
}

/* ============ 顶栏（Linear 风） ============ */
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-border);
}

.topbar-title {
  margin: 0 0 4px;
  font-size: 22px;
  font-weight: 600;
  color: var(--color-text-primary);
  letter-spacing: -0.3px;
}

.topbar-sub {
  margin: 0;
  font-size: 14px;
  color: var(--color-text-muted);
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.topbar-role {
  font-size: 12px;
  color: var(--color-text-muted);
  padding: 4px 10px;
  border: 1px solid var(--color-border);
  border-radius: 999px;
}

.topbar-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0;
}

/* ============ 主区域 ============ */
.layout {
  display: grid;
  grid-template-columns: 1.6fr 1fr;
  gap: 48px;
  align-items: start;
}

/* ============ 日历 ============ */
.calendar-pane {
  min-width: 0;
}

.calendar-card {
  padding: 28px;
  border-radius: 12px;
  background: var(--color-bg);
  border: 1px solid var(--color-border);
}

body.dark-mode .calendar-card {
  background: var(--color-dark-surface, #1e1b2e);
  border-color: rgba(255, 255, 255, 0.08);
}

.pane-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.month-switcher {
  display: flex;
  align-items: center;
  gap: 8px;
}

.month-label {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-text-primary);
  min-width: 130px;
  text-align: center;
  letter-spacing: -0.2px;
}

.icon-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s ease, color 0.15s ease;
}

.icon-btn:hover {
  background: var(--color-primary-light-bg);
  color: var(--color-primary);
}

.today-btn {
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  font-size: 13px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: color 0.15s ease, background 0.15s ease;
}

.today-btn:hover {
  color: var(--color-primary);
  background: var(--color-primary-light-bg);
}

.week-row {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 8px;
}

.week-row span {
  text-align: center;
  font-size: 12px;
  color: var(--color-text-muted);
  font-weight: 500;
}

.day-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  transition: background 0.12s ease;
  user-select: none;
}

.day:not(.is-other):hover {
  background: var(--color-primary-light-bg);
}

.day-num {
  font-size: 14px;
  color: var(--color-text-primary);
  font-feature-settings: 'tnum';
  line-height: 1;
}

/* 其他月份的日期：透明底+极淡边框，溶进背景但不"黑洞" */
.day.is-other {
  cursor: default;
  pointer-events: none;
  background: transparent;
}

.day.is-other .day-num {
  color: var(--color-text-muted);
  opacity: 0.25;
}

body.dark-mode .day.is-other .day-num {
  opacity: 0.3;
  color: var(--color-dark-text-muted);
}

.day.is-today .day-num {
  color: var(--color-primary);
  font-weight: 600;
}

.day.is-selected {
  background: var(--color-text-primary);
}

.day.is-selected .day-num {
  color: var(--color-bg);
  font-weight: 600;
}

.event-dot {
  width: 3px;
  height: 3px;
  border-radius: 50%;
  background: var(--color-primary);
}

.day.is-selected .event-dot {
  background: var(--color-bg);
}

.calendar-hint {
  margin: 24px 0 0;
  font-size: 12px;
  color: var(--color-text-muted);
  text-align: center;
}

/* ============ 右侧栏 ============ */
.side-pane {
  display: flex;
  flex-direction: column;
  gap: 32px;
  min-width: 0;
}

.card {
  /* 极简：分隔线，不用玻璃卡 */
  padding-bottom: 24px;
  border-bottom: 1px solid var(--color-border);
}

.card:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.card-h {
  margin: 0 0 16px;
  font-size: 12px;
  font-weight: 500;
  color: var(--color-text-muted);
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

/* 账号卡 */
.account-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
}

.account-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--color-primary);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 600;
  flex-shrink: 0;
}

.account-info {
  min-width: 0;
}

.account-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary);
  margin-bottom: 2px;
}

.account-id {
  font-size: 12px;
  color: var(--color-text-muted);
  font-feature-settings: 'tnum';
}

.account-meta {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 13px;
  border-bottom: 1px solid var(--color-border);
}

.meta-row:last-child {
  border-bottom: none;
}

.meta-row span:first-child {
  color: var(--color-text-muted);
}

.meta-row span:last-child {
  color: var(--color-text-primary);
  font-feature-settings: 'tnum';
}

/* 活动 */
.activity-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border);
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-top: 7px;
  flex-shrink: 0;
}

.activity-body {
  flex: 1;
  min-width: 0;
}

.activity-title {
  font-size: 13px;
  color: var(--color-text-primary);
  margin-bottom: 2px;
  line-height: 1.4;
}

.activity-meta {
  font-size: 12px;
  color: var(--color-text-muted);
}

.empty {
  font-size: 13px;
  color: var(--color-text-muted);
  padding: 8px 0;
}

/* 快捷入口 */
.quick-link {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  font-size: 13px;
  color: var(--color-text-primary);
  text-decoration: none;
  border-bottom: 1px solid var(--color-border);
  transition: color 0.15s ease;
}

.quick-link:hover {
  color: var(--color-primary);
}

.quick-link:last-child {
  border-bottom: none;
}

.quick-link.logout {
  background: none;
  border: none;
  border-bottom: 1px solid var(--color-border);
  width: 100%;
  cursor: pointer;
  text-align: left;
  font-family: inherit;
}

.quick-link.logout:hover {
  color: #ef4444;
}

/* ============ 响应式 ============ */
@media screen and (max-width: 960px) {
  .admin-home {
    padding: 24px 20px 40px;
  }
  .layout {
    grid-template-columns: 1fr;
    gap: 32px;
  }
}

@media screen and (max-width: 480px) {
  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}

/* ============ 暗黑模式 ============ */
body.dark-mode .admin-home {
  background: var(--color-dark-bg);
}

body.dark-mode .topbar {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

body.dark-mode .topbar-title {
  color: var(--color-dark-text-regular);
}

body.dark-mode .topbar-sub,
body.dark-mode .topbar-role {
  color: var(--color-dark-text-muted);
}

body.dark-mode .topbar-role {
  border-color: rgba(255, 255, 255, 0.1);
}

body.dark-mode .month-label {
  color: var(--color-dark-text-regular);
}

body.dark-mode .icon-btn {
  color: var(--color-dark-text-muted);
}

body.dark-mode .icon-btn:hover {
  background: rgba(123, 111, 232, 0.12);
  color: #b794f4;
}

body.dark-mode .today-btn {
  color: var(--color-dark-text-muted);
}

body.dark-mode .today-btn:hover {
  color: #b794f4;
  background: rgba(123, 111, 232, 0.12);
}

body.dark-mode .week-row {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

body.dark-mode .week-row span {
  color: var(--color-dark-text-muted);
}

body.dark-mode .day:not(.is-other):hover {
  background: rgba(123, 111, 232, 0.1);
}

body.dark-mode .day-num {
  color: var(--color-dark-text-regular);
}

body.dark-mode .day.is-today .day-num {
  color: #b794f4;
}

body.dark-mode .day.is-selected {
  background: var(--color-dark-text-regular);
}

body.dark-mode .day.is-selected .day-num {
  color: var(--color-dark-bg);
}

body.dark-mode .event-dot {
  background: #b794f4;
}

body.dark-mode .card {
  border-bottom-color: rgba(255, 255, 255, 0.08);
}

body.dark-mode .card-h {
  color: var(--color-dark-text-muted);
}

body.dark-mode .account-name,
body.dark-mode .meta-row span:last-child {
  color: var(--color-dark-text-regular);
}

body.dark-mode .account-id,
body.dark-mode .meta-row span:first-child,
body.dark-mode .activity-meta {
  color: var(--color-dark-text-muted);
}

body.dark-mode .activity-title {
  color: var(--color-dark-text-regular);
}

body.dark-mode .meta-row,
body.dark-mode .activity-item,
body.dark-mode .quick-link {
  border-bottom-color: rgba(255, 255, 255, 0.06);
}

body.dark-mode .quick-link {
  color: var(--color-dark-text-regular);
}

body.dark-mode .quick-link:hover {
  color: #b794f4;
}

body.dark-mode .quick-link.logout:hover {
  color: #f87171;
}
</style>
