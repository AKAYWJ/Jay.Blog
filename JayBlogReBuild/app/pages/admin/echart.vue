<template>
  <div class="chart-page">
    <div class="page-header">
      <h1 class="page-title">{{ t('admin.echart.title') }}</h1>
      <p class="page-subtitle">{{ t('admin.echart.subtitle') }}</p>
    </div>

    <el-row :gutter="20" class="row-section">
      <el-col :span="8">
        <div class="stat-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.visitorTotal') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ visitorTotal }}<span class="unit">{{ t('admin.echart.unitPerson') }}</span></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.articleTotal') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ articleTotal }}<span class="unit">{{ t('admin.echart.unitArticle') }}</span></div>
          </div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.commentTotal') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ commentTotal }}<span class="unit">{{ t('admin.echart.unitComment') }}</span></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="row-section">
      <el-col :span="12">
        <div class="chart-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.categoryPie') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="chart-wrapper">
            <div ref="pieChart" class="chart-box"></div>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.tagDistribution') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="chart-wrapper">
            <div ref="ringChart" class="chart-box"></div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="row-section">
      <el-col :span="24">
        <div class="table-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.articleRank') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="table-wrapper">
            <el-table :data="articleRankData" border stripe size="small">
              <el-table-column prop="index" :label="t('admin.echart.colIndex')" width="60" align="center" />
              <el-table-column prop="title" :label="t('admin.echart.colTitle')" align="left" />
              <el-table-column prop="viewCount" :label="t('admin.echart.colViews')" align="center" />
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="row-section">
      <el-col :span="24">
        <div class="chart-card">
          <div class="card-header">
            <span class="card-title">{{ t('admin.echart.visitorTrend') }}</span>
            <span class="update-time">{{ t('admin.echart.updatedAt', { time: todayTime }) }}</span>
          </div>
          <div class="chart-wrapper">
            <div ref="lineChart" class="chart-box"></div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue'
import { getVisitorList, getDailyVisitorData } from '~/api/visitor'
import { getArticleList } from '~/api/article'
import { getCommentList } from '~/api/comment'
import { getFrontCategoryList } from '~/api/category'
import { getFrontTagList } from '~/api/tag'

// echarts 体积大（~200KB gzip），异步导入
const echartsPromise = import('echarts')

// 暗色模式：监听 + 重渲染图表
const { isDark } = useDarkMode()

const { t } = useI18n()

const todayTime = ref('')
const visitorTotal = ref(0)
const articleTotal = ref(0)
const commentTotal = ref(0)
const categoryData = ref([])
const tagData = ref([])
const articleRankData = ref([])
const xAxisData = ref([])
const lineSeriesData = ref([])

const pieChart = ref(null)
const ringChart = ref(null)
const lineChart = ref(null)

let pieInstance = null
let ringInstance = null
let lineInstance = null

// 全局缓存 echarts 实例（在 onMounted 触发异步加载）
let echartsLib: typeof import('echarts') | null = null

// 主题色：跟随暗色模式
const chartPalette = ['#5B4FCF', '#409eff', '#e6a23c', '#f56c6c', '#67c23a', '#722ed1', '#13c2c2']
const chartTheme = computed(() => ({
  dark: isDark.value,
  // 文字/轴/分割线颜色
  text: isDark.value ? '#a0a0b8' : '#666',
  axis: isDark.value ? 'rgba(255,255,255,0.08)' : '#eee',
  tooltipBg: isDark.value ? 'rgba(30,27,46,0.95)' : 'rgba(255,255,255,0.95)',
  tooltipBorder: isDark.value ? 'rgba(255,255,255,0.08)' : '#eee'
}))

// 主题变化时重新渲染所有图表
watch(chartTheme, () => {
  if (categoryData.value?.length) initPieChart()
  if (tagData.value?.length) initRingChart()
  if (lineSeriesData.value?.length) initLineChart()
})

watch(categoryData, (val) => {
  if (val && val.length > 0) initPieChart()
}, { deep: true })

watch(tagData, (val) => {
  if (val && val.length > 0) initRingChart()
}, { deep: true })

watch(lineSeriesData, (val) => {
  if (val && val.length > 0) initLineChart()
}, { deep: true })

