<template>
  <div class="timeline-page">
    <div class="timeline-card">
      <div v-if="articleList.length === 0" class="empty-tip">暂无发布文章</div>
      <div v-else class="timeline-wrap">
        <!-- 时间轴条目，最多5条 -->
        <div v-for="item in articleList" :key="item.id" class="timeline-item">
          <!-- 左侧圆点+竖线 -->
          <div class="timeline-left">
            <div class="timeline-dot"></div>
            <div class="timeline-line"></div>
          </div>
          <!-- 右侧内容 -->
          <div class="timeline-right">
            <div class="item-time">{{ formatDateTime(item.createTime) }}</div>
            <div class="item-title" @click="$router.push({path:'/article', query:{id:item.id}})">
              {{ item.title }}
            </div>
            <div class="item-desc" v-if="item.about">
              {{ item.about }}
            </div>
            <div class="item-tag">
              分类：{{ item.categoryName }}
              <span v-if="item.tagNameList && item.tagNameList.length > 0">
                ｜标签：{{ item.tagNameList.join('、') }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getFrontArticleList } from '@/api/front'
export default {
  name: 'TimeLine',
  data() {
    return {
      articleList: [],
      categoryMap: {}
    }
  },
  mounted() {
    this.loadTimelineData()
  },
  methods: {
    // ISO时间格式化 2026-06-20T05:28:30 → 2026年6月20日 5点28分
    formatDateTime(isoStr) {
      if (!isoStr) return ''
      const d = new Date(isoStr)
      const y = d.getFullYear()
      const m = d.getMonth() + 1
      const day = d.getDate()
      const h = d.getHours()
      const min = d.getMinutes()
      return `${y}年${m}月${day}日 ${h}点${min}分`
    },

    async loadTimelineData() {
      // 只取最新5条已发布文章，按创建时间倒序
      const params = {
        pageNum: 1,
        pageSize: 5,
        status: 2
      }
      const res = await getFrontArticleList(params)
      this.articleList = res.data.records
    }
  }
}
</script>

<style scoped>
.timeline-page {
  width: 100%;
  padding: 48px 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
/* 页面大标题 加大字号加重权重 */
.page-title {
  font-size: 26px;
  font-weight: 700;
  color: #1c1c36;
  margin-bottom: 36px;
  text-align: center;
}
.timeline-card {
  background: #fff;
  border-radius: 18px;
  padding: 40px;
  box-shadow: 0 4px 16px rgba(99, 85, 216, 0.08);
  max-width: 820px;
  width: 100%;
}
.empty-tip {
  text-align: center;
  padding: 60px 0;
  font-size: 15px;
  color: #aaa;
}

/* 时间轴容器 */
.timeline-wrap {
  position: relative;
}
.timeline-item {
  display: flex;
  gap: 20px;
  padding-bottom: 36px;
  position: relative;
}
/* 左侧圆点竖线容器 */
.timeline-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex-shrink: 0;
  width: 28px;
}
/* 加大圆点，视觉更突出 */
.timeline-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: #6355d8;
}
/* 垂直分割线加粗一点，柔和淡紫 */
.timeline-line {
  width: 2px;
  flex: 1;
  background: #e9e6fa;
  margin-top: 10px;
}
/* 最后一条隐藏竖线 */
.timeline-item:last-child .timeline-line {
  display: none;
}

/* 右侧内容区域 */
.timeline-right {
  flex: 1;
}
/* 时间文字 适中字号、柔和灰色 */
.item-time {
  font-size: 14px;
  color: #888;
  margin-bottom: 10px;
  letter-spacing: 0.3px;
}
/* 文章标题 放大字号、加粗、行宽松 */
.item-title {
  font-size: 18px;
  font-weight: 600;
  color: #1c1c36;
  cursor: pointer;
  transition: color 0.24s ease;
  line-height: 1.45;
}
.item-title:hover {
  color: #6355d8;
}
/* 简介文字 舒适行高、标准字号 */
.item-desc {
  font-size: 15px;
  color: #555;
  line-height: 1.75;
  margin: 12px 0;
}
/* 底部标签信息 略小一点弱化层级 */
.item-tag {
  font-size: 13px;
  color: #777;
}

/* 暗黑模式适配 */
body.dark-mode .page-title {
  color: #f0f0f0;
}
body.dark-mode .timeline-card {
  background: #1e1c31;
  box-shadow: 0 4px 16px rgba(0,0,0,0.3);
}
body.dark-mode .timeline-line {
  background: #383550;
}
body.dark-mode .item-title {
  color: #eee;
}
body.dark-mode .item-desc {
  color: #bbbbbb;
}
body.dark-mode .item-time {
  color: #aaa;
}
body.dark-mode .item-tag {
  color: #999;
}
</style>