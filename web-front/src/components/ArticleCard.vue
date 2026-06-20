<template>
    <div class="article-card" :class="{ 'is-top': article.isTop === 1 }" @click="$emit('click')">
        <!-- 置顶角标 -->
        <div v-if="article.isTop === 1" class="top-badge">置顶</div>

        <!-- 封面图 -->
        <div class="card-cover" v-if="article.cover">
            <img :src="article.cover" alt="封面">
        </div>
        <div class="card-cover cover-placeholder" v-else>
            <i class="el-icon-document"></i>
        </div>

        <!-- 内容区 -->
        <div class="card-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
            <div class="article-meta">
                <span class="meta-tag">{{ article.categoryName || '未分类' }}</span>
                <span class="meta-item">
                    <i class="el-icon-view"></i> {{ article.viewCount || 0 }}
                </span>
                <!-- 新增点赞数量 -->
                <span class="meta-item">
                    <i class="el-icon-star-off"></i> {{ article.likeCount || 0 }}
                </span>
                <span class="meta-item">
                    <i class="el-icon-time"></i> {{ formatDate(article.createTime) }}
                </span>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'ArticleCard',
    props: {
        article: {
            type: Object,
            required: true
        }
    },
    methods: {
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, 0)}`
        }
    }
}
</script>

<style scoped>
.article-card {
    position: relative;
    display: flex;
    gap: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 3px 14px rgba(99, 85, 216, 0.07);
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid transparent;
    overflow: hidden;
    margin-bottom: 16px;
}

.article-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(99, 85, 216, 0.12);
    border-color: #6355d8;
}

/* 置顶卡片左侧高亮边 */
.article-card.is-top {
    border-left: 4px solid #e6a23c;
}

/* 右上角置顶角标 */
.top-badge {
    position: absolute;
    top: 0;
    right: 0;
    padding: 4px 12px;
    background: linear-gradient(135deg, #e6a23c, #f56c6c);
    color: #fff;
    font-size: 12px;
    border-radius: 0 16px 0 12px;
    font-weight: 500;
    z-index: 2;
}

.card-cover {
    width: 200px;
    height: 130px;
    border-radius: 8px;
    overflow: hidden;
    flex-shrink: 0;
    background: #f5f7fa;
}

.card-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.cover-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c0c4cc;
    font-size: 42px;
}

.card-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.article-title {
    font-size: 17px;
    font-weight: 600;
    color: #1c1c36;
    margin: 0 0 8px 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.article-summary {
    font-size: 13px;
    color: #666;
    line-height: 1.6;
    margin: 0 0 10px 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.article-meta {
    display: flex;
    align-items: center;
    gap: 14px;
    font-size: 12px;
    color: #909399;
}

.meta-tag {
    padding: 2px 8px;
    background: #ecf5ff;
    color: #6355d8;
    border-radius: 4px;
    font-size: 12px;
}

.meta-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

/* 暗黑模式适配 */
body.dark-mode .article-card {
    background: var(--color-dark-surface);
    box-shadow: var(--shadow-card-dark);
}

body.dark-mode .article-card:hover {
    border-color: #6355d8;
}

body.dark-mode .article-title {
    color: var(--color-text-primary);
}

body.dark-mode .article-summary {
    color: var(--color-text-secondary);
}

body.dark-mode .card-cover {
    background: rgba(255, 255, 255, 0.05);
}

body.dark-mode .meta-tag {
    background: rgba(99, 85, 216, 0.15);
}
</style>