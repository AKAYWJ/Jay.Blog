<template>
    <div class="article-card" :class="{ 'is-top': article.isTop === 1 }" @click="$emit('click')">
        <!-- 置顶角标 -->
        <span v-if="article.isTop === 1" class="top-badge">置顶</span>

        <!-- 封面图 -->
        <div class="card-cover" v-if="article.cover">
            <img :src="article.cover" alt="封面">
        </div>
        <div class="card-cover cover-placeholder" v-else>
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                <polyline points="14 2 14 8 20 8"></polyline>
                <line x1="16" y1="13" x2="8" y2="13"></line>
                <line x1="16" y1="17" x2="8" y2="17"></line>
            </svg>
        </div>

        <!-- 内容区 -->
        <div class="card-content">
            <h3 class="card-title">{{ article.title }}</h3>
            <p class="card-summary">{{ article.summary || '暂无摘要' }}</p>
            <div class="card-meta">
                <span class="meta-category">{{ article.categoryName || '未分类' }}</span>
                <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                    {{ article.viewCount || 0 }}
                </span>
                <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                    {{ article.likeCount || 0 }}
                </span>
                <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                    {{ formatDate(article.createTime) }}
                </span>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'ArticleCard',
    props: {
        article: { type: Object, required: true }
    },
    methods: {
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
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
    background: var(--glass-bg);
    backdrop-filter: var(--glass-blur);
    -webkit-backdrop-filter: var(--glass-blur);
    border: 1px solid var(--glass-border);
    border-radius: var(--radius-xl);
    box-shadow: var(--glass-shadow);
    cursor: pointer;
    transition: var(--transition-base);
    overflow: hidden;
}

.article-card:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-card-hover);
    border-color: rgba(91, 79, 207, 0.14);
    background: rgba(255, 255, 255, 0.88);
}

/* 置顶卡片左侧高亮边 */
.article-card.is-top {
    border-left: 3px solid var(--color-primary);
}

/* 右上角置顶角标 */
.top-badge {
    position: absolute;
    top: 0;
    right: 0;
    padding: 4px 14px;
    background: var(--color-primary-gradient);
    color: #fff;
    font-size: 11px;
    border-radius: 0 var(--radius-xl) 0 var(--radius-md);
    font-weight: 500;
    z-index: 2;
}

/* 封面图 */
.card-cover {
    width: 200px;
    height: 135px;
    border-radius: var(--radius-md);
    overflow: hidden;
    flex-shrink: 0;
    background: var(--color-bg);
}

.card-cover img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
}

.article-card:hover .card-cover img {
    transform: scale(1.04);
}

.cover-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-text-muted);
    background: var(--color-bg);
}

/* 内容区 */
.card-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.card-title {
    font-size: 17px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 8px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.card-summary {
    font-size: 13px;
    color: var(--color-text-secondary);
    line-height: 1.6;
    margin: 0 0 12px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.card-meta {
    display: flex;
    align-items: center;
    gap: 16px;
    font-size: 12px;
    color: var(--color-text-muted);
}

.meta-category {
    display: inline-flex;
    align-items: center;
    padding: 2px 8px;
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
    border-radius: var(--radius-sm);
    font-size: 12px;
    font-weight: 500;
}

.meta-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

/* ===== 暗黑模式 ===== */
body.dark-mode .article-card {
    background: var(--color-dark-glass);
    border-color: var(--color-dark-border);
}

body.dark-mode .article-card:hover {
    background: rgba(37, 34, 60, 0.92);
    border-color: rgba(123, 111, 232, 0.2);
}

body.dark-mode .card-cover,
body.dark-mode .cover-placeholder {
    background: rgba(255, 255, 255, 0.04);
}

/* ===== 响应式：移动端封面缩小 ===== */
@media screen and (max-width: 640px) {
    .card-cover {
        width: 120px;
        height: 90px;
    }

    .card-title {
        font-size: 15px;
    }
}
</style>
