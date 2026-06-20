<template>
    <div class="front-index">
        <!-- 搜索结果提示 -->
        <div v-if="searchKey" class="search-tip">
            搜索「{{ searchKey }}」的结果，共 {{ total }} 篇
            <el-button type="text" size="small" @click="clearSearch">清除搜索</el-button>
        </div>

        <el-row :gutter="24">
            <!-- 左侧 20% 日历+统计 -->
            <el-col :span="5">
                <div class="side-card calendar-card">
                    <h3 class="card-title">文章日历</h3>
                    <!-- 对标设计图 纯手写日历 -->
                    <div class="calendar-box">
                        <!-- 顶部年月下拉行 -->
                        <div class="calendar-header">
                            <div class="select-wrap">
                                <div class="select-trigger" @click="monthDropdownShow = !monthDropdownShow">
                                    {{ monthList[currentMonth - 1] }} ▾
                                </div>
                                <div v-if="monthDropdownShow" class="dropdown-panel month-drop" @click.stop>
                                    <div v-for="m in monthList" :key="m" class="drop-item"
                                        :class="{active: m === monthList[currentMonth - 1]}"
                                        @click="pickMonth(m)">
                                        {{ m }}
                                    </div>
                                </div>
                            </div>
                            <div class="select-wrap">
                                <div class="select-trigger" @click="yearDropdownShow = !yearDropdownShow">
                                    {{ currentYear }} ▾
                                </div>
                                <div v-if="yearDropdownShow" class="dropdown-panel year-drop" @click.stop>
                                    <div v-for="y in yearRange" :key="y" class="drop-item"
                                        :class="{active: y === currentYear}"
                                        @click="pickYear(y)">
                                        {{ y }}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 星期表头 Mo Tu We Th Fr Sa Su -->
                        <div class="calendar-week">
                            <span v-for="w in weekShort" :key="w">{{ w }}</span>
                        </div>

                        <!-- 日期网格 -->
                        <div class="calendar-days">
                            <div v-for="d in dayList" :key="d.key" class="day-cell"
                                :class="{
                                    disabled: d.disabled,
                                    today: d.isToday,
                                    hasArticle: articleDateList.includes(d.fullDate),
                                    selectedDay: selectDate === d.fullDate
                                }"
                                @click="pickDay(d)">
                                {{ d.day }}
                            </div>
                        </div>
                    </div>
                </div>
                <div class="side-card stat-card">
                    <h3 class="card-title">全站统计</h3>
                    <div class="stat-item">文章总数：{{ stat.articleTotal }}</div>
                    <div class="stat-item">访客总数：{{ stat.visitorTotal }}</div>
                    <div class="stat-item">评论总数：{{ stat.commentTotal }}</div>
                </div>
            </el-col>

            <!-- 中间 60% 文章主列表 -->
            <el-col :span="14">
                <div class="article-list">
                    <ArticleCard v-for="item in articleList" :key="item.id" :article="item"
                        @click="goDetail(item.id)" />
                </div>
                <!-- 分页 -->
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pageNum" :page-sizes="[3, 6, 9]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" class="pagination" />
            </el-col>

            <!-- 右侧 20% 侧边栏卡片 -->
            <el-col :span="5">
                <!-- 分类卡片 -->
                <div class="side-card">
                    <h3 class="card-title">文章分类</h3>
                    <div class="category-list">
                        <div v-for="c in categoryList" :key="c.id" class="category-item"
                            @click="$router.push({ path: '/front/category/all', query: { categoryId: c.id } })">
                            {{ c.name }}
                        </div>
                    </div>
                </div>
                <!-- 热门文章 -->
                <div class="side-card">
                    <h3 class="card-title">热门文章</h3>
                    <div class="hot-list">
                        <template v-if="hotArticleList && hotArticleList.length > 0">
                            <div v-for="hot in hotArticleList.slice(0, 2)" :key="hot.id" class="hot-item"
                                @click="$router.push({ path: '/front/article', query: { id: hot.id } })">
                                <span class="hot-title">{{ hot.title }}</span>
                                <span class="view-num">👁 {{ hot.viewCount }}</span>
                            </div>
                        </template>
                        <div v-else class="empty-tip">暂无热门文章</div>
                    </div>
                </div>
                <!-- 关于本站 -->
                <div class="side-card">
                    <h3 class="card-title">关于本站</h3>
                    <p class="about-desc">{{ blogConfig.about }}</p>
                    <div class="contact-line">
                        <span>QQ：{{ blogConfig.qq }}</span>
                    </div>
                    <div class="contact-line github-row">
                        <span>Github：</span>
                        <span class="github-full">{{ blogConfig.github }}</span>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import ArticleCard from '@/components/ArticleCard.vue'
