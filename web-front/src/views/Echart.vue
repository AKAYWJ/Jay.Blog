<template>
    <div class="chart-page">
        <!-- 页面标题 -->
        <div class="page-header">
            <h1 class="page-title">数据图表</h1>
            <p class="page-subtitle">博客全站数据统计与访问趋势概览</p>
        </div>

        <!-- 核心指标卡片：累计访客 / 文章总数 / 评论总数（去掉点赞） -->
        <el-row :gutter="20" class="row-section">
            <el-col :span="8">
                <div class="stat-card">
                    <div class="card-header">
                        <span class="card-title">累计访客数量</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">{{ visitorTotal }}<span class="unit">人</span></div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="stat-card">
                    <div class="card-header">
                        <span class="card-title">文章总数量</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">{{ articleTotal }}<span class="unit">篇</span></div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="stat-card">
                    <div class="card-header">
                        <span class="card-title">评论总数量</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">{{ commentTotal }}<span class="unit">条</span></div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 第一行：各分类文章占比饼图 + 热门标签环形图 -->
        <el-row :gutter="20" class="row-section">
            <el-col :span="12">
                <div class="chart-card">
                    <div class="card-header">
                        <span class="card-title">各分类文章占比</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="chart-wrapper">
                        <div ref="pieChart" class="chart-box"></div>
                    </div>
                </div>
            </el-col>
            <el-col :span="12">
                <div class="chart-card">
                    <div class="card-header">
                        <span class="card-title">热门标签分布</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="chart-wrapper">
                        <div ref="ringChart" class="chart-box"></div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 第二行：文章浏览TOP表格 -->
        <el-row :gutter="20" class="row-section">
            <el-col :span="24">
                <div class="table-card">
                    <div class="card-header">
                        <span class="card-title">文章浏览量排行</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="table-wrapper">
                        <el-table :data="articleRankData" border stripe size="small">
                            <el-table-column prop="index" label="序号" width="60" align="center" />
                            <el-table-column prop="title" label="文章标题" align="left" />
                            <el-table-column prop="viewCount" label="浏览量" align="center" />
                        </el-table>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 第三行：访客增长折线图 -->
        <el-row :gutter="20" class="row-section">
            <el-col :span="24">
                <div class="chart-card">
                    <div class="card-header">
                        <span class="card-title">访客增长趋势（近30天）</span>
                        <span class="update-time">更新于 {{ todayTime }}</span>
                    </div>
                    <div class="chart-wrapper">
                        <div ref="lineChart" class="chart-box"></div>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
import * as echarts from 'echarts'
// 复用已存在的接口，不新增任何后端接口
import { getVisitorList, getDailyVisitorData } from '@/api/visitor'
import { getArticleList } from '@/api/article'
import { getCommentList } from '@/api/comment'
import { getCategoryList } from '@/api/category'
import { getTagList } from '@/api/tag'

