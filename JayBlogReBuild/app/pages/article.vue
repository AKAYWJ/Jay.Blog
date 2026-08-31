<template>
    <div class="article-detail">
        <ReadingProgress />

        <!-- 极简返回按钮，无外层玻璃卡片 -->
        <div class="back-row">
            <span class="back-text-btn" @click="goBack">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                    <polyline points="15 18 9 12 15 6"></polyline>
                </svg>
                {{ t('article.backToList') }}
            </span>
        </div>

        <!-- 加载状态容器 -->
        <div v-loading="loading" :element-loading-text="t('common.loading')">
            <!-- 文章不存在 -->
            <div v-if="isError" class="error-box">
                <el-empty :description="t('article.notFound')"></el-empty>
            </div>

            <!-- 文章正文卡片（液态玻璃主体） -->
            <div v-else class="article-content">
                <!-- 文章标题 -->
                <h1 class="article-title">{{ article.title }}</h1>

                <!-- 文章元信息 -->
                <div class="article-meta">
                    <el-tag v-if="article.isTop === 1" type="warning" size="small">{{ t('article.top') }}</el-tag>
                    <el-tag :type="article.status === 2 ? 'success' : article.status === 1 ? 'info' : 'danger'"
                        size="small">
                        {{ article.status === 1 ? t('article.draft') : article.status === 2 ? t('article.published') : t('article.private') }}
                    </el-tag>
                    <span class="meta-item">{{ t('article.category') }}: {{ article.categoryName || t('article.uncategorized') }}</span>
                    <span class="meta-item">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                        {{ article.viewCount || 0 }}
                    </span>
                    <!-- 点赞按钮 -->
                    <span class="meta-item like-btn" :class="{ active: isLiked }" @click="handleLike">
                        <svg v-if="isLiked" width="14" height="14" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="1.5" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                        <svg v-else width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
                        {{ likeCount }}
                    </span>
                    
                    <span class="meta-item">{{ t('article.publishTime') }}: {{ formatDate(article.createTime) }}</span>
                    <!-- 分享按钮 -->
                    <span class="meta-item like-btn share-btn" @click="shareVisible = true">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <circle cx="18" cy="5" r="3"></circle><circle cx="6" cy="12" r="3"></circle><circle cx="18" cy="19" r="3"></circle>
                            <line x1="8.59" y1="13.51" x2="15.42" y2="17.49"></line><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"></line>
                        </svg>
                        {{ t('article.share.title') }}
                    </span>
                </div>

                <!-- 文章标签区域 -->
                <div v-if="article.tagNameList && article.tagNameList.length > 0" class="tag-wrap">
                    <span class="tag-label">{{ t('article.tag') }}:</span>
                    <el-tag v-for="tag in article.tagNameList" :key="tag" size="small" effect="light">
                        {{ tag }}
                    </el-tag>
                </div>

                <!-- 文章摘要 -->
                <div v-if="article.summary" class="article-summary">
                    {{ article.summary }}
                </div>

                <!-- 正文 + 目录两栏 -->
                <div class="article-body">
                    <!-- Markdown正文内容（DOMPurify 白名单过滤后渲染，防 XSS） -->
                    <div id="markdown-container" class="markdown-body" v-html="sanitizedContent"></div>
                    <!-- 右侧浮动目录 -->
                    <ArticleToc container-selector="#markdown-container" />
                </div>
            </div>
        </div>

        <!-- 评论区 -->
        <CommentSection :article-id="id" />

        <!-- 分享弹窗 -->
        <ArticleShare v-model="shareVisible" :title="article.title" :summary="article.summary" :article-id="id" />
    </div>
</template>

<script setup>
import '~/assets/css/markdown.css'
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'nuxt/app'
import { ElMessage } from 'element-plus'
import DOMPurify from 'dompurify'
import { getFrontArticleDetail, getLikeStatus, likeArticle } from '~/api/article'
import { useUserStore } from '~/stores/store'
import { buildSeoMeta, SITE_URL, SITE_NAME } from '~/utils/seo'
import ReadingProgress from '~/components/ReadingProgress.vue'
import ArticleToc from '~/components/ArticleToc.vue'
import ArticleShare from '~/components/ArticleShare.vue'

definePageMeta({ layout: 'front' })

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

const id = ref('')
const loading = ref(false)
const isError = ref(false)
const likeCount = ref(0)
const isLiked = ref(false)
const likeLoading = ref(false)
const shareVisible = ref(false)
const article = ref({
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
    likeCount: 0,
    createTime: '',
    tagIdList: [],
    tagNameList: []
})

// 对后端返回的 HTML 做白名单过滤，阻断 <script>/onerror 等 XSS 注入
const sanitizedContent = computed(() => {
    return article.value.contentHtml
        ? DOMPurify.sanitize(article.value.contentHtml)
        : ''
})

const initPage = () => {
    id.value = route.query.id
    if (!id.value) {
        isError.value = true
        return
    }
    loadArticleDetail()
    loadLikeStatus()
}

