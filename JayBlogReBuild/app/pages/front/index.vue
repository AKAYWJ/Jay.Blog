<template>
    <div class="front-index">
        <!-- Hero：极简单色，参考 Apple / Stripe 风格 -->
        <section class="hero">
            <div class="hero-meta">
                <span class="hero-date">{{ todayStr }}</span>
                <span class="hero-dot"></span>
                <span class="hero-section">Personal Blog</span>
            </div>

            <h1 class="hero-title">
                {{ t('hero.title') }}<br>
                {{ t('hero.titleLine2') }}
            </h1>

            <p class="hero-sub">
                {{ t('hero.subtitle') }}
            </p>

            <div class="hero-footer">
                <div class="hero-stats">
                    <span class="hero-stat">
                        <span class="hero-stat-n">{{ stat.articleTotal || 0 }}</span>
                        <span class="hero-stat-l">{{ t('hero.stat.article') }}</span>
                    </span>
                    <span class="hero-stat">
                        <span class="hero-stat-n">{{ stat.visitorTotal || 0 }}</span>
                        <span class="hero-stat-l">{{ t('hero.stat.visitor') }}</span>
                    </span>
                    <span class="hero-stat">
                        <span class="hero-stat-n">{{ stat.commentTotal || 0 }}</span>
                        <span class="hero-stat-l">{{ t('hero.stat.comment') }}</span>
                    </span>
                </div>
                <NuxtLink to="/articlelist" class="hero-cta">
                    {{ t('hero.cta') }}
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                        stroke-width="1.5" stroke-linecap="round">
                        <line x1="5" y1="12" x2="19" y2="12"></line>
                        <polyline points="12 5 19 12 12 19"></polyline>
                    </svg>
                </NuxtLink>
            </div>
        </section>

        <!-- 搜索提示 -->
        <div v-if="searchKey" class="search-tip">
            <span>{{ t('hero.search.tip', { key: searchKey, total }) }}</span>
            <el-button type="text" size="small" @click="clearSearch">{{ t('hero.search.clear') }}</el-button>
        </div>

        <div class="index-layout">
            <!-- 左侧文章区域 -->
            <main class="article-area">
                <div class="article-area-header">
                    <span class="area-label">{{ t('hero.section.recent') }}</span>
                    <NuxtLink to="/articlelist" class="readmore-mini">
                        {{ t('hero.cta') }}
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" stroke-linecap="round">
                            <polyline points="9 18 15 12 9 6"></polyline>
                        </svg>
                    </NuxtLink>
                </div>

                <div class="article-list">
                    <ArticleCard v-for="item in articleList" :key="item.id" :article="item"
                        @click="goDetail(item.id)" />
                </div>
            </main>

            <!-- 右侧侧边栏 -->
            <aside class="side-area">
                <!-- 日历卡片 -->
                <div class="side-card calendar-card">
                    <div class="calendar-month-row">
                        <button class="month-arrow" @click="prevMonth">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <polyline points="15 18 9 12 15 6"></polyline>
                            </svg>
                        </button>
                        <span class="month-label">{{ currentYear }}{{ t('calendar.monthUnit') }}{{ currentMonth }}{{ t('calendar.monthUnit') }}</span>
                        <button class="month-arrow" @click="nextMonth">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                stroke-width="2">
                                <polyline points="9 18 15 12 9 6"></polyline>
                            </svg>
                        </button>
                    </div>

                    <div class="calendar-week">
                        <span v-for="w in weekShort" :key="w">{{ w }}</span>
                    </div>

                    <div class="calendar-days">
                        <div v-for="d in dayList" :key="d.key" class="day-cell" :class="{
                            disabled: d.disabled,
                            today: d.isToday,
                            hasArticle: articleDateList.includes(d.fullDate),
                            selected: selectDate === d.fullDate
                        }" @click="pickDay(d)">
                            {{ d.day }}
                        </div>
                    </div>
                </div>

                <!-- 站点统计 -->
                <div class="side-card stat-card">
                    <div class="stat-grid">
                        <div class="stat-item">
                            <span class="stat-num">{{ stat.articleTotal || 0 }}</span>
                            <span class="stat-label">{{ t('hero.stat.article') }}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-num">{{ stat.visitorTotal || 0 }}</span>
                            <span class="stat-label">{{ t('hero.stat.visitor') }}</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-num">{{ stat.commentTotal || 0 }}</span>
                            <span class="stat-label">{{ t('hero.stat.comment') }}</span>
                        </div>
                    </div>
                </div>

                <!-- 分类列表 -->
                <div class="side-card">
                    <h3 class="side-title">{{ t('hero.section.category') }}</h3>
                    <div class="side-list">
                        <NuxtLink v-for="c in categoryList" :key="c.id"
                            :to="{ path: '/front/category/all', query: { categoryId: c.id } }" class="side-link">
                            {{ c.name }}
                            <span class="side-count">{{ c.articleCount || 0 }}</span>
                        </NuxtLink>
                    </div>
                </div>

                <!-- 热门文章 -->
                <div class="side-card">
                    <h3 class="side-title">{{ t('hero.section.hot') }}</h3>
                    <div class="hot-list">
                        <template v-if="hotArticleList && hotArticleList.length > 0">
                            <div v-for="hot in hotArticleList.slice(0, 5)" :key="hot.id" class="hot-item"
                                @click="goDetail(hot.id)">
                                <span class="hot-title">{{ hot.title }}</span>
                                <span class="hot-views">{{ hot.viewCount || 0 }} {{ t('article.view') }}</span>
                            </div>
                        </template>
                        <div v-else class="empty-tip">{{ t('common.empty') }}</div>
                    </div>
                </div>

                <!-- 时间轴入口 -->
                <NuxtLink to="/front/timeline" class="timeline-entry">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                        stroke-linecap="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                    {{ t('nav.archive') }}
                </NuxtLink>
            </aside>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'nuxt/app'
