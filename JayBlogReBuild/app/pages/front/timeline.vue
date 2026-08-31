<template>
  <div class="timeline-page">
    <div v-if="articleList.length === 0" class="empty-state">{{ t('timeline.empty') }}</div>
    <div v-else class="timeline-wrap">
      <div v-for="item in articleList" :key="item.id" class="timeline-item">
        <div class="timeline-marker">
          <div class="marker-dot"></div>
          <div class="marker-line"></div>
        </div>
        <div class="timeline-card" @click="goDetail(item.id)">
          <div class="tl-date">{{ formatDateTime(item.createTime) }}</div>
          <div class="tl-title">{{ item.title }}</div>
          <div class="tl-meta">
            <span>{{ item.categoryName }}</span>
            <span v-if="item.tagNameList && item.tagNameList.length">· {{ item.tagNameList.join(' / ') }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'nuxt/app'
import { getFrontArticleList } from '~/api/front'
import { buildSeoMeta } from '~/utils/seo'
import { useI18n } from '~/composables/useI18n'

const { t, locale } = useI18n()

useSeoMeta(buildSeoMeta({
  title: t('timeline.seo.title'),
  description: t('timeline.seo.desc')
}))

const router = useRouter()

const articleList = ref([])

const goDetail = (id) => { router.push({ path: '/article', query: { id } }) }

const formatDateTime = (isoStr) => {
  if (!isoStr) return ''
  const d = new Date(isoStr)
  // 补全小时、分钟，不足两位补0
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hour = String(d.getHours()).padStart(2, '0')
  const minute = String(d.getMinutes()).padStart(2, '0')
  if (locale.value === 'en-US') return `${year}-${month}-${day} ${hour}:${minute}`
  return `${year}年${month}月${day}日 ${hour}:${minute}`
}

async function loadTimelineData() {
  const params = { pageNum: 1, pageSize: 20, status: 2 }
  const res = await getFrontArticleList(params)
  articleList.value = res.data.records || []
}

onMounted(loadTimelineData)
</script>

<style scoped>
.timeline-page {
  max-width: 700px;
  margin: 0 auto;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: var(--color-text-muted);
  font-size: 15px;
}

.timeline-wrap {
  position: relative;
  padding-left: 36px;
}

.timeline-item {
  position: relative;
  margin-bottom: 0;
}

.timeline-marker {
  position: absolute;
  left: -36px;
  top: 0;
  bottom: 0;
  width: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.marker-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--color-primary);
  flex-shrink: 0;
  margin-top: 22px;
  z-index: 1;
  box-shadow: 0 0 0 4px var(--color-primary-light-bg);
}

.marker-line {
  flex: 1;
  width: 2px;
  background: var(--color-border);
  margin-top: 4px;
}

.timeline-item:last-child .marker-line {
  display: none;
}

.timeline-card {
  padding: 20px 24px;
  margin-bottom: 20px;
  position: relative;
  overflow: hidden;
  background: var(--liquid-glass-bg);
  backdrop-filter: var(--liquid-glass-blur);
  border: var(--liquid-glass-border);
  border-radius: var(--radius-xl);
  box-shadow: var(--liquid-glass-shadow);
  cursor: pointer;
  transition: var(--transition-base);
}

.timeline-card > * {
  position: relative;
  z-index: 1;
}

.timeline-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-card-hover);
  border-color: rgba(91, 79, 207, 0.16);
}

.tl-date {
  font-size: 13px;
  color: var(--color-text-muted);
  margin-bottom: 8px;
}

.tl-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--color-text-primary);
  line-height: 1.4;
  margin-bottom: 8px;
}

.tl-meta {
  font-size: 13px;
  color: var(--color-text-secondary);
}

body.dark-mode .timeline-card {
  background: var(--liquid-glass-bg);
  border: var(--liquid-glass-border);
  box-shadow: var(--liquid-glass-shadow);
}

body.dark-mode .timeline-card:hover {
  border-color: rgba(123, 111, 232, 0.2);
}

body.dark-mode .marker-dot {
  box-shadow: 0 0 0 4px rgba(91, 79, 207, 0.15);
}
</style>