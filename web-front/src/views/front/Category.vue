<template>
    <div class="category-page">
        <div class="page-header">
            <h2 class="page-title">文章分类</h2>
            <p class="page-desc">共 {{ categoryList.length }} 个分类，浏览感兴趣的内容</p>
        </div>

        <div class="category-grid" v-loading="loading">
            <el-empty v-if="categoryList.length === 0 && !loading" description="暂无分类"></el-empty>

            <div v-for="item in categoryList" :key="item.id" class="category-card" @click="goArticleList(item.id)">
                <div class="card-icon">
                    <i class="el-icon-folder-opened"></i>
                </div>
                <div class="card-info">
                    <h3 class="category-name">{{ item.name }}</h3>
                    <p class="article-count">{{ item.articleCount || 0 }} 篇文章</p>
                </div>
                <i class="el-icon-arrow-right card-arrow"></i>
            </div>
        </div>
    </div>
</template>

<script>
import { getCategoryList } from '@/api/category'

export default {
    name: 'Category',
    data() {
        return {
            categoryList: [],
            loading: false
        }
    },
    created() {
        this.loadCategoryList()
    },
    methods: {
        loadCategoryList() {
            this.loading = true
            getCategoryList().then(res => {
                this.categoryList = res.data
            }).catch(() => {
                this.$message.error('获取分类失败')
            }).finally(() => {
                this.loading = false
            })
        },

        // 跳转到对应分类的文章列表
        goArticleList(categoryId) {
            this.$router.push({
                path: '/articlelist',
                query: { categoryId }
            })
        }
    }
}
</script>

<style scoped>
.category-page {
    max-width: 1000px;
    margin: 0 auto;
    padding: 32px 24px;
    min-height: calc(100vh - 80px);
}

.page-header {
    margin-bottom: 32px;
    text-align: center;
}

.page-title {
    font-size: 28px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 8px 0;
}

.page-desc {
    color: var(--color-text-secondary);
    font-size: 14px;
    margin: 0;
}

.category-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}

.category-card {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 24px;
    background: var(--color-white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid transparent;
}

.category-card:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-card-hover);
    border-color: #409eff;
}

.card-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: linear-gradient(135deg, #409eff, #66b1ff);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
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
    margin: 0 0 4px 0;
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
    color: var(--color-text-secondary);
    font-size: 16px;
    flex-shrink: 0;
    transition: transform 0.3s;
}

.category-card:hover .card-arrow {
    transform: translateX(4px);
    color: #409eff;
}

/* 暗黑模式适配 */
body.dark-mode .category-card {
    background: var(--color-dark-surface);
    box-shadow: var(--shadow-card-dark);
}

body.dark-mode .category-card:hover {
    border-color: #409eff;
}
</style>