import ArticleCard from '~/components/ArticleCard.vue'
import { getFrontArticleList, getHotArticle, getBlogStat } from '~/api/front'
import { getCategoryList } from '~/api/category'
import { buildSeoMeta } from '~/utils/seo'

// 路由实例
const route = useRoute()
const router = useRouter()
const { t, locale } = useI18n()

// SSR 数据预取：服务端拿到数据 → HTML 里直接有内容
const { data: ssrData } = await useAsyncData('front:index', async () => {
    const [catRes, articleRes, hotRes, statRes] = await Promise.all([
        getCategoryList(),
        getFrontArticleList({ pageNum: 1, pageSize: 5 }),
        getHotArticle(),
        getBlogStat()
    ])
    return {
        categoryList: catRes.data || [],
        articleList: articleRes.data?.records || [],
        total: articleRes.data?.total || 0,
        hotArticleList: hotRes.data || [],
        stat: statRes.data || { articleTotal: 0, visitorTotal: 0, commentTotal: 0 }
    }
})

// 模板里直接用 ssrData.xxx，无需再 ref 复制
const searchKey = ref('')
const pageNum = ref(1)
const pageSize = ref(5)

const categoryList = computed(() => ssrData.value?.categoryList || [])
const articleList = computed(() => ssrData.value?.articleList || [])
const total = computed(() => ssrData.value?.total || 0)
const hotArticleList = computed(() => ssrData.value?.hotArticleList || [])
const stat = computed(() => ssrData.value?.stat || { articleTotal: 0, visitorTotal: 0, commentTotal: 0 })

// Hero 日期字符串（不依赖网络）
const todayStr = computed(() => {
    const now = new Date()
    const dayNames = locale === 'en-US'
        ? ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
        : ['日', '一', '二', '三', '四', '五', '六']
    const week = dayNames[now.getDay()]
    if (locale === 'en-US') {
        return `${now.getFullYear()}.${String(now.getMonth() + 1).padStart(2, '0')}.${String(now.getDate()).padStart(2, '0')} ${week}`
    }
    return `${now.getFullYear()}.${String(now.getMonth() + 1).padStart(2, '0')}.${String(now.getDate()).padStart(2, '0')} 周${week}`
})

