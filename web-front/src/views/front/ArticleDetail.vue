<template>
    <div class="article-detail">
        <!-- 极简返回按钮，无外层玻璃卡片 -->
        <div class="back-row">
            <span class="back-text-btn" @click="goBack">
                <i class="el-icon-arrow-left"></i>
                返回文章列表
            </span>
        </div>

        <!-- 加载状态容器 -->
        <div v-loading="loading" element-loading-text="加载中...">
            <!-- 文章不存在 -->
            <div v-if="isError" class="error-box">
                <el-empty description="文章不存在或已被删除"></el-empty>
            </div>

            <!-- 文章正文卡片（液态玻璃主体） -->
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

                <!-- 文章标签区域 -->
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

        <!-- 评论区 -->
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
                tagIdList: [],
                tagNameList: []
            }
        }
    },
    created() {
        this.initPage()
    },
    watch: {
        $route() {
            this.initPage()
        }
    },
    methods: {
        initPage() {
            this.id = this.$route.query.id
            if (!this.id) {
                this.isError = true
                return
            }
            this.loadArticleDetail()
            this.loadLikeStatus()
        },

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

        loadLikeStatus() {
            if (localStorage.getItem('token')) {
                getLikeStatus(this.id).then(res => {
                    this.likeCount = res.data.likeCount
                    this.isLiked = res.data.isLiked
                }).catch(() => { })
                return
            }

        },

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

        goBack() {
            this.$router.back()
        },

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
/* 页面整体容器 */
.article-detail {
    max-width: 900px;
    margin: 0 auto;
    padding: 24px 20px 60px;
}

/* 极简返回文字按钮区域，无玻璃卡片 */
.back-row {
    margin-bottom: 24px;
}

.back-text-btn {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: #6355d8;
    cursor: pointer;
    transition: all 0.2s ease;
}

.back-text-btn:hover {
    color: #4c3fc4;
    transform: translateX(-3px);
}

.back-text-btn i {
    font-size: 16px;
}

.error-box {
    padding: 80px 0;
}

/* 文章主体液态玻璃卡片 */
.article-content {
    padding: 36px 32px;
    position: relative;
    overflow: hidden;
    background: rgba(255, 255, 255, 0.62);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.7);
    border-radius: 24px;
    box-shadow:
        0 1px 3px rgba(0, 0, 0, 0.02),
        0 6px 22px rgba(0, 0, 0, 0.05);
}

.article-content>* {
    position: relative;
    z-index: 1;
}

.article-title {
    font-size: 30px;
    font-weight: 600;
    color: #1d1d1f;
    margin: 0 0 20px 0;
    line-height: 1.4;
}

.article-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 12px 16px;
    align-items: center;
    color: #86868b;
    font-size: 13px;
    padding-bottom: 18px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    margin-bottom: 16px;
}

.meta-item {
    display: inline-flex;
    align-items: center;
    gap: 4px;
}

/* 标签区域 */
.tag-wrap {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 24px;
}

.tag-label {
    font-size: 13px;
    color: #86868b;
}

/* 点赞按钮 */
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
    margin-bottom: 24px;
    text-align: center;
}

.cover-img {
    max-width: 100%;
    max-height: 420px;
    border-radius: 16px;
    object-fit: cover;
}

.article-summary {
    padding: 16px 20px;
    background: rgba(64, 158, 255, 0.08);
    border-left: 4px solid #409eff;
    border-radius: 12px;
    color: #515154;
    font-size: 15px;
    line-height: 1.65;
    margin-bottom: 28px;
}

/* Markdown正文排版 */
.markdown-body {
    color: #1d1d1f;
    font-size: 16px;
    line-height: 1.85;
    word-break: break-word;
}

.markdown-body /deep/ h2,
.markdown-body /deep/ h3 {
    margin-top: 32px;
    margin-bottom: 14px;
    font-weight: 600;
    color: #1d1d1f;
}

.markdown-body /deep/ p {
    margin-bottom: 18px;
}

.markdown-body /deep/ img {
    max-width: 100%;
    border-radius: 12px;
    margin: 16px 0;
}

.markdown-body /deep/ ul,
.markdown-body /deep/ ol {
    padding-left: 26px;
    margin-bottom: 18px;
}

.markdown-body /deep/ code {
    background: #f5f7fa;
    padding: 3px 8px;
    border-radius: 6px;
    font-size: 14px;
    color: #e6a23c;
}

.markdown-body /deep/ pre {
    background: #2d2d2d;
    color: #ccc;
    padding: 20px;
    border-radius: 12px;
    overflow-x: auto;
    margin-bottom: 18px;
}

.markdown-body /deep/ pre code {
    background: transparent;
    color: inherit;
    padding: 0;
}

.markdown-body /deep/ blockquote {
    margin: 20px 0;
    padding: 12px 20px;
    border-left: 4px solid #dcdfe6;
    color: #6e6e73;
    background: rgba(0, 0, 0, 0.02);
    border-radius: 0 12px 12px 0;
}

/* ========== 暗黑模式适配 ========== */
body.dark-mode .back-text-btn {
    color: #b3a7ff;
}

body.dark-mode .back-text-btn:hover {
    color: #d1c8ff;
}

body.dark-mode .article-content {
    background: rgba(30, 30, 36, 0.58);
    border: 1px solid rgba(255, 255, 255, 0.08);
}

body.dark-mode .article-title {
    color: #f5f5f7;
}

body.dark-mode .article-meta {
    border-bottom-color: rgba(255, 255, 255, 0.06);
}

body.dark-mode .article-summary {
    background: rgba(64, 158, 255, 0.1);
    color: #a3b1c2;
}

body.dark-mode .markdown-body {
    color: #f5f5f7;
}

body.dark-mode .markdown-body /deep/ h2,
body.dark-mode .markdown-body /deep/ h3 {
    color: #f5f5f7;
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

/* 移动端适配 */
@media (max-width:768px) {
    .article-detail {
        padding: 16px;
    }

    .back-row {
        margin-bottom: 16px;
    }

    .article-content {
        padding: 24px 20px;
        border-radius: 20px;
    }

    .article-title {
        font-size: 24px;
    }
}
</style>