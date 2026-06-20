<template>
    <div class="comment-management">
        <!-- 页面标题 -->
        <h1 class="page-title">评论管理</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.native.prevent="handleSearch">
                <el-form-item label="文章ID">
                    <el-input v-model="searchForm.articleId" placeholder="输入文章ID查询" clearable
                        style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item label="审核状态">
                    <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
                        <el-option label="待审核" value="0"></el-option>
                        <el-option label="正常展示" value="1"></el-option>
                        <el-option label="屏蔽" value="2"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
                    <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 操作按钮 + 表格区域 -->
        <div class="table-card">
            <div class="table-header">
                <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">批量删除</el-button>
            </div>

            <el-table :data="commentList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="70" align="center"></el-table-column>
                <el-table-column prop="articleId" label="所属文章ID" width="110" align="center"></el-table-column>
                <el-table-column prop="nickname" label="评论昵称" width="140" align="center"></el-table-column>
                <el-table-column prop="email" label="邮箱" min-width="180" align="center"></el-table-column>
                <el-table-column prop="content" label="评论内容" min-width="260" align="left"></el-table-column>
                <el-table-column label="审核状态" width="110" align="center">
                    <template slot-scope="scope">
                        <el-tag
                            :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'danger'"
                            size="small">
                            {{ scope.row.status === 0 ? '待审核' : scope.row.status === 1 ? '正常' : '屏蔽' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="评论时间" width="170" align="center">
                    <template slot-scope="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="220" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-check" @click="auditPass(scope.row)"
                            v-if="scope.row.status !== 1">通过</el-button>
                        <el-button type="text" icon="el-icon-close" style="color:orange" @click="auditBlock(scope.row)"
                            v-if="scope.row.status !== 2">屏蔽</el-button>
                        <el-button type="text" icon="el-icon-delete" style="color: #ef4444"
                            @click="handleDelete(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="pageNum" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
            </div>
        </div>
    </div>
</template>

<script>
import { getCommentList, updateComment, deleteComment, batchDeleteComment } from '@/api/comment'

export default {
    name: 'Comment',
    data() {
        return {
            searchForm: { articleId: null, status: null },
            commentList: [],
            loading: false,
            pageNum: 1,
            pageSize: 10,
            total: 0,
            selectedIds: []
        }
    },
    mounted() {
        this.getCommentList()
    },
    methods: {
        getCommentList() {
            this.loading = true
            const params = { pageNum: this.pageNum, pageSize: this.pageSize, ...this.searchForm }
            getCommentList(params).then(res => {
                this.commentList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取评论列表失败')
            }).finally(() => {
                this.loading = false
            })
        },
        handleSearch() {
            this.pageNum = 1
            this.getCommentList()
        },
        handleReset() {
            this.searchForm = { articleId: null, status: null }
            this.pageNum = 1
            this.getCommentList()
        },
        handleSizeChange(size) {
            this.pageSize = size
            this.getCommentList()
        },
        handleCurrentChange(page) {
            this.pageNum = page
            this.getCommentList()
        },
        handleSelectionChange(selection) {
            this.selectedIds = selection.map(item => item.id)
        },

        // 审核通过
        auditPass(row) {
            row.status = 1
            updateComment(row).then(() => {
                this.$message.success('审核通过')
                this.getCommentList()
            })
        },
        // 屏蔽评论
        auditBlock(row) {
            row.status = 2
            updateComment(row).then(() => {
                this.$message.success('已屏蔽该评论')
                this.getCommentList()
            })
        },

        // 单条删除
        handleDelete(id) {
            this.$confirm('确定删除该评论？', '提示', { type: 'warning' }).then(() => {
                deleteComment(id).then(() => {
                    this.$message.success('删除成功')
                    this.getCommentList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },
        // 批量删除
        handleBatchDelete() {
            this.$confirm(`确定删除选中的 ${this.selectedIds.length} 条评论？`, '提示', { type: 'warning' }).then(() => {
                batchDeleteComment(this.selectedIds).then(() => {
                    this.$message.success('批量删除成功')
                    this.getCommentList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },

        // 时间格式化
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, 0)}-${String(d.getDate()).padStart(2, 0)} ${String(d.getHours()).padStart(2, 0)}:${String(d.getMinutes()).padStart(2, 0)}`
        }
    }
}
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
    background: var(--color-white);
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-card);
    padding: var(--spacing-card);
    margin-bottom: var(--spacing-gap);
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
    background: var(--color-dark-surface);
    box-shadow: var(--shadow-card-dark);
}
</style>