<template>
    <div class="article-detail">
        <!-- 加载状态 -->
        <div v-loading="loading" element-loading-text="加载中...">
            <!-- 返回按钮 -->
            <div class="back-bar">
                <el-button icon="el-icon-arrow-left" @click="goBack">返回列表</el-button>
            </div>

            <!-- 文章不存在 -->
            <div v-if="isError" class="error-box">
                <el-empty description="文章不存在或已被删除"></el-empty>
            </div>

            <!-- 文章正文 -->
            <div v-else class="article-content">
                <!-- 文章标题 -->
                <h1 class="article-title">{{ article.title }}</h1>

                <!-- 文章元信息 -->
                <div class="article-meta">
                    <el-tag v-if="article.isTop === 1" type="warning" size="small">置顶</el-tag>
                    <el-tag :type="article.status === 2 ? 'success' : article.status === 1 ? 'info' : 'danger'"
                        size="small">
                        {{ article.status === 1 ? '草稿' : article.status === 2 ? '已发布' : '私密' }}
                    </el-tag>
                    <span class="meta-item">分类：{{ article.categoryName || '未分类' }}</span>
                    <span class="meta-item">
                        <i class="el-icon-view"></i> {{ article.viewCount || 0 }}
                    </span>
                    <!-- 点赞按钮 -->
                    <span class="meta-item like-btn" :class="{ active: isLiked }" @click="handleLike">
                        <i :class="isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                        {{ likeCount }}
                    </span>
                    <span class="meta-item">发布时间：{{ formatDate(article.createTime) }}</span>
                </div>

                <!-- 新增：文章标签区域 -->
                <div v-if="article.tagNameList && article.tagNameList.length > 0" class="tag-wrap">
                    <span class="tag-label">标签：</span>
                    <el-tag v-for="tag in article.tagNameList" :key="tag" size="small" effect="light">
                        {{ tag }}
                    </el-tag>
                </div>

                <!-- 封面图 -->
                <!-- <div v-if="article.cover" class="cover-box">
                    <img :src="article.cover" alt="封面图" class="cover-img">
                </div> -->

                <!-- 文章摘要 -->
                <div v-if="article.summary" class="article-summary">
                    {{ article.summary }}
                </div>

                <!-- Markdown正文内容 -->
                <div class="markdown-body" v-html="article.contentHtml"></div>
            </div>
        </div>

        <CommentSection :article-id="id" />
    </div>
</template>

<script>
import { getArticleById, likeArticle, getLikeStatus } from '@/api/article'
import CommentSection from '@/components/CommentSection.vue'

export default {
    name: 'ArticleDetail',
    components: {
        CommentSection
    },
    data() {
        return {
            id: '',
            loading: false,
            isError: false,
            likeCount: 0,
            isLiked: false,
            likeLoading: false,
            article: {
                id: null,
                title: '',
                categoryId: null,
                categoryName: '',
                summary: '',
                cover: '',
                contentHtml: '',
                isTop: 0,
                status: 1,
                viewCount: 0,
                createTime: '',
                // 标签数组，后端视图自动返回
                tagIdList: [],
                tagNameList: []
            }
        }
    },
    created() {
        this.initPage()
    },
    watch: {
        // 同页面路由切换不同文章，重新加载
        $route() {
            this.initPage()
        }
    },
    methods: {
        // 初始化页面
        initPage() {
            this.id = this.$route.query.id
            if (!this.id) {
                this.isError = true
                return
            }
            this.loadArticleDetail()
            this.loadLikeStatus()
        },

        // 请求文章详情（后端视图自动携带categoryName、tagNameList）
        loadArticleDetail() {
            this.loading = true
            this.isError = false
            getArticleById(this.id).then(res => {
                this.article = res.data
            }).catch(() => {
                this.isError = true
                this.$message.error('获取文章详情失败')
            }).finally(() => {
                this.loading = false
            })
        },

        // 加载点赞状态
        loadLikeStatus() {
            getLikeStatus(this.id).then(res => {
                this.likeCount = res.data.likeCount
                this.isLiked = res.data.isLiked
            }).catch(() => { })
        },

        // 点赞/取消点赞
        handleLike() {
            if (!localStorage.getItem('token')) {
                this.$message.warning('请先登录后点赞')
                return
            }
            if (this.likeLoading) return

            this.likeLoading = true
            const newLikeStatus = !this.isLiked
            likeArticle({
                articleId: Number(this.id),
                isLike: newLikeStatus
            }).then(res => {
                this.likeCount = res.data.likeCount
                this.isLiked = res.data.isLiked
                this.$message.success(newLikeStatus ? '点赞成功' : '取消点赞')
            }).catch(err => {
                this.$message.error(err?.data || '操作失败')
            }).finally(() => {
                this.likeLoading = false
            })
        },

        // 返回列表
        goBack() {
            this.$router.back()
        },

        // 统一补零时间格式化
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            const pad = num => String(num).padStart(2, '0')
            const y = d.getFullYear()
            const m = pad(d.getMonth() + 1)
            const day = pad(d.getDate())
            const h = pad(d.getHours())
            const min = pad(d.getMinutes())
            return `${y}-${m}-${day} ${h}:${min}`
        }
    }
}
</script>

