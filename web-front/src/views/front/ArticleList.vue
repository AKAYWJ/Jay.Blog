<template>
    <div class="article-list-page">
        <!-- 筛选栏 -->
        <div class="filter-bar">
            <!-- 返回上一页按钮 -->
            <div class="back-btn">
                <el-button icon="el-icon-arrow-left" circle @click="goBack"></el-button>
            </div>

            <div class="search-box">
                <el-input v-model="searchKeyword" placeholder="搜索文章标题..." clearable @keyup.enter="handleSearch"
                    @clear="handleSearch">
                </el-input>
            </div>

            <div class="category-filter">
                <el-select v-model="currentCategoryId" placeholder="全部分类" clearable @change="handleCategoryChange">
                    <el-option label="全部分类" :value="null"></el-option>
                    <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id">
                    </el-option>
                </el-select>
            </div>
        </div>

        <!-- 文章列表 -->
        <div class="article-list" v-loading="loading">
            <el-empty v-if="articleList.length === 0 && !loading" description="暂无相关文章"></el-empty>

            <div v-for="item in articleList" :key="item.id" class="article-card" @click="goDetail(item.id)">
                <!-- 封面图 -->
                <div class="card-cover" v-if="item.cover">
                    <img :src="item.cover" alt="封面">
                </div>
                <div class="card-cover cover-placeholder" v-else>
                    <i class="el-icon-document"></i>
                </div>

                <!-- 文章内容 -->
                <div class="card-content">
                    <h3 class="article-title">{{ item.title }}</h3>
                    <p class="article-summary">{{ item.summary || '暂无摘要' }}</p>
                    <div class="article-meta">
                        <span class="meta-tag">{{ item.categoryName || '未分类' }}</span>
                        <span class="meta-item">
                            <i class="el-icon-view"></i> {{ item.viewCount || 0 }}
                        </span>
                        <span class="meta-item">
                            <i class="el-icon-time"></i> {{ formatDate(item.createTime) }}
                        </span>
                    </div>
                </div>
            </div>
        </div>

        <!-- 分页 -->
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
        if (routeCategoryId) {
            this.currentCategoryId = Number(routeCategoryId)
        }
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
        // 返回上一页
        goBack() {
            this.$router.back()
        },

        loadCategoryList() {
            getCategoryList().then(res => {
                this.categoryList = res.data
            }).catch(() => {
                this.$message.error('获取分类失败')
            })
        },

        loadArticleList() {
            this.loading = true
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize
            }
            if (this.searchKeyword.trim()) {
                params.title = this.searchKeyword.trim()
            }
            if (this.currentCategoryId) {
                params.categoryId = this.currentCategoryId
            }

            getFrontArticleList(params).then(res => {
                this.articleList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取文章列表失败')
            }).finally(() => {
                this.loading = false
            })
        },

        handleSearch() {
            this.pageNum = 1
            this.loadArticleList()
        },

        handleCategoryChange() {
            this.pageNum = 1
            this.$router.replace({
                path: '/articlelist',
                query: {
                    categoryId: this.currentCategoryId || undefined
                }
            })
        },

        handlePageChange(page) {
            this.pageNum = page
            this.loadArticleList()
            window.scrollTo({ top: 0, behavior: 'smooth' })
        },

        goDetail(id) {
            this.$router.push({
                path: '/article',
                query: { id }
            })
        },

        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, 0)}-${String(d.getDate()).padStart(2, 0)}`
        }
    }
}
</script>

<style scoped>
.article-list-page {
    max-width: 1000px;
    margin: 0 auto;
    padding: 24px;
    min-height: calc(100vh - 80px);
}

.filter-bar {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
    padding: 16px 20px;
    background: var(--color-white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
}

.back-btn {
    flex-shrink: 0;
}

.search-box {
    flex: 1;
}

/* 搜索框美化 */
.search-box /deep/ .el-input__inner {
    height: 40px;
    line-height: 40px;
    border-radius: 20px;
    border: 1px solid #e4e7ed;
    padding-left: 42px;
    padding-right: 20px;
    font-size: 14px;
    transition: all 0.25s ease;
}

.search-box /deep/ .el-input__inner:hover {
    border-color: #c0c4cc;
}

.search-box /deep/ .el-input__inner:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.12);
}

.search-icon {
    color: #909399;
    font-size: 16px;
    margin-left: 6px;
}

.category-filter {
    width: 200px;
    flex-shrink: 0;
}

/* 分类选择器统一圆角 */
.category-filter /deep/ .el-input__inner {
    height: 40px;
    line-height: 40px;
    border-radius: 20px;
    border: 1px solid #e4e7ed;
    transition: all 0.25s ease;
}

.category-filter /deep/ .el-input__inner:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.12);
}

.article-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
}

.article-card {
    display: flex;
    gap: 20px;
    padding: 20px;
    background: var(--color-white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid transparent;
}

.article-card:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-card-hover);
    border-color: #409eff;
}

.card-cover {
    width: 220px;
    height: 140px;
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
    font-size: 48px;
}

.card-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.article-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 8px 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.article-summary {
    font-size: 14px;
    color: var(--color-text-secondary);
    line-height: 1.6;
    margin: 0 0 12px 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.article-meta {
    display: flex;
    align-items: center;
    gap: 16px;
    font-size: 12px;
    color: var(--color-text-secondary);
}

.meta-tag {
    padding: 2px 8px;
    background: #ecf5ff;
    color: #409eff;
    border-radius: 4px;
    font-size: 12px;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
}

.pagination-wrap {
    display: flex;
    justify-content: center;
    margin-top: 32px;
}

/* 暗黑模式适配 */
body.dark-mode .filter-bar,
body.dark-mode .article-card {
    background: var(--color-dark-surface);
    box-shadow: var(--shadow-card-dark);
}

body.dark-mode .article-card:hover {
    border-color: #409eff;
}

body.dark-mode .card-cover {
    background: rgba(255, 255, 255, 0.05);
}

body.dark-mode .meta-tag {
    background: rgba(64, 158, 255, 0.1);
}

/* 暗黑模式输入框适配 */
body.dark-mode .search-box /deep/ .el-input__inner,
body.dark-mode .category-filter /deep/ .el-input__inner {
    background: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.1);
    color: var(--color-text-primary);
}

body.dark-mode .search-box /deep/ .el-input__inner:hover,
body.dark-mode .category-filter /deep/ .el-input__inner:hover {
    border-color: rgba(64, 158, 255, 0.4);
}

body.dark-mode .search-box /deep/ .el-input__inner:focus,
body.dark-mode .category-filter /deep/ .el-input__inner:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
}

body.dark-mode .search-icon {
    color: #909399;
}

/* 响应式适配 */
@media (max-width: 768px) {
    .filter-bar {
        flex-wrap: wrap;
    }

    .back-btn {
        order: 1;
    }

    .search-box {
        order: 3;
        width: 100%;
    }

    .category-filter {
        order: 2;
        flex: 1;
        width: auto;
    }

    .article-card {
        flex-direction: column;
    }

    .card-cover {
        width: 100%;
        height: 180px;
    }
}
</style>