// 日历相关数据
const now = new Date()
const currentYear = ref(now.getFullYear())
const currentMonth = ref(now.getMonth() + 1)
const selectDate = ref('')
const dayList = ref([])
const articleDateList = ref([])
const weekShort = computed(() => [
    t('calendar.week.mon'),
    t('calendar.week.tue'),
    t('calendar.week.wed'),
    t('calendar.week.thu'),
    t('calendar.week.fri'),
    t('calendar.week.sat'),
    t('calendar.week.sun')
])

// 切换月份 - 上月
const prevMonth = () => {
    if (currentMonth.value === 1) {
        currentMonth.value = 12
        currentYear.value--
    } else {
        currentMonth.value--
    }
    renderCalendar()
}

// 切换月份 - 下月
const nextMonth = () => {
    if (currentMonth.value === 12) {
        currentMonth.value = 1
        currentYear.value++
    } else {
        currentMonth.value++
    }
    renderCalendar()
}

// 选中日历日期
const pickDay = (cell) => {
    if (!cell.disabled) selectDate.value = cell.fullDate
}

// 渲染日历单元格
const renderCalendar = () => {
    const year = currentYear.value
    const month = currentMonth.value
    const today = new Date()
    const tY = today.getFullYear()
    const tM = today.getMonth() + 1
    const tD = today.getDate()

    const firstDate = new Date(year, month - 1, 1)
    const totalDay = new Date(year, month, 0).getDate()
    let firstWeekIdx = firstDate.getDay()
    firstWeekIdx = firstWeekIdx === 0 ? 6 : firstWeekIdx - 1
    const lastPrevMonthDay = new Date(year, month - 1, 0).getDate()

    const daysArr = []
    let keyId = 0

    // 上月占位天数
    for (let i = 0; i < firstWeekIdx; i++) {
        const d = lastPrevMonthDay - firstWeekIdx + i + 1
        daysArr.push({
            key: keyId++,
            day: d,
            fullDate: '',
            disabled: true,
            isToday: false
        })
    }

    // 当月天数
    for (let d = 1; d <= totalDay; d++) {
        const full = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
        daysArr.push({
            key: keyId++,
            day: d,
            fullDate: full,
            disabled: false,
            isToday: tY === year && tM === month && tD === d
        })
    }

    // 填充尾部空白到42格
    const fillNum = 42 - daysArr.length
    for (let d = 1; d <= fillNum; d++) {
        daysArr.push({
            key: keyId++,
            day: d,
            fullDate: '',
            disabled: true,
            isToday: false
        })
    }
    dayList.value = daysArr
}

// 加载文章列表（仅客户端分页/搜索时用，写回 ssrData 触发 computed 更新）
const loadArticleList = async () => {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    if (searchKey.value) params.title = searchKey.value
    const res = await getFrontArticleList(params)
    if (ssrData.value) {
        ssrData.value.articleList = res.data.records || []
        ssrData.value.total = res.data.total || 0
    }
}

// 清空搜索
const clearSearch = () => {
    router.replace({ path: '/front' })
}

// 跳转文章详情
const goDetail = (id) => {
    router.push({ path: '/article', query: { id } })
}

// 监听路由搜索参数变化
watch(route, (to) => {
    searchKey.value = to.query.key || ''
    pageNum.value = 1
    loadArticleList()
})

// SEO
useSeoMeta(buildSeoMeta({
  title: t('hero.seoTitle'),
  description: t('hero.seoDesc')
}))

// 页面挂载初始化（SSR 已预取数据，这里只处理日历渲染）
onMounted(() => {
    searchKey.value = route.query.key || ''
    renderCalendar()
})
</script>

<style scoped>
.front-index {
    width: 100%;
}

.search-tip {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px;
    margin-bottom: 24px;
    font-size: 14px;
    color: var(--color-text-regular);
}

.index-layout {
    display: flex;
    gap: 28px;
    align-items: flex-start;
}

/* 左侧文章区域 */
.article-area {
    flex: 1;
    min-width: 0;
}

.article-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.article-area-header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    margin-bottom: 14px;
}

.area-label {
    font-size: 15px;
    font-weight: 600;
    color: var(--color-text-primary);
}