<style scoped>
.article-detail {
    max-width: 900px;
    margin: 0 auto;
    padding: 24px;
    background: var(--color-white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
    min-height: calc(100vh - 80px);
}

.back-bar {
    margin-bottom: 20px;
}

.error-box {
    padding: 60px 0;
}

.article-title {
    font-size: 28px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 16px 0;
    line-height: 1.4;
}

.article-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px 16px;
    align-items: center;
    color: var(--color-text-secondary);
    font-size: 13px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--color-border);
    margin-bottom: 12px;
}

.meta-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

/* 标签样式 */
.tag-wrap {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 20px;
}
.tag-label {
    font-size: 13px;
    color: var(--color-text-secondary);
}

/* 点赞按钮样式 */
.like-btn {
    cursor: pointer;
    transition: color 0.2s;
    user-select: none;
}

.like-btn:hover {
    color: #6355d8;
}

.like-btn.active {
    color: #e6a23c;
}

.cover-box {
    margin-bottom: 20px;
    text-align: center;
}

.cover-img {
    max-width: 100%;
    max-height: 400px;
    border-radius: 8px;
    object-fit: cover;
}

.article-summary {
    padding: 14px 18px;
    background: #f5f7fa;
    border-left: 4px solid #409eff;
    border-radius: 4px;
    color: #606266;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 24px;
}

/* Markdown正文排版 */
.markdown-body {
    color: var(--color-text-primary);
    font-size: 15px;
    line-height: 1.8;
    word-break: break-word;
}

.markdown-body /deep/ h2,
.markdown-body /deep/ h3 {
    margin-top: 24px;
    margin-bottom: 12px;
    font-weight: 600;
    color: var(--color-text-primary);
}

.markdown-body /deep/ p {
    margin-bottom: 16px;
}

.markdown-body /deep/ img {
    max-width: 100%;
    border-radius: 6px;
    margin: 12px 0;
}

.markdown-body /deep/ ul,
.markdown-body /deep/ ol {
    padding-left: 24px;
    margin-bottom: 16px;
}

.markdown-body /deep/ code {
    background: #f5f7fa;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 13px;
    color: #e6a23c;
}

.markdown-body /deep/ pre {
    background: #2d2d2d;
    color: #ccc;
    padding: 16px;
    border-radius: 6px;
    overflow-x: auto;
    margin-bottom: 16px;
}

.markdown-body /deep/ pre code {
    background: transparent;
    color: inherit;
    padding: 0;
}

.markdown-body /deep/ blockquote {
    margin: 16px 0;
    padding: 10px 16px;
    border-left: 4px solid #dcdfe6;
    color: #909399;
    background: #fafafa;
    border-radius: 0 4px 4px 0;
}

/* 暗黑模式适配 */
body.dark-mode .article-detail {
    background: var(--color-dark-surface);
    box-shadow: var(--shadow-card-dark);
}

body.dark-mode .article-summary {
    background: rgba(64, 158, 255, 0.1);
    color: #a3b1c2;
}

body.dark-mode .markdown-body /deep/ code {
    background: rgba(255, 255, 255, 0.1);
}

body.dark-mode .markdown-body /deep/ blockquote {
    background: rgba(255, 255, 255, 0.05);
    border-color: #4a5060;
    color: #909399;
}

body.dark-mode .like-btn:hover {
    color: #b3a7ff;
}
</style>