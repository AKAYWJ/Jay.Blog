<template>
    <div class="category-page">
        <div class="page-header">
            <h2 class="page-title">{{ t('category.title') }}</h2>
            <p class="page-desc">{{ t('category.count', { count: categoryList.length }) }}</p>
        </div>

        <div class="category-grid" v-loading="loading">
            <el-empty v-if="categoryList.length === 0 && !loading" :description="t('category.empty')"></el-empty>
            <div v-for="item in categoryList" :key="item.id" class="category-card" @click="goArticleList(item.id)">
                <div class="card-icon">
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                        <path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path>
                    </svg>
                </div>
                <div class="card-info">
                    <h3 class="category-name">{{ item.name }}</h3>
                    <p class="article-count">{{ t('category.articleCount', { count: item.articleCount || 0 }) }}</p>
                </div>
                <svg class="card-arrow" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                    <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'nuxt/app'
import { ElMessage } from 'element-plus'
import { getCategoryList } from '~/api/category'

const router = useRouter()
const { t } = useI18n()

const categoryList = ref([])
const loading = ref(false)

const loadCategoryList = () => {
    loading.value = true
    getCategoryList().then(res => {
        categoryList.value = res.data
    }).catch(() => {
        ElMessage.error(t('category.loadFail'))
    }).finally(() => { loading.value = false })
}

const goArticleList = (categoryId) => {
    router.push({ path: '/articlelist', query: { categoryId } })
}

onMounted(loadCategoryList)
</script>

<style scoped>
.category-page {
    max-width: 900px;
    margin: 0 auto;
    padding: 16px 0 40px;
}

.page-header {
    margin-bottom: 32px;
}

.page-title {
    font-size: 24px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 6px;
}

.page-desc {
    color: var(--color-text-muted);
    font-size: 14px;
    margin: 0;
}

.category-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 16px;
}

.category-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 22px 24px;
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

.category-card > * {
    position: relative;
    z-index: 1;
}

.category-card:hover {
    transform: translateY(-3px);
    box-shadow: var(--shadow-card-hover);
    border-color: rgba(91, 79, 207, 0.16);
}

.card-icon {
    width: 46px;
    height: 46px;
    border-radius: var(--radius-md);
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
}

.card-info {
    flex: 1;
    min-width: 0;
}

.category-name {
    font-size: 16px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.article-count {
    font-size: 13px;
    color: var(--color-text-secondary);
    margin: 0;
}

.card-arrow {
    color: var(--color-text-muted);
    flex-shrink: 0;
    transition: var(--transition-fast);
}

.category-card:hover .card-arrow {
    transform: translateX(3px);
    color: var(--color-primary);
}

body.dark-mode .category-card {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

body.dark-mode .category-card:hover {
    border-color: rgba(123, 111, 232, 0.2);
}

body.dark-mode .card-icon {
    background: rgba(91, 79, 207, 0.15);
}
</style>