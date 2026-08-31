<template>
    <div class="comment-section">
        <h3 class="section-title">{{ t('article.detail.comment') }} ({{ total }})</h3>

        <div class="comment-input-box">
            <el-input v-model="commentForm.content" type="textarea" :rows="3" :placeholder="t('article.detail.writeComment')" maxlength="500"
                show-word-limit></el-input>
            <div class="submit-bar">
                <span v-if="!isLogin" class="login-tip">{{ t('article.detail.loginToComment') }}</span>
                <el-button :disabled="!commentForm.content.trim() || !isLogin" :loading="submitLoading"
                    @click="submitComment" class="submit-btn">{{ t('article.detail.submitComment') }}</el-button>
            </div>
        </div>

        <div class="comment-list" v-loading="loading">
            <el-empty v-if="commentList.length === 0 && !loading" :description="t('article.detail.emptyComment')"></el-empty>

            <div v-for="comment in commentList" :key="comment.id" class="comment-item">
                <div class="comment-header">
                    <div class="user-avatar">{{ comment.userName?.charAt(0) || 'U' }}</div>
                    <div class="user-info">
                        <div class="user-name">{{ comment.userName || '匿名用户' }}</div>
                        <div class="comment-time">{{ formatDate(comment.createTime) }}</div>
                    </div>
                    <div class="comment-actions">
                        <el-button type="link" size="small" @click="openReply(comment.id)">{{ t('article.detail.reply') }}</el-button>
                        <el-button type="link" size="small" class="delete-btn"
                            v-if="isLogin && String(comment.userId) === String(currentUserId)"
                            @click="handleDelete(comment.id)">{{ t('article.detail.delete') }}</el-button>
                    </div>
                </div>

                <div class="comment-content">{{ comment.content }}</div>

                <div class="reply-list" v-if="comment.replyList && comment.replyList.length > 0">
                    <div v-for="reply in comment.replyList" :key="reply.id" class="reply-item">
                        <div class="reply-main">
                            <span class="reply-user">{{ reply.userName }}：</span>
                            <span class="reply-content">{{ reply.content }}</span>
                        </div>
                        <div class="reply-footer">
                            <span class="reply-time">{{ formatDate(reply.createTime) }}</span>
                            <el-button type="link" size="small" class="delete-btn"
                                v-if="isLogin && String(reply.userId) === String(currentUserId)"
                                @click="handleDelete(reply.id)">{{ t('article.detail.delete') }}</el-button>
                        </div>
                    </div>
                </div>

                <div v-if="activeReplyId === comment.id" class="reply-input-box">
                    <el-input v-model="replyForm.content" size="small" :placeholder="t('article.detail.replyPlaceholder')" maxlength="300"></el-input>
                    <div class="reply-actions">
                        <el-button size="small" @click="activeReplyId = null">{{ t('common.cancel') }}</el-button>
                        <el-button size="small" class="submit-btn" :disabled="!replyForm.content.trim()"
                            :loading="replyLoading" @click="submitReply(comment.id)">{{ t('article.detail.send') }}</el-button>
                    </div>
                </div>
            </div>
        </div>

        <div class="pagination-wrap" v-if="total > pageSize">
            <el-pagination @current-change="handlePageChange" :current-page="pageNum" :page-size="pageSize"
                layout="prev, pager, next" :total="total"></el-pagination>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFrontCommentList, addFrontComment, deleteMyComment } from '~/api/comment'
import { useUserStore } from '~/stores/store'

const props = defineProps({
    articleId: { type: [String, Number], required: true }
})

const userStore = useUserStore()
const { t } = useI18n()

const commentList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const submitLoading = ref(false)
const replyLoading = ref(false)
const activeReplyId = ref(null)
const commentForm = ref({ content: '' })
const replyForm = ref({ content: '' })

const currentUserId = computed(() => userStore.getUserId)
const isLogin = computed(() => userStore.isLoggedIn)

const loadCommentList = () => {
    loading.value = true
    getFrontCommentList({ articleId: props.articleId, pageNum: pageNum.value, pageSize: pageSize.value })
        .then(res => { commentList.value = res.data.records; total.value = res.data.total })
        .catch(() => { ElMessage.error(t('comment.loadFail')) })
        .finally(() => { loading.value = false })
}