import { getFrontArticleList, getFrontCategory, getHotArticle, getBlogStat, getBlogConfig } from '@/api/front'

export default {
    name: 'FrontIndex',
    components: { ArticleCard },
    data() {
        const now = new Date()
        const curY = now.getFullYear()
        const curM = now.getMonth() + 1
        return {
            searchKey: '',
            pageNum: 1,
            pageSize: 3,
            total: 0,
            articleList: [],
            categoryMap: {},
            categoryList: [],
            hotArticleList: [],
            stat: {
                articleTotal: 0,
                visitorTotal: 0,
                commentTotal: 0
            },
            blogConfig: {
                about: '',
                qq: '',
                github: ''
            },

            // ========== 日历核心数据（对标设计图） ==========
            currentYear: curY,
            currentMonth: curM,
            selectDate: '', // 用户选中日期 YYYY-MM-DD
            dayList: [],
            articleDateList: [], // 后端返回有文章的日期数组

            // 下拉面板开关
            monthDropdownShow: false,
            yearDropdownShow: false,
            // 月份文字
            monthList: ['January','February','March','April','May','June','July','August','September','October','November','December'],
            weekShort: ['Mo','Tu','We','Th','Fr','Sa','Su'],
            // 年份区间 前后10年
            yearRange: Array.from({length:21}, (_,i)=>curY-10+i)
        }
    },
    created() {
        this.searchKey = this.$route.query.key || ''
        this.renderCalendar()
        // 点击空白关闭下拉
        document.addEventListener('click', this.closeAllDrop)
    },
    beforeDestroy() {
        document.removeEventListener('click', this.closeAllDrop)
    },
    mounted() {
        this.loadAllData()
    },
    watch: {
        $route(to) {
            this.searchKey = to.query.key || ''
            this.pageNum = 1
            this.loadArticleList()
        }
    },
    methods: {
        // 关闭年月下拉
        closeAllDrop() {
            this.monthDropdownShow = false
            this.yearDropdownShow = false
        },
        // 选择月份
        pickMonth(name) {
            this.currentMonth = this.monthList.findIndex(m=>m===name)+1
            this.monthDropdownShow = false
            this.renderCalendar()
        },
        // 选择年份
        pickYear(y) {
            this.currentYear = y
            this.yearDropdownShow = false
            this.renderCalendar()
        },
        // 点击日期选中
        pickDay(cell) {
            if(cell.disabled) return
            this.selectDate = cell.fullDate
        },

        // 渲染日历网格（对标设计图布局）
        renderCalendar() {
            const year = this.currentYear
            const month = this.currentMonth
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

            // 上月灰色占位
            for(let i=0; i<firstWeekIdx; i++){
                const d = lastPrevMonthDay - firstWeekIdx + i + 1
                daysArr.push({
                    key: keyId++,
                    day: d,
                    fullDate: `${year}-${String(month-1).padStart(2,'0')}-${String(d).padStart(2,'0')}`,
                    disabled: true,
                    isToday: false
                })
            }
            // 当月日期
            for(let d=1; d<=totalDay; d++){
                const full = `${year}-${String(month).padStart(2,'0')}-${String(d).padStart(2,'0')}`
                daysArr.push({
                    key: keyId++,
                    day: d,
                    fullDate: full,
                    disabled: false,
                    isToday: (tY===year && tM===month && tD===d)
                })
            }
            // 补足6行42格
            const fillNum = 42 - daysArr.length
            for(let d=1; d<=fillNum; d++){
                daysArr.push({
                    key: keyId++,
                    day: d,
                    fullDate: `${year}-${String(month+1).padStart(2,'0')}-${String(d).padStart(2,'0')}`,
                    disabled: true,
                    isToday: false
                })
            }
            this.dayList = daysArr
        },

        // 首页数据加载
        async loadAllData() {
            try {
                const cateRes = await getFrontCategory()
                this.categoryList = cateRes.data
                this.categoryMap = {}
                cateRes.data.forEach(item=>this.categoryMap[item.id]=item.name)
                await this.loadArticleList()
                const hotRes = await getHotArticle()
                this.hotArticleList = hotRes.data || []
                const statRes = await getBlogStat()
                this.stat = statRes.data
                const configRes = await getBlogConfig()
                this.blogConfig = configRes.data
            } catch (err) {
                console.error('首页加载失败',err)
            }
        },
        async loadArticleList() {
            const params = {pageNum:this.pageNum,pageSize:this.pageSize}
            if(this.searchKey) params.title = this.searchKey
            const res = await getFrontArticleList(params)
            this.articleList = res.data.records.map(item=>({
                ...item,
                categoryName: this.categoryMap[item.categoryId] || '未分类'
            }))
            this.total = res.data.total
        },
        clearSearch() {
            this.$router.replace({path:'/front/index'})
        },
        handleCurrentChange(v) {
            this.pageNum = v
            this.loadArticleList()
        },
        handleSizeChange(v) {
            this.pageSize = v
            this.pageNum = 1
            this.loadArticleList()
        },
        goDetail(id) {
            this.$router.push({path:'/front/article',query:{id}})
        }
    }
}
</script>

<style scoped>
.front-index {
    width: 100%;
}
.search-tip {
    background: #fff;
    padding: 12px 20px;
    border-radius: 12px;
    margin-bottom: 20px;
    font-size: 14px;
    color: #555;
    display: flex;
    align-items: center;
    justify-content: space-between;
    box-shadow: 0 3px 14px rgba(99, 85, 216, 0.07);
}
.side-card {
    background: #fff;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 3px 14px rgba(99, 85, 216, 0.07);
    margin-bottom: 24px;
}
.card-title {
    font-size: 17px;
    font-weight: 600;
    color: #1c1c36;
    margin: 0 0 16px;
    padding-bottom: 10px;
    border-bottom: 1px solid #f3f3fb;
}
.stat-item {
    line-height: 2.2;
    color: #555;
    font-size: 14px;
}
.category-item {
    line-height: 2.2;
    font-size: 14px;
    color: #555;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
}
.category-item:hover {
    color: #6355d8;
}
.hot-item {
    line-height: 2.1;
    font-size: 14px;
    color: #555;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    gap: 6px;
}
.hot-title {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.hot-item:hover .hot-title {
    color: #6355d8;
}
.view-num {
    color: #999;
    font-size: 12px;
    flex-shrink: 0;
}
.empty-tip {
    text-align: center;
    padding: 12px 0;
    font-size: 14px;
    color: #aaa;
}
.about-desc {
    font-size: 14px;
    color: #666;
    line-height: 1.7;
    margin-bottom: 12px;
}
.pagination {
    margin-top: 30px;
    text-align: center;
}
.contact-line {
    font-size: 13px;
    color: #777;
    margin: 6px 0;
}
.github-row {
    display: flex;
    align-items: flex-start;
    gap: 4px;
}
.github-full {
    word-break: break-all;
    line-break: anywhere;
    color: #6355d8;
}

/* ==================== 对标参考图 日历样式核心 ==================== */
.calendar-box {
    width: 100%;
    position: relative;
}
/* 顶部年月双下拉 */
.calendar-header {
    display: flex;
    gap: 10px;
    justify-content: flex-start;
    margin-bottom: 10px;
}
.select-wrap {
    position: relative;
}
.select-trigger {
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 6px;
    background: #f5f5fc;
    color: #333;
    cursor: pointer;
    transition: 0.2s;
}
.select-trigger:hover {
    background: #e9e6fa;
}
/* 下拉弹出面板 参考图侧边悬浮 */
.dropdown-panel {
    position: absolute;
    top: 110%;
    left: 0;
    background: #fff;
    border-radius: 10px;
    box-shadow: 0 6px 20px rgba(0,0,0,0.1);
    z-index: 99;
    overflow: hidden;
}
.month-drop {
    width: 110px;
}
.year-drop {
    width: 70px;
    max-height: 200px;
    overflow-y: auto;
}
.drop-item {
    padding: 6px 12px;
    font-size: 12px;
    cursor: pointer;
    color: #444;
}
.drop-item:hover {
    background: #f5f5fc;
}
.drop-item.active {
    background: #6355d8;
    color: #fff;
}
/* 星期表头 Mo Tu... */
.calendar-week {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    margin-bottom: 4px;
}
.calendar-week span {
    text-align: center;
    font-size: 11px;
    color: #999;
    line-height: 22px;
}
/* 日期网格 7列 */
.calendar-days {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 2px;
}
.day-cell {
    height: 28px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.2s ease;
}
/* 上月/下月灰色禁用 */
.day-cell.disabled {
    color: #ddd;
    background: transparent;
    cursor: default;
}
/* 普通日期hover */
.day-cell:not(.disabled):hover {
    background: #f5f5fc;
}
/* 今日：底部小圆点标记（参考图样式） */
.day-cell.today {
    position: relative;
    color: #222;
}
.day-cell.today::after {
    content: '';
    width: 4px;
    height: 4px;
    background: #6355d8;
    border-radius: 50%;
    position: absolute;
    bottom: 3px;
}
/* 有文章日期 淡紫底色 */
.day-cell.hasArticle {
    background: rgba(99, 85, 216, 0.11);
    color: #6355d8;
    font-weight: 500;
}
/* 选中日期：大圆形紫色填充（参考图核心视觉） */
.day-cell.selectedDay {
    background: #6355d8 !important;
    color: #fff !important;
    font-weight: 500;
}
</style>