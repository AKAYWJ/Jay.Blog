<template>
    <div class="front-index">
        <!-- 搜索结果提示 -->
        <div v-if="searchKey" class="search-tip">
            <span>搜索「{{ searchKey }}」，共 {{ total }} 篇</span>
            <el-button type="text" size="small" @click="clearSearch">清除</el-button>
        </div>

        <div class="index-layout">
            <!-- 左侧：文章列表 -->
            <main class="article-area">
                <div class="article-area-header">
                    <span class="area-label">最新文章</span>
                    <router-link to="/articlelist" class="readmore-mini">
                        浏览全部
                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><polyline points="9 18 15 12 9 6"></polyline></svg>
                    </router-link>
                </div>

                <div class="article-list">
                    <ArticleCard v-for="item in articleList" :key="item.id" :article="item"
                        @click="goDetail(item.id)" />
                </div>
            </main>

            <!-- 右侧栏 -->
            <aside class="side-area">
                <!-- 日历 -->
                <div class="side-card calendar-card">
                    <div class="calendar-month-row">
                        <button class="month-arrow" @click="prevMonth">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="15 18 9 12 15 6"></polyline></svg>
                        </button>
                        <span class="month-label">{{ currentYear }}年{{ currentMonth }}月</span>
                        <button class="month-arrow" @click="nextMonth">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="9 18 15 12 9 6"></polyline></svg>
                        </button>
                    </div>

                    <div class="calendar-week">
                        <span v-for="w in weekShort" :key="w">{{ w }}</span>
                    </div>

                    <div class="calendar-days">
                        <div v-for="d in dayList" :key="d.key" class="day-cell"
                            :class="{
                                disabled: d.disabled,
                                today: d.isToday,
                                hasArticle: articleDateList.includes(d.fullDate),
                                selected: selectDate === d.fullDate
                            }"
                            @click="pickDay(d)">
                            {{ d.day }}
                        </div>
                    </div>
                </div>

                <!-- 站点统计 -->
                <div class="side-card stat-card">
                    <div class="stat-grid">
                        <div class="stat-item">
                            <span class="stat-num">{{ stat.articleTotal || 0 }}</span>
                            <span class="stat-label">文章</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-num">{{ stat.visitorTotal || 0 }}</span>
                            <span class="stat-label">访客</span>
                        </div>
                        <div class="stat-item">
                            <span class="stat-num">{{ stat.commentTotal || 0 }}</span>
                            <span class="stat-label">评论</span>
                        </div>
                    </div>
                </div>

                <!-- 分类 -->
                <div class="side-card">
                    <h3 class="side-title">分类</h3>
                    <div class="side-list">
                        <router-link v-for="c in categoryList" :key="c.id"
                            :to="{ path: '/front/category/all', query: { categoryId: c.id } }"
                            class="side-link">
                            {{ c.name }}
                            <span class="side-count">{{ c.articleCount || 0 }}</span>
                        </router-link>
                    </div>
                </div>

                <!-- 热门文章 -->
                <div class="side-card">
                    <h3 class="side-title">热门文章</h3>
                    <div class="hot-list">
                        <template v-if="hotArticleList && hotArticleList.length > 0">
                            <div v-for="hot in hotArticleList.slice(0, 5)" :key="hot.id" class="hot-item"
                                @click="$router.push({ path: '/article', query: { id: hot.id } })">
                                <span class="hot-title">{{ hot.title }}</span>
                                <span class="hot-views">{{ hot.viewCount || 0 }} 阅读</span>
                            </div>
                        </template>
                        <div v-else class="empty-tip">暂无热门</div>
                    </div>
                </div>

                <!-- 时间轴 -->
                <router-link to="/front/timeline" class="timeline-entry">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polyline points="12 6 12 12 16 14"></polyline>
                    </svg>
                    时间轴
                </router-link>
            </aside>
        </div>
    </div>
</template>

<script>
import ArticleCard from '@/components/ArticleCard.vue'
import { getFrontArticleList, getHotArticle, getBlogStat } from '@/api/front'
import {getCategoryList} from '@/api/category'