onMounted(async () => {
  // 异步加载 echarts（不阻塞首屏）
  echartsLib = (await echartsPromise).default ?? (await echartsPromise)

  const now = new Date()
  todayTime.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:00`
  loadAllExistData()
  loadVisitorLineData()
})

onBeforeUnmount(() => {
  if (pieInstance) pieInstance.dispose()
  if (ringInstance) ringInstance.dispose()
  if (lineInstance) lineInstance.dispose()
})

const loadAllExistData = async () => {
  const visitorRes = await getVisitorList({ pageNum: 1, pageSize: 1 })
  visitorTotal.value = visitorRes.data.total

  const articleRes = await getArticleList({ pageNum: 1, pageSize: 100 })
  articleTotal.value = articleRes.data.total
  articleRankData.value = articleRes.data.records
    .sort((a, b) => b.viewCount - a.viewCount)
    .slice(0, 10)
    .map((item, idx) => ({
      index: idx + 1,
      title: item.title,
      viewCount: item.viewCount
    }))

  const commentRes = await getCommentList({ pageNum: 1, pageSize: 1 })
  commentTotal.value = commentRes.data.total

  const categoryRes = await getFrontCategoryList()
  // 前台接口返回 [{id, name, articleCount}]；统一成图表需要的格式
  categoryData.value = (categoryRes.data || []).map(item => ({
    name: item.name,
    articleCount: item.articleCount
  }))

  const tagRes = await getFrontTagList()
  // 标签带 articleCount；过滤掉 0 篇的避免脏数据
  tagData.value = (tagRes.data || []).filter(item => item.articleCount > 0)
}

const loadVisitorLineData = async () => {
  try {
    const res = await getDailyVisitorData()
    const list = res.data
    xAxisData.value = []
    lineSeriesData.value = []
    list.forEach(item => {
      xAxisData.value.push(item.day)
      lineSeriesData.value.push(item.count)
    })
  } catch (err) {
    console.log(t('admin.echart.loadFail'), err)
  }
}

const initPieChart = () => {
  const chartDom = pieChart.value
  if (!chartDom) return
  if (!categoryData.value || categoryData.value.length === 0) return
  if (pieInstance) pieInstance.dispose()
  const chart = echartsLib!.init(chartDom)
  pieInstance = chart
  const pieData = categoryData.value.map(item => ({
    name: item.name,
    value: item.articleCount
  }))
  const theme = chartTheme.value
  chart.setOption({
    color: chartPalette,
    tooltip: {
      trigger: 'item',
      formatter: t('admin.echart.tooltipFormat'),
      backgroundColor: theme.tooltipBg,
      borderColor: theme.tooltipBorder,
      textStyle: { color: theme.text }
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      label: { show: false, color: theme.text },
      labelLine: { lineStyle: { color: theme.text } },
      itemStyle: { borderWidth: 2, borderColor: theme.dark ? '#1e1b2e' : '#fff' },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold', color: theme.text } },
      data: pieData
    }]
  }, true)
}

const initRingChart = () => {
  const chartDom = ringChart.value
  if (!chartDom) return
  if (!tagData.value || tagData.value.length === 0) return
  if (ringInstance) ringInstance.dispose()
  const chart = echartsLib!.init(chartDom)
  ringInstance = chart
  const ringData = tagData.value.map(item => ({
    name: item.name,
    value: item.articleCount || 1
  }))
  const theme = chartTheme.value
  chart.setOption({
    color: chartPalette,
    tooltip: {
      trigger: 'item',
      formatter: t('admin.echart.tooltipFormat'),
      backgroundColor: theme.tooltipBg,
      borderColor: theme.tooltipBorder,
      textStyle: { color: theme.text }
    },
    series: [{
      type: 'pie',
      radius: ['70%', '90%'],
      avoidLabelOverlap: false,
      label: { show: false, color: theme.text },
      labelLine: { lineStyle: { color: theme.text } },
      itemStyle: { borderWidth: 2, borderColor: theme.dark ? '#1e1b2e' : '#fff' },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold', color: theme.text } },
      data: ringData
    }]
  }, true)
}

const initLineChart = () => {
  const chartDom = lineChart.value
  if (!chartDom) return
  if (!xAxisData.value || xAxisData.value.length === 0) return
  if (lineInstance) lineInstance.dispose()
  const chart = echartsLib!.init(chartDom)
  lineInstance = chart
  const theme = chartTheme.value
  chart.setOption({
    tooltip: {
      trigger: 'axis',
      backgroundColor: theme.tooltipBg,
      borderColor: theme.tooltipBorder,
      textStyle: { color: theme.text }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData.value,
      axisLine: { lineStyle: { color: theme.axis } },
      axisLabel: { color: theme.text }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false, lineStyle: { color: theme.axis } },
      axisLabel: { color: theme.text },
      splitLine: { lineStyle: { color: theme.axis } }
    },
    series: [{
      name: t('admin.echart.seriesVisitor'),
      type: 'line',
      smooth: true,
      lineStyle: { color: '#5B4FCF' },
      areaStyle: { color: theme.dark ? 'rgba(91, 79, 207, 0.15)' : 'rgba(91, 79, 207, 0.1)' },
      data: lineSeriesData.value
    }]
  }, true)
}

if (typeof window !== 'undefined') {
  window.addEventListener('resize', () => {
    if (pieInstance) pieInstance.resize()
    if (ringInstance) ringInstance.resize()
    if (lineInstance) lineInstance.resize()
  })
}
</script>

<style scoped>
.chart-page {
  background-color: var(--color-chart-bg);
  min-height: 100vh;
  padding: var(--spacing-page);
}

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

.row-section {
  margin-bottom: var(--spacing-gap);
}

.stat-card,
.chart-card,
.table-card {
  position: relative;
  overflow: hidden;
  background: var(--liquid-glass-bg);
  backdrop-filter: var(--liquid-glass-blur);
  border: var(--liquid-glass-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--liquid-glass-shadow);
  padding: var(--spacing-card);
  height: 100%;
}

.stat-card > *,
.chart-card > *,
.table-card > * {
  position: relative;
  z-index: 1;
}

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

.chart-wrapper {
  height: 220px;
}

.chart-box {
  width: 100%;
  height: 100%;
}

.table-wrapper {
  height: 200px;
  overflow: auto;
}

body.dark-mode .chart-page {
  background-color: var(--color-dark-bg-alt);
}

body.dark-mode .stat-card,
body.dark-mode .chart-card,
body.dark-mode .table-card {
  background: var(--liquid-glass-bg);
  border: var(--liquid-glass-border);
  box-shadow: var(--liquid-glass-shadow);
}

body.dark-mode .card-title {
  color: var(--color-dark-text-regular);
}

body.dark-mode .stat-number {
  color: var(--color-dark-text-regular);
}
</style>
