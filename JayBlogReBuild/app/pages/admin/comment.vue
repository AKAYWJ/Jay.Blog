<template>
    <div class="comment-management">
        <h1 class="page-title">{{ t('admin.comment.title') }}</h1>

        <div class="search-card">
            <el-form :model="searchForm" inline @submit.prevent="handleSearch">
                <el-form-item :label="t('admin.comment.articleId')">
                    <el-input v-model="searchForm.articleId" :placeholder="t('admin.comment.articleIdPlaceholder')" clearable
                        style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.comment.auditStatus')">
                    <el-select v-model="searchForm.status" :placeholder="t('admin.comment.allStatus')" clearable style="width: 140px">
                        <el-option :label="t('admin.comment.pending')" :value="0"></el-option>
                        <el-option :label="t('admin.comment.normalDisplay')" :value="1"></el-option>
                        <el-option :label="t('admin.comment.blocked')" :value="2"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('admin.common.search') }}</el-button>
                    <el-button :icon="Refresh" @click="handleReset">{{ t('admin.common.reset') }}</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="table-card">
            <div class="table-header">
                <el-button type="danger" :icon="Delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">{{ t('admin.common.batchDelete') }}</el-button>
            </div>

            <el-table :data="commentList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" :label="t('admin.common.id')" width="70" align="center"></el-table-column>
                <el-table-column prop="articleId" :label="t('admin.comment.articleId')" width="110" align="center"></el-table-column>
                <el-table-column prop="nickname" :label="t('admin.comment.nickname')" width="140" align="center"></el-table-column>
                <el-table-column prop="email" :label="t('admin.comment.email')" min-width="180" align="center"></el-table-column>
                <el-table-column prop="content" :label="t('admin.comment.content')" min-width="260" align="left"></el-table-column>
                <el-table-column :label="t('admin.comment.auditStatus')" width="110" align="center">
                    <template #default="scope">
                        <el-tag
                            :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'danger'"
                            size="small">
                            {{ scope.row.status === 0 ? t('admin.comment.pending') : scope.row.status === 1 ? t('admin.comment.normal') : t('admin.comment.blocked') }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :label="t('admin.comment.commentTime')" width="170" align="center">
                    <template #default="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column :label="t('admin.common.actions')" width="220" align="center" fixed="right">
                    <template #default="scope">
                        <el-button type="text" :icon="Check" @click="auditPass(scope.row)"
                            v-if="scope.row.status !== 1">{{ t('admin.comment.pass') }}</el-button>
                        <el-button type="text" :icon="Close" style="color:orange" @click="auditBlock(scope.row)"
                            v-if="scope.row.status !== 2">{{ t('admin.comment.block') }}</el-button>
                        <el-button type="text" :icon="Delete" style="color: #ef4444"
                            @click="handleDelete(scope.row.id)">{{ t('admin.common.delete') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <div class="pagination-container">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pageNum" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete, Check, Close } from '@element-plus/icons-vue'
import { getCommentList, updateComment, deleteComment, batchDeleteComment } from '~/api/comment'
import type { Comment } from '~/api/comment'

const { t } = useI18n()

const searchForm = ref<{ articleId: number | null; status: number | null }>({ articleId: null, status: null })
const commentList = ref<Comment[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref<number[]>([])

function fetchCommentList() {
    loading.value = true
    const params: any = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm.value }
    if (params.articleId === null || params.articleId === '') delete params.articleId
    if (params.status === null || params.status === '') delete params.status
    getCommentList(params).then(res => {
        commentList.value = res.data.records
        total.value = res.data.total
    }).catch(() => {
        ElMessage.error(t('admin.comment.fetchFail'))
    }).finally(() => {
        loading.value = false
    })
}

function handleSearch() {
    pageNum.value = 1
    fetchCommentList()
}

function handleReset() {
    searchForm.value = { articleId: null, status: null }
    pageNum.value = 1
    fetchCommentList()
}

function handleSizeChange(size: number) {
    pageSize.value = size
    fetchCommentList()
}

function handleCurrentChange(page: number) {
    pageNum.value = page
    fetchCommentList()
}

function handleSelectionChange(selection: Comment[]) {
    selectedIds.value = selection.map(item => item.id as number)
}

function auditPass(row: Comment) {
    row.status = 1
    updateComment(row).then(() => {
        ElMessage.success(t('admin.comment.auditPassSuccess'))
        fetchCommentList()
    })
}

function auditBlock(row: Comment) {
    row.status = 2
    updateComment(row).then(() => {
        ElMessage.success(t('admin.comment.blockSuccess'))
        fetchCommentList()
    })
}

function handleDelete(id: number) {
    ElMessageBox.confirm(t('admin.comment.deleteConfirm'), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        deleteComment(id).then(() => {
            ElMessage.success(t('admin.common.deleteSuccess'))
            fetchCommentList()
        })
    }).catch(() => { /* cancel */ })
}

function handleBatchDelete() {
    ElMessageBox.confirm(t('admin.comment.batchDeleteConfirm', { count: selectedIds.value.length }), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        batchDeleteComment(selectedIds.value).then(() => {
            ElMessage.success(t('admin.common.batchDeleteSuccess'))
            fetchCommentList()
        })
    }).catch(() => { /* cancel */ })
}

function formatDate(date?: string) {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
    fetchCommentList()
})
</script>

<style scoped>
.comment-management {
    padding: 0;
}

.page-title {
    font-size: var(--font-size-page-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin-bottom: var(--spacing-page);
}

.search-card,
.table-card {
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    box-shadow: var(--liquid-glass-shadow);
    padding: var(--spacing-card);
    margin-bottom: var(--spacing-gap);
}

.search-card > *,
.table-card > * {
    position: relative;
    z-index: 1;
}

.table-header {
    display: flex;
    justify-content: flex-start;
    margin-bottom: var(--spacing-gap);
}

.pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: var(--spacing-gap);
}

body.dark-mode .page-title {
    color: var(--color-dark-text);
}

body.dark-mode .search-card,
body.dark-mode .table-card {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}
</style>