export default {
    name: 'FrontIndex',
    components: { ArticleCard },
    data() {
        const now = new Date()
        return {
            searchKey: '',
            pageNum: 1,
            pageSize: 5,
            total: 0,
            articleList: [],
            categoryList: [],
            hotArticleList: [],
            stat: { articleTotal: 0, visitorTotal: 0, commentTotal: 0 },

            /* 日历 */
            currentYear: now.getFullYear(),
            currentMonth: now.getMonth() + 1,
            selectDate: '',
            dayList: [],
            articleDateList: [],
            weekShort: ['一', '二', '三', '四', '五', '六', '日']
        }
    },
    created() {
        this.searchKey = this.$route.query.key || ''
        this.renderCalendar()
    },
    mounted() { this.loadAllData() },
    watch: {
        $route(to) {
            this.searchKey = to.query.key || ''
            this.pageNum = 1
            this.loadArticleList()
        }
    },
    methods: {
        prevMonth() {
            if (this.currentMonth === 1) { this.currentMonth = 12; this.currentYear-- }
            else this.currentMonth--
            this.renderCalendar()
        },
        nextMonth() {
            if (this.currentMonth === 12) { this.currentMonth = 1; this.currentYear++ }
            else this.currentMonth++
            this.renderCalendar()
        },
        pickDay(cell) {
            if (!cell.disabled) this.selectDate = cell.fullDate
        },
        renderCalendar() {
            const year = this.currentYear, month = this.currentMonth
            const today = new Date()
            const tY = today.getFullYear(), tM = today.getMonth() + 1, tD = today.getDate()
            const firstDate = new Date(year, month - 1, 1)
            const totalDay = new Date(year, month, 0).getDate()
            let firstWeekIdx = firstDate.getDay()
            firstWeekIdx = firstWeekIdx === 0 ? 6 : firstWeekIdx - 1
            const lastPrevMonthDay = new Date(year, month - 1, 0).getDate()
            const daysArr = []
            let keyId = 0
            for (let i = 0; i < firstWeekIdx; i++) {
                const d = lastPrevMonthDay - firstWeekIdx + i + 1
                daysArr.push({ key: keyId++, day: d, fullDate: '', disabled: true, isToday: false })
            }
            for (let d = 1; d <= totalDay; d++) {
                const full = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`
                daysArr.push({ key: keyId++, day: d, fullDate: full, disabled: false, isToday: tY === year && tM === month && tD === d })
            }
            const fillNum = 42 - daysArr.length
            for (let d = 1; d <= fillNum; d++) {
                daysArr.push({ key: keyId++, day: d, fullDate: '', disabled: true, isToday: false })
            }
            this.dayList = daysArr
        },
        async loadAllData() {
            try {
                const cateRes = await getCategoryList()
                this.categoryList = cateRes.data || []
                await this.loadArticleList()
                const hotRes = await getHotArticle()
                this.hotArticleList = hotRes.data || []
                const statRes = await getBlogStat()
                this.stat = statRes.data || {}
            } catch (err) { console.error('首页加载失败', err) }
        },
        async loadArticleList() {
            const params = { pageNum: this.pageNum, pageSize: this.pageSize }
            if (this.searchKey) params.title = this.searchKey
            const res = await getFrontArticleList(params)
            this.articleList = res.data.records || []
            this.total = res.data.total || 0
        },
        clearSearch() { this.$router.replace({ path: '/front/index' }) },
        goDetail(id) { this.$router.push({ path: '/article', query: { id } }) }
    }
}
</script>

<style scoped>
.front-index { width: 100%; }

.search-tip {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px;
    margin-bottom: 24px;
    font-size: 14px;
    color: var(--color-text-regular);
}

.search-tip > * {
    position: relative;
    z-index: 1;
}

/* ===== 两栏布局 ===== */
.index-layout {
    display: flex;
    gap: 28px;
    align-items: flex-start;
}

/* ===== 左侧文章区 ===== */
.article-area {
    flex: 1;
    min-width: 0;
}
.article-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

/* 文章区头部：标题 + 浏览全部小字 */
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

/* ===== 右侧栏 ===== */
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

.side-card > * {
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

/* ---- 日历 ---- */
.calendar-card { padding: 16px 18px; }

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

.day-cell.disabled { visibility: hidden; }

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

/* 今天 + 选中 叠加时，恢复为选中态 */
.day-cell.today.selected { color: #fff; }
.day-cell.today.selected::after { background: #fff; }

.day-cell.selected {
    background: var(--color-primary) !important;
    color: #fff !important;
    font-weight: 600;
}

.day-cell.hasArticle:not(.selected):not(.today) {
    font-weight: 600;
    color: var(--color-primary);
}

/* ---- 统计 ---- */
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

/* ---- 分类 ---- */
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

/* ---- 热门文章 ---- */
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

.hot-item:hover { background: var(--color-primary-light-bg); }

.hot-title {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
    margin-right: 8px;
    color: var(--color-text-regular);
}

.hot-item:hover .hot-title { color: var(--color-primary); }

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

/* ---- 时间轴入口 ---- */
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

.timeline-entry > * {
    position: relative;
    z-index: 1;
}

.timeline-entry:hover {
    background: var(--color-primary-light-bg);
    box-shadow: var(--shadow-card-hover);
}

/* ===== 暗黑模式 ===== */
body.dark-mode .search-tip,
body.dark-mode .side-card,
body.dark-mode .timeline-entry {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

body.dark-mode .side-title { border-color: var(--color-dark-border); }
body.dark-mode .side-count { background: var(--color-dark-border); }
body.dark-mode .month-arrow:hover { background: rgba(91, 79, 207, 0.12); }
body.dark-mode .day-cell.today { background: var(--color-primary); }

/* ===== 响应式 ===== */
@media screen and (max-width: 900px) {
    .side-area { display: none; }
}
</style>
