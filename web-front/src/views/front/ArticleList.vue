<template>
    <div class="article-list-page">
        <!-- iOS风格导航过滤栏 -->
        <div class="filter-bar">
            <div class="back-btn">
                <el-button icon="el-icon-arrow-left" circle size="small"></el-button>
            </div>
            <div class="search-box">
                <el-input v-model="searchKeyword" placeholder="搜索文章..." clearable @keyup.enter="handleSearch"
                    @clear="handleSearch"></el-input>
            </div>
            <div class="category-filter">
                <el-select v-model="currentCategoryId" placeholder="全部分类" clearable @change="handleCategoryChange">
                    <el-option label="全部分类" :value="null"></el-option>
                    <el-option v-for="item in categoryList" :key="item.id" :label="item.name"
                        :value="item.id"></el-option>
                </el-select>
            </div>
        </div>

        <!-- 文章列表区域 -->
        <div class="article-list" v-loading="loading">
            <el-empty v-if="articleList.length === 0 && !loading" description="暂无相关文章"></el-empty>
            <div v-for="item in articleList" :key="item.id" class="article-card" @click="goDetail(item.id)">
                <!-- 顶部元信息：日期 + 分类 -->
                <div class="card-meta">
                    <span class="meta-date">{{ formatDate(item.createTime) }}</span>
                    <span class="meta-sep">·</span>
                    <span class="meta-category">{{ item.categoryName || '未分类' }}</span>
                </div>
                <!-- 主标题 -->
                <h3 class="card-title">{{ item.title }}</h3>
                <!-- 摘要文本 -->
                <p class="card-summary">{{ item.summary || '暂无摘要' }}</p>
                <!-- 底部阅读量 -->
                <div class="card-footer">
                    <span class="view-count">{{ item.viewCount || 0 }} 次阅读</span>
                </div>
            </div>
        </div>

        <!-- 分页器 -->
        <div class="pagination-wrap" v-if="total > pageSize">
            <el-pagination @current-change="handlePageChange" :current-page="pageNum" :page-size="pageSize"
                layout="prev, pager, next, total" :total="total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { getFrontArticleList } from '@/api/front'
import { getCategoryList } from '@/api/category'

export default {
    name: 'ArticleList',
    data() {
        return {
            searchKeyword: '',
            currentCategoryId: null,
            articleList: [],
            categoryList: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            loading: false
        }
    },
    created() {
        const routeCategoryId = this.$route.query.categoryId
        if (routeCategoryId) this.currentCategoryId = Number(routeCategoryId)
        this.loadCategoryList()
        this.loadArticleList()
    },
    watch: {
        $route(to) {
            const newCategoryId = to.query.categoryId
            this.currentCategoryId = newCategoryId ? Number(newCategoryId) : null
            this.pageNum = 1
            this.loadArticleList()
        }
    },
    methods: {
        goBack() { this.$router.back() },
        loadCategoryList() {
            getCategoryList().then(res => { this.categoryList = res.data }).catch(() => { })
        },
        loadArticleList() {
            this.loading = true
            const params = { pageNum: this.pageNum, pageSize: this.pageSize }
            if (this.searchKeyword.trim()) params.title = this.searchKeyword.trim()
            if (this.currentCategoryId) params.categoryId = this.currentCategoryId
            getFrontArticleList(params).then(res => {
                this.articleList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取文章列表失败')
            }).finally(() => { this.loading = false })
        },
        handleSearch() { this.pageNum = 1; this.loadArticleList() },
        handleCategoryChange() {
            this.pageNum = 1
            this.$router.replace({ path: '/articlelist', query: { categoryId: this.currentCategoryId || undefined } })
        },
        handlePageChange(page) { this.pageNum = page; this.loadArticleList(); window.scrollTo({ top: 0, behavior: 'smooth' }) },
        goDetail(id) { this.$router.push({ path: '/front/article', query: { id } }) },
        // 苹果极简日期格式
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            const month = String(d.getMonth() + 1).padStart(2, '0')
            const day = String(d.getDate()).padStart(2, '0')
            return `${d.getFullYear()}年${month}月${day}日`
        }
    }
}
</script>

<style scoped>
/* 页面全局基础约束 苹果安全边距 */
.article-list-page {
    max-width: 820px;
    margin: 0 auto;
    padding: 24px 20px 60px;
}