.readmore-mini {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: var(--color-text-muted);
    text-decoration: none;
    transition: var(--transition-fast);
}

.readmore-mini:hover {
    color: var(--color-primary);
}

/* 右侧侧边栏 */
.side-area {
    width: 280px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    gap: 18px;
    position: sticky;
    top: 88px;
}

.side-card {
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    padding: 18px 20px;
    box-shadow: var(--liquid-glass-shadow);
}

.side-card>* {
    position: relative;
    z-index: 1;
}

.side-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 12px;
    padding-bottom: 10px;
    border-bottom: 1px solid var(--color-border);
}

/* 日历模块 */
.calendar-card {
    padding: 16px 18px;
}

.calendar-month-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 14px;
}

.month-label {
    font-size: 14px;
    font-weight: 600;
    color: var(--color-text-primary);
}

.month-arrow {
    width: 28px;
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    background: transparent;
    color: var(--color-text-muted);
    border-radius: 50%;
    cursor: pointer;
    transition: var(--transition-fast);
}

.month-arrow:hover {
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
}

.calendar-week {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    margin-bottom: 4px;
}

.calendar-week span {
    text-align: center;
    font-size: 11px;
    color: var(--color-text-muted);
    font-weight: 500;
    line-height: 22px;
}

.calendar-days {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 2px;
}

.day-cell {
    aspect-ratio: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    border-radius: 50%;
    cursor: pointer;
    color: var(--color-text-regular);
    transition: var(--transition-fast);
}

.day-cell.disabled {
    visibility: hidden;
}

.day-cell:not(.disabled):hover {
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
}

.day-cell.today {
    font-weight: 700;
    color: var(--color-primary);
    position: relative;
}

.day-cell.today::after {
    content: '';
    position: absolute;
    bottom: 4px;
    left: 50%;
    transform: translateX(-50%);
    width: 4px;
    height: 4px;
    border-radius: 50%;
    background: var(--color-primary);
}

.day-cell.today.selected {
    color: #fff;
}

.day-cell.today.selected::after {
    background: #fff;
}

.day-cell.selected {
    background: var(--color-primary) !important;
    color: #fff !important;
    font-weight: 600;
}

.day-cell.hasArticle:not(.selected):not(.today) {
    font-weight: 600;
    color: var(--color-primary);
}

/* 站点统计 */
.stat-grid {
    display: flex;
    gap: 8px;
}

.stat-item {
    flex: 1;
    text-align: center;
    padding: 12px 4px;
    background: var(--color-primary-light-bg);
    border-radius: var(--radius-md);
}

.stat-num {
    display: block;
    font-size: 20px;
    font-weight: 700;
    color: var(--color-primary);
    line-height: 1.2;
}

.stat-label {
    display: block;
    font-size: 11px;
    color: var(--color-text-muted);
    margin-top: 2px;
}

/* 分类列表 */
.side-list {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.side-link {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 7px 10px;
    border-radius: var(--radius-sm);
    font-size: 13px;
    color: var(--color-text-regular);
    text-decoration: none;
    transition: var(--transition-fast);
}

.side-link:hover {
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
}

.side-count {
    font-size: 11px;
    color: var(--color-text-muted);
    background: var(--color-bg);
    padding: 2px 7px;
    border-radius: 10px;
}

/* 热门文章 */
.hot-list {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.hot-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 7px 10px;
    border-radius: var(--radius-sm);
    font-size: 13px;
    cursor: pointer;
    transition: var(--transition-fast);
}

.hot-item:hover {
    background: var(--color-primary-light-bg);
}

.hot-title {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
    margin-right: 8px;
    color: var(--color-text-regular);
}

.hot-item:hover .hot-title {
    color: var(--color-primary);
}

.hot-views {
    font-size: 11px;
    color: var(--color-text-muted);
    flex-shrink: 0;
}

.empty-tip {
    text-align: center;
    padding: 10px 0;
    font-size: 13px;
    color: var(--color-text-muted);
}

/* 时间轴入口 */
.timeline-entry {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 14px;
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    color: var(--color-primary);
    font-size: 14px;
    font-weight: 500;
    text-decoration: none;
    box-shadow: var(--liquid-glass-shadow);
    transition: var(--transition-fast);
}

.timeline-entry>* {
    position: relative;
    z-index: 1;
}

.timeline-entry:hover {
    background: var(--color-primary-light-bg);
    box-shadow: var(--shadow-card-hover);
}

/* 暗黑模式深度适配 */
:deep(body.dark-mode) .search-tip,
:deep(body.dark-mode) .side-card,
:deep(body.dark-mode) .timeline-entry {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

:deep(body.dark-mode) .side-title {
    border-color: var(--color-dark-border);
}

:deep(body.dark-mode) .side-count {
    background: var(--color-dark-border);
}

:deep(body.dark-mode) .month-arrow:hover {
    background: rgba(91, 79, 207, 0.12);
}

:deep(body.dark-mode) .day-cell.today {
    background: var(--color-primary);
}

/* 移动端响应式 */
@media screen and (max-width: 900px) {
    .index-layout {
        flex-direction: column;
    }

    .side-area {
        width: 100%;
        position: static;
        flex-direction: row;
        flex-wrap: wrap;
        gap: 12px;
    }

    /* 侧边栏卡片在小屏下变成网格排列 */
    .side-card {
        flex: 1 1 calc(50% - 6px);
        min-width: 0;
    }

    .timeline-entry {
        flex: 1 1 100%;
    }

    /* 日历网格单元缩小一点 */
    .day-cell {
        font-size: 11px;
    }
}

@media screen and (max-width: 480px) {
    .side-card {
        flex: 1 1 100%;
    }

    .stat-grid {
        flex-wrap: wrap;
    }

    .stat-item {
        flex: 1 1 30%;
    }
}

/* ===== Hero：极简风格 ===== */
.hero {
    padding: 56px 0 36px;
    border-bottom: 1px solid var(--color-border);
    margin-bottom: 36px;
}

.hero-meta {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 28px;
    font-family: 'JetBrains Mono', 'SF Mono', Menlo, Consolas, monospace;
    font-size: 12px;
    color: var(--color-text-muted);
    letter-spacing: 0.5px;
}

.hero-dot {
    width: 3px;
    height: 3px;
    border-radius: 50%;
    background: var(--color-text-muted);
}

.hero-title {
    font-size: 64px;
    font-weight: 700;
    line-height: 1.05;
    letter-spacing: -2px;
    color: var(--color-text-primary);
    margin: 0 0 20px;
}

.hero-sub {
    font-size: 19px;
    font-weight: 400;
    line-height: 1.5;
    color: var(--color-text-regular);
    margin: 0 0 40px;
    max-width: 540px;
}

.hero-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
}

.hero-stats {
    display: flex;
    gap: 32px;
}

.hero-stat {
    display: inline-flex;
    align-items: baseline;
    gap: 5px;
    font-size: 13px;
    color: var(--color-text-muted);
}

.hero-stat-n {
    color: var(--color-text-primary);
    font-weight: 600;
    font-feature-settings: 'tnum';
    font-size: 14px;
}

.hero-cta {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    font-weight: 500;
    color: var(--color-text-primary);
    text-decoration: none;
    padding: 6px 0;
    border-bottom: 1px solid currentColor;
    transition: gap 0.2s ease;
}

.hero-cta:hover {
    gap: 10px;
    color: var(--color-primary);
}

@media screen and (max-width: 768px) {
    .hero {
        padding: 32px 0 24px;
        margin-bottom: 24px;
    }
    .hero-title {
        font-size: 40px;
        letter-spacing: -1.2px;
    }
    .hero-sub {
        font-size: 16px;
    }
    .hero-footer {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
    }
}

@media screen and (max-width: 480px) {
    .hero-title {
        font-size: 32px;
    }
    .hero-stats {
        gap: 20px;
    }
}

/* 暗黑模式 */
body.dark-mode .hero {
    border-bottom-color: rgba(255, 255, 255, 0.08);
}



::v-deep .el-input__wrapper {
    box-shadow:none;
}

::v-deep .el-input__inner{

    padding: 10px;
}
</style>