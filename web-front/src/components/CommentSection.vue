<template>
    <div class="comment-section">
        <h3 class="section-title">评论 ({{ total }})</h3>

        <div class="comment-input-box">
            <el-input v-model="commentForm.content" type="textarea" :rows="3" placeholder="说点什么..." maxlength="500"
                show-word-limit></el-input>
            <div class="submit-bar">
                <span v-if="!isLogin" class="login-tip">登录后发表评论</span>
                <el-button :disabled="!commentForm.content.trim() || !isLogin" :loading="submitLoading"
                    @click="submitComment" class="submit-btn">发表</el-button>
            </div>
        </div>

        <div class="comment-list" v-loading="loading">
            <el-empty v-if="commentList.length === 0 && !loading" description="还没有评论"></el-empty>

            <div v-for="comment in commentList" :key="comment.id" class="comment-item">
                <div class="comment-header">
                    <div class="user-avatar">{{ comment.userName?.charAt(0) || 'U' }}</div>
                    <div class="user-info">
                        <div class="user-name">{{ comment.userName || '匿名用户' }}</div>
                        <div class="comment-time">{{ formatDate(comment.createTime) }}</div>
                    </div>
                    <div class="comment-actions">
                        <el-button type="text" size="small" @click="openReply(comment.id)">回复</el-button>
                        <el-button type="text" size="small" class="delete-btn"
                            v-if="isLogin && String(comment.userId) === String(currentUserId)"
                            @click="handleDelete(comment.id)">删除</el-button>
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
                            <el-button type="text" size="mini" class="delete-btn"
                                v-if="isLogin && String(reply.userId) === String(currentUserId)"
                                @click="handleDelete(reply.id)">删除</el-button>
                        </div>
                    </div>
                </div>

                <div v-if="activeReplyId === comment.id" class="reply-input-box">
                    <el-input v-model="replyForm.content" size="small" placeholder="回复..." maxlength="300"></el-input>
                    <div class="reply-actions">
                        <el-button size="mini" @click="activeReplyId = null">取消</el-button>
                        <el-button size="mini" class="submit-btn" :disabled="!replyForm.content.trim()"
                            :loading="replyLoading" @click="submitReply(comment.id)">发送</el-button>
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

<script>
import { mapGetters } from 'vuex'
import { getFrontCommentList, addComment, addReply, deleteMyComment } from '@/api/comment'

export default {
    name: 'CommentSection',
    props: { articleId: { type: [String, Number], required: true } },
    computed: {
        ...mapGetters(['getUserId']),
        currentUserId() { return this.getUserId },
        isLogin() { return !!localStorage.getItem('token') }
    },
    data() {
        return {
            commentList: [], total: 0, pageNum: 1, pageSize: 10,
            loading: false, submitLoading: false, replyLoading: false,
            activeReplyId: null,
            commentForm: { content: '' },
            replyForm: { content: '' }
        }
    },
    watch: {
        articleId: { immediate: true, handler() { this.pageNum = 1; this.loadCommentList() } }
    },
    methods: {
        loadCommentList() {
            this.loading = true
            getFrontCommentList({ articleId: this.articleId, pageNum: this.pageNum, pageSize: this.pageSize })
                .then(res => { this.commentList = res.data.records; this.total = res.data.total })
                .catch(() => { this.$message.error('获取评论失败') })
                .finally(() => { this.loading = false })
        },
        submitComment() {
            if (!this.isLogin) return this.$message.warning('请先登录')
            this.submitLoading = true
            addComment({ articleId: this.articleId, content: this.commentForm.content.trim() })
                .then(() => { this.$message.success('评论成功'); this.commentForm.content = ''; this.pageNum = 1; this.loadCommentList() })
                .catch(() => { this.$message.error('评论失败') })
                .finally(() => { this.submitLoading = false })
        },
        openReply(commentId) {
            if (!this.isLogin) return this.$message.warning('请先登录')
            this.activeReplyId = commentId; this.replyForm.content = ''
        },
        submitReply(parentId) {
            this.replyLoading = true
            addReply({ articleId: this.articleId, parentId, content: this.replyForm.content.trim() })
                .then(() => { this.$message.success('回复成功'); this.activeReplyId = null; this.replyForm.content = ''; this.loadCommentList() })
                .catch(() => { this.$message.error('回复失败') })
                .finally(() => { this.replyLoading = false })
        },
        handleDelete(id) {
            this.$confirm('确定删除该评论？', '提示', { type: 'warning' })
                .then(() => deleteMyComment(id).then(() => { this.$message.success('已删除'); this.loadCommentList() }))
                .catch(() => {})
        },
        handlePageChange(page) { this.pageNum = page; this.loadCommentList() },
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
        }
    }
}
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

.comment-input-box /deep/ .el-textarea__inner {
    border-radius: var(--radius-md);
    border-color: var(--color-border);
    transition: var(--transition-fast);
}

.comment-input-box /deep/ .el-textarea__inner:focus {
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

.reply-input-box /deep/ .el-input__inner {
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