/* ========== 顶部过滤导航栏 iOS液态玻璃导航风格 ========== */
.filter-bar {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 32px;
    padding: 16px 20px;
    position: relative;
    overflow: hidden;
    /* 苹果原生液态玻璃半透 */
    background: rgba(255, 255, 255, 0.65);
    backdrop-filter: blur(22px);
    -webkit-backdrop-filter: blur(22px);
    border: 1px solid rgba(255, 255, 255, 0.75);
    border-radius: 20px;
    /* 极淡柔和阴影，拒绝厚重阴影 */
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.filter-bar>* {
    position: relative;
    z-index: 1;
}

.back-btn {
    flex-shrink: 0;
}

.search-box {
    flex: 1;
}

.category-filter {
    width: 190px;
    flex-shrink: 0;
}

/* 输入框 苹果胶囊圆角 */
.search-box /deep/ .el-input__inner,
.category-filter /deep/ .el-input__inner {
    height: 40px;
    border-radius: 999px;
    border: 1px solid rgba(0, 0, 0, 0.06);
    background: rgba(255, 255, 255, 0.85);
    font-size: 15px;
    transition: all 0.24s ease;
}

.search-box /deep/ .el-input__inner:focus,
.category-filter /deep/ .el-input__inner:focus {
    border-color: rgba(91, 79, 207, 0.35);
    box-shadow: 0 0 0 4px rgba(91, 79, 207, 0.08);
}

/* ========== 文章列表容器 ========== */
.article-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

/* 苹果标准文章卡片 Liquid Glass 重构 */
.article-card {
    padding: 28px 30px;
    position: relative;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.62);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.7);
    border-radius: 24px;
    /* 多层极淡柔和阴影，苹果标志性悬浮质感 */
    box-shadow:
        0 1px 3px rgba(0, 0, 0, 0.02),
        0 6px 22px rgba(0, 0, 0, 0.05);
    cursor: pointer;
    transition: all 0.28s cubic-bezier(0.2, 0, 0.2, 1);
}

.article-card>* {
    position: relative;
    z-index: 1;
}

/* Hover 轻盈上浮，苹果克制动效，无刺眼变色 */
.article-card:hover {
    transform: translateY(-4px);
    background: rgba(255, 255, 255, 0.78);
    box-shadow:
        0 2px 6px rgba(0, 0, 0, 0.03),
        0 10px 32px rgba(0, 0, 0, 0.07);
    border-color: rgba(91, 79, 207, 0.18);
}

/* ========== 文字层级 严格对齐苹果动态字体规范 ========== */
/* 顶部元信息 */
.card-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #86868b;
    margin-bottom: 12px;
}

.meta-sep {
    opacity: 0.45;
}

.meta-category {
    color: #5b4fcf;
    font-weight: 500;
}

/* 标题：苹果大标题字重、字号宽松行高 */
.card-title {
    font-size: 19px;
    font-weight: 600;
    color: #1d1d1f;
    line-height: 1.42;
    margin: 0 0 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* 摘要正文 柔和浅灰，两行截断 */
.card-summary {
    font-size: 15px;
    color: #515154;
    line-height: 1.65;
    margin: 0 0 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

/* 底部小字 */
.card-footer {
    font-size: 13px;
    color: #86868b;
}

/* ========== 分页器 ========== */
.pagination-wrap {
    display: flex;
    justify-content: center;
    margin-top: 40px;
}

/* ========== 暗黑模式 macOS/iOS 深色液态玻璃适配 ========== */
body.dark-mode .filter-bar,
body.dark-mode .article-card {
    background: rgba(30, 30, 36, 0.58);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border: 1px solid rgba(255, 255, 255, 0.08);
    box-shadow: 0 4px 18px rgba(0, 0, 0, 0.22);
}

body.dark-mode .article-card:hover {
    background: rgba(42, 42, 50, 0.68);
    border-color: rgba(123, 111, 232, 0.22);
}

body.dark-mode .search-box /deep/ .el-input__inner,
body.dark-mode .category-filter /deep/ .el-input__inner {
    background: rgba(255, 255, 255, 0.06);
    border-color: rgba255, 255, 255, 0.09;
    color: #f5f5f7;
}

body.dark-mode .card-title {
    color: #f5f5f7;
}

body.dark-mode .card-summary {
    color: #a1a1aa;
}

body.dark-mode .card-meta,
body.dark-mode .card-footer {
    color: #86868b;
}

/* ========== 移动端自适应 苹果移动端规范 ========== */
@media (max-width: 768px) {
    .article-list-page {
        padding: 16px 16px 40px;
    }

    .filter-bar {
        flex-wrap: wrap;
        gap: 12px;
        padding: 14px 16px;
    }

    .search-box {
        order: 3;
        width: 100%;
    }

    .category-filter {
        flex: 1;
        width: auto;
    }

    .article-card {
        padding: 22px 20px;
        border-radius: 20px;
    }

    .card-title {
        font-size: 17px;
    }
}
</style>