export default {
    name: 'Echart',
    data() {
        return {
            todayTime: '',
            // 顶部统计数字
            visitorTotal: 0,
            articleTotal: 0,
            commentTotal: 0,
            // 图表原始数据
            categoryData: [],
            tagData: [],
            articleRankData: [],
            // 折线图专用
            xAxisData: [],
            lineSeriesData: [],
            // echarts实例
            pieInstance: null,
            ringInstance: null,
            lineInstance: null
        }
    },
    watch: {
        // 分类数据加载完成自动渲染饼图
        categoryData: {
            handler(val) {
                if (val && val.length > 0) {
                    this.initPieChart()
                }
            },
            deep: true
        },
        // 标签数据加载完成渲染环形图
        tagData: {
            handler(val) {
                if (val && val.length > 0) {
                    this.initRingChart()
                }
            },
            deep: true
        },
        // 折线数据加载完成渲染折线图
        lineSeriesData: {
            handler(val) {
                if (val && val.length > 0) {
                    this.initLineChart()
                }
            },
            deep: true
        }
    },
    mounted() {
        // 当前更新时间
        const now = new Date()
        this.todayTime = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:00`

        // 先加载全部后台数据，数据监听自动渲染图表
        this.loadAllExistData()
        this.loadVisitorLineData()
    },
    beforeDestroy() {
        // 销毁全部echarts实例，释放内存
        if (this.pieInstance) this.pieInstance.dispose()
        if (this.ringInstance) this.ringInstance.dispose()
        if (this.lineInstance) this.lineInstance.dispose()
    },
    methods: {
        // 全部复用现有接口，不新增后端接口
        async loadAllExistData() {
            // 1. 访客总数
            const visitorRes = await getVisitorList({ pageNum: 1, pageSize: 1 })
            this.visitorTotal = visitorRes.data.total

            // 2. 文章总数 + 浏览排行
            const articleRes = await getArticleList({ pageNum: 1, pageSize: 100 })
            this.articleTotal = articleRes.data.total
            // 按浏览量倒序取前10
            this.articleRankData = articleRes.data.records
                .sort((a, b) => b.viewCount - a.viewCount)
                .slice(0, 10)
                .map((item, idx) => ({
                    index: idx + 1,
                    title: item.title,
                    viewCount: item.viewCount
                }))

            // 3. 评论总数
            const commentRes = await getCommentList({ pageNum: 1, pageSize: 1 })
            this.commentTotal = commentRes.data.total

            // 4. 分类数据（饼图）
            const categoryRes = await getCategoryList({ pageNum: 1, pageSize: 100 })
            this.categoryData = categoryRes.data

            // 5. 标签数据（环形图）
            const tagRes = await getTagList({ pageNum: 1, pageSize: 100 })
            this.tagData = tagRes.data.records
        },
        // 加载折线图每日访客数据
        async loadVisitorLineData() {
            try {
                const res = await getDailyVisitorData()
                const list = res.data
                this.xAxisData = []
                this.lineSeriesData = []
                list.forEach(item => {
                    this.xAxisData.push(item.day)
                    this.lineSeriesData.push(item.count)
                })
            } catch (err) {
                console.log('加载访客折线数据失败', err)
            }
        },
        // 分类饼图
        initPieChart() {
            const chartDom = this.$refs.pieChart
            if (!chartDom) return
            if (!this.categoryData || this.categoryData.length === 0) return

            // 销毁上一次实例，防止叠加灰色
            if (this.pieInstance) {
                this.pieInstance.dispose()
            }
            const chart = echarts.init(chartDom)
            this.pieInstance = chart

            const pieData = this.categoryData.map(item => ({
                name: item.name,
                value: item.articleCount
            }))

            const option = {
                color: ['#6355d8', '#409eff', '#e6a23c', '#f56c6c', '#67c23a', '#722ed1', '#13c2c2'],
                tooltip: { trigger: 'item', formatter: '{b}: {c}篇 ({d}%)' },
                series: [
                    {
                        type: 'pie',
                        radius: ['40%', '70%'],
                        avoidLabelOverlap: false,
                        label: { show: false },
                        // 白色分割边框，均分数据也能分开色块
                        itemStyle: {
                            borderWidth: 2,
                            borderColor: '#fff'
                        },
                        emphasis: {
                            label: { show: true, fontSize: 14, fontWeight: 'bold' }
                        },
                        data: pieData
                    }
                ]
            }
            chart.setOption(option, true)

            // resize只绑定一次
            const resizeFn = () => {
                if (this.pieInstance) this.pieInstance.resize()
            }
            window.removeEventListener('resize', resizeFn)
            window.addEventListener('resize', resizeFn)
        },

        // 标签环形图（修复灰色、无实例销毁、统一配色）
        initRingChart() {
            const chartDom = this.$refs.ringChart
            if (!chartDom) return
            if (!this.tagData || this.tagData.length === 0) return

            if (this.ringInstance) {
                this.ringInstance.dispose()
            }
            const chart = echarts.init(chartDom)
            this.ringInstance = chart

            // 有articleCount用真实数值，无则默认1
            const ringData = this.tagData.map(item => ({
                name: item.name,
                value: item.articleCount || 1
            }))
            const option = {
                color: ['#6355d8', '#409eff', '#e6a23c', '#f56c6c', '#67c23a', '#722ed1', '#13c2c2'],
                tooltip: { trigger: 'item', formatter: '{b}: {c}篇 ({d}%)' },
                series: [
                    {
                        type: 'pie',
                        radius: ['70%', '90%'],
                        avoidLabelOverlap: false,
                        label: { show: false },
                        itemStyle: {
                            borderWidth: 2,
                            borderColor: '#fff'
                        },
                        emphasis: {
                            label: { show: true, fontSize: 14, fontWeight: 'bold' }
                        },
                        data: ringData
                    }
                ]
            }
            chart.setOption(option, true)

            const resizeFn = () => {
                if (this.ringInstance) this.ringInstance.resize()
            }
            window.removeEventListener('resize', resizeFn)
            window.addEventListener('resize', resizeFn)
        },

        // 访客折线图（纯后端真实数据，无模拟写死）
        initLineChart() {
            const chartDom = this.$refs.lineChart
            if (!chartDom) return
            if (!this.xAxisData || this.xAxisData.length === 0) return

            // 销毁旧实例，防止重叠异常
            if (this.lineInstance) {
                this.lineInstance.dispose()
            }
            const chart = echarts.init(chartDom)
            this.lineInstance = chart

            const option = {
                tooltip: { trigger: 'axis' },
                grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: this.xAxisData
                },
                yAxis: { type: 'value' },
                series: [
                    {
                        name: '访客数',
                        type: 'line',
                        smooth: true,
                        lineStyle: { color: '#008CFF' },
                        areaStyle: { color: 'rgba(0, 140, 255, 0.1)' },
                        data: this.lineSeriesData
                    }
                ]
            }
            chart.setOption(option, true)

            // 窗口自适应
            const resizeHandler = () => {
                if (this.lineInstance) this.lineInstance.resize()
            }
            window.removeEventListener('resize', resizeHandler)
            window.addEventListener('resize', resizeHandler)
        },
    }
}
</script>

<style scoped>
/* 全局背景 */
.chart-page {
    background-color: var(--color-chart-bg);
    min-height: 100vh;
    padding: var(--spacing-page);
}

/* 页面标题 */
.page-header {
    margin-bottom: var(--spacing-gap-lg);
}

.page-title {
    font-size: var(--font-size-page-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 8px;
}

.page-subtitle {
    font-size: var(--font-size-body);
    color: var(--color-text-placeholder);
    margin: 0;
}

/* 通用卡片样式 */
.row-section {
    margin-bottom: var(--spacing-gap);
}

.stat-card,
.chart-card,
.table-card {
    background: var(--color-white);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-card);
    padding: var(--spacing-card);
    height: 100%;
}

/* 卡片头部 */
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.card-title {
    font-size: var(--font-size-body);
    font-weight: 500;
    color: var(--color-text-primary);
}

.update-time {
    font-size: 12px;
    color: var(--color-text-placeholder);
}

/* 数字卡片样式 */
.stat-content {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 120px;
}

.stat-number {
    font-size: 32px;
    font-weight: 600;
    color: var(--color-text-primary);
}

.stat-number .unit {
    font-size: var(--font-size-body);
    font-weight: 400;
    color: var(--color-text-aux);
    margin-left: 4px;
}

/* 图表容器 */
.chart-wrapper {
    height: 220px;
}

.chart-box {
    width: 100%;
    height: 100%;
}

/* 表格容器 */
.table-wrapper {
    height: 200px;
    overflow: auto;
}

/* 暗黑模式适配 */
body.dark-mode .chart-page {
    background-color: var(--color-dark-bg-alt);
}

body.dark-mode .stat-card,
body.dark-mode .chart-card,
body.dark-mode .table-card {
    background-color: var(--color-dark-surface-alt);
}

body.dark-mode .card-title {
    color: var(--color-dark-text-regular);
}

body.dark-mode .stat-number {
    color: var(--color-dark-text-regular);
}
</style>