const loadArticleDetail = () => {
    loading.value = true
    isError.value = false
    getFrontArticleDetail(id.value).then(res => {
        article.value = res.data
        // 匿名访客兜底显示文章自带点赞数（登录用户稍后会由 loadLikeStatus 覆盖为精确值）
        likeCount.value = res.data.likeCount || 0
        // SEO meta（基于文章详情动态更新）
        useSeoMeta(buildSeoMeta({
            title: res.data.title,
            description: res.data.summary || `${res.data.title} - ${t('article.seo.readOn')}`,
            type: 'article',
            publishedTime: res.data.createTime,
            updatedTime: res.data.updateTime,
            keywords: res.data.tagNameList ? res.data.tagNameList.join(',') : undefined
        }))
        // JSON-LD 结构化数据（BlogPosting）
        useHead({
            script: [{
                type: 'application/ld+json',
                innerHTML: JSON.stringify({
                    '@context': 'https://schema.org',
                    '@type': 'BlogPosting',
                    headline: res.data.title,
                    description: res.data.summary || res.data.title,
                    image: res.data.cover ? [res.data.cover] : undefined,
                    datePublished: res.data.createTime,
                    dateModified: res.data.updateTime || res.data.createTime,
                    author: { '@type': 'Person', name: res.data.author || SITE_NAME },
                    publisher: { '@type': 'Organization', name: SITE_NAME, url: SITE_URL },
                    mainEntityOfPage: {
                        '@type': 'WebPage',
                        '@id': `${SITE_URL}/article?id=${res.data.id}`
                    },
                    keywords: res.data.tagNameList ? res.data.tagNameList.join(',') : undefined
                })
            }]
        })
    }).catch(() => {
        isError.value = true
        ElMessage.error(t('article.loadFail'))
    }).finally(() => {
        loading.value = false
    })
}

// 仅登录用户拉取精确点赞状态
const loadLikeStatus = () => {
    if (userStore.getToken) {
        getLikeStatus(id.value).then(res => {
            likeCount.value = res.data.likeCount
            isLiked.value = res.data.isLiked
        }).catch(() => { })
    }
}

const handleLike = () => {
    if (!userStore.getToken) {
        ElMessage.warning(t('common.loginFirst'))
        return
    }
    if (likeLoading.value) return

    likeLoading.value = true
    const newLikeStatus = !isLiked.value
    likeArticle({
        articleId: Number(id.value),
        isLike: newLikeStatus
    }).then(res => {
        likeCount.value = res.data.likeCount
        isLiked.value = res.data.isLiked
        ElMessage.success(newLikeStatus ? t('article.likeSuccess') : t('article.likeCancel'))
    }).catch(err => {
        ElMessage.error(err?.data?.msg || err?.msg || t('common.operationFail'))
    }).finally(() => {
        likeLoading.value = false
    })
}

const goBack = () => {
    router.back()
}

const formatDate = (date) => {
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

onMounted(initPage)

// 路由 query.id 变化（如文章间跳转）时重新加载
watch(route, () => { initPage() })
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

.back-text-btn svg {
    width: 16px;
    height: 16px;
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

.article-content > * {
    position: relative;
    z-index: 1;
}

/* 正文 + TOC 两栏布局 */
.article-body {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 200px;
    gap: 40px;
    align-items: start;
}

@media screen and (max-width: 1100px) {
    .article-body {
        grid-template-columns: 1fr;
    }
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

.share-btn{
    color: #77b2ff;
    font-weight: 600;
}
.share-btn:hover {
    color: #6352ff;
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
    line-height: 1.4;
    word-break: break-word;
}

.markdown-body :deep(h2),
.markdown-body :deep(h3) {
    margin-top: 12px;
    margin-bottom: 12px;
    font-weight: 600;
    color: #1d1d1f;
}

.markdown-body :deep(p) {
    margin-bottom: 5px;
}

.markdown-body :deep(hr) {
    margin: 1.5em 0;
}

.markdown-body :deep(img) {
    max-width: 100%;
    border-radius: 12px;
    margin: 16px 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
    padding-left: 26px;
    margin-bottom: 18px;
}

.markdown-body :deep(code) {
    background: #f5f7fa;
    padding: 3px 8px;
    border-radius: 6px;
    font-size: 14px;
    color: #e6a23c;
}

.markdown-body :deep(pre) {
    background: #2d2d2d;
    color: #ccc;
    padding: 20px;
    border-radius: 12px;
    overflow-x: auto;
    margin-bottom: 18px;
}

.markdown-body :deep(pre code) {
    background: transparent;
    color: inherit;
    padding: 0;
}

.markdown-body :deep(blockquote) {
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

body.dark-mode .markdown-body :deep(h2),
body.dark-mode .markdown-body :deep(h3) {
    color: #f5f5f7;
}

body.dark-mode .markdown-body :deep(code) {
    background: rgba(255, 255, 255, 0.1);
}

body.dark-mode .markdown-body :deep(blockquote) {
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