const submitComment = () => {
    if (!isLogin.value) return ElMessage.warning(t('common.loginFirst'))
    submitLoading.value = true
    addFrontComment({ articleId: props.articleId, parentId: null, content: commentForm.value.content.trim() })
        .then(() => { ElMessage.success(t('comment.submitSuccess')); commentForm.value.content = ''; pageNum.value = 1; loadCommentList() })
        .catch(() => { ElMessage.error(t('comment.submitFail')) })
        .finally(() => { submitLoading.value = false })
}

const openReply = (commentId) => {
    if (!isLogin.value) return ElMessage.warning(t('common.loginFirst'))
    activeReplyId.value = commentId
    replyForm.value.content = ''
}

const submitReply = (parentId) => {
    replyLoading.value = true
    addFrontComment({ articleId: props.articleId, parentId, content: replyForm.value.content.trim() })
        .then(() => { ElMessage.success(t('comment.replySuccess')); activeReplyId.value = null; replyForm.value.content = ''; loadCommentList() })
        .catch(() => { ElMessage.error(t('comment.replyFail')) })
        .finally(() => { replyLoading.value = false })
}

const handleDelete = (id) => {
    ElMessageBox.confirm(t('comment.deleteConfirm'), t('common.confirm'), { type: 'warning' })
        .then(() => deleteMyComment(id).then(() => { ElMessage.success(t('comment.deleted')); loadCommentList() }))
        .catch(() => {})
}

const handlePageChange = (page) => { pageNum.value = page; loadCommentList() }

const formatDate = (date) => {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

watch(() => props.articleId, () => { pageNum.value = 1; loadCommentList() }, { immediate: true })
</script>

<style scoped>
.comment-section {
    margin-top: 36px;
    padding-top: 28px;
    border-top: 1px solid var(--color-border);
}

.section-title {
    font-size: 18px;
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0 0 20px;
}

.comment-input-box {
    margin-bottom: 28px;
}

.comment-input-box :deep(.el-textarea__inner) {
    border-radius: var(--radius-md);
    border-color: var(--color-border);
    transition: var(--transition-fast);
}

.comment-input-box :deep(.el-textarea__inner:focus) {
    border-color: var(--color-primary);
}

.submit-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
}

.login-tip {
    color: var(--color-text-muted);
    font-size: 13px;
}

.submit-btn {
    background: var(--color-primary-gradient);
    border: none;
    border-radius: 999px;
    color: #fff;
}

.submit-btn:hover { filter: brightness(1.08); }

.comment-list {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.comment-item {
    padding: 16px 20px;
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    box-shadow: var(--liquid-glass-shadow);
}

.comment-item > * {
    position: relative;
    z-index: 1;
}

.comment-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 10px;
}

.user-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 14px;
    font-weight: 600;
    flex-shrink: 0;
}

.user-info { flex: 1; }

.user-name {
    font-size: 14px;
    font-weight: 500;
    color: var(--color-text-primary);
}

.comment-time {
    font-size: 12px;
    color: var(--color-text-muted);
    margin-top: 2px;
}

.comment-actions {
    display: flex;
    gap: 8px;
}

.delete-btn {
    color: var(--color-text-muted) !important;
}

.delete-btn:hover {
    color: #D32F2F !important;
}

.comment-content {
    font-size: 14px;
    line-height: 1.7;
    color: var(--color-text-primary);
    padding-left: 48px;
    word-break: break-word;
}

.reply-list {
    margin-top: 14px;
    margin-left: 48px;
    padding: 14px 18px;
    background: var(--color-primary-light-bg);
    border-radius: var(--radius-md);
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.reply-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
}

.reply-main {
    font-size: 13px;
    line-height: 1.6;
}

.reply-user {
    color: var(--color-primary);
    font-weight: 500;
}

.reply-content {
    color: var(--color-text-primary);
}

.reply-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.reply-time {
    color: var(--color-text-muted);
    font-size: 12px;
}

.reply-input-box {
    margin-left: 48px;
    margin-top: 12px;
}

.reply-input-box :deep(.el-input__inner) {
    border-radius: var(--radius-sm);
}

.reply-actions {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
}

.pagination-wrap {
    display: flex;
    justify-content: center;
    margin-top: 28px;
}

body.dark-mode .comment-section {
    border-top-color: var(--color-dark-border);
}

body.dark-mode .user-avatar {
    background: rgba(91, 79, 207, 0.15);
}

body.dark-mode .reply-list {
    background: rgba(91, 79, 207, 0.06);
}

body.dark-mode .comment-item {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
    border-bottom-color: var(--color-dark-border);
}
</style>