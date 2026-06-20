<template>
    <div class="tag-management">
        <!-- 页面标题 -->
        <h1 class="page-title">标签管理</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.native.prevent="handleSearch">
                <el-form-item label="标签名称">
                    <el-input v-model="searchForm.name" placeholder="请输入标签名称" clearable style="width: 220px"></el-input>
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
                <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')">新增标签</el-button>
                <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">批量删除</el-button>
            </div>

            <el-table :data="tagList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="70" align="center"></el-table-column>
                <el-table-column prop="name" label="标签名称" width="200" align="center"></el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="170" align="center">
                    <template slot-scope="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="180" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-edit" @click="openDialog('edit', scope.row)">编辑</el-button>
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

        <!-- 新增/编辑弹窗 -->
        <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" :close-on-click-modal="false"
            destroy-on-close>
            <el-form ref="tagFormRef" :model="tagForm" :rules="tagRules" label-width="90px">
                <el-form-item label="标签名称" prop="name">
                    <el-input v-model="tagForm.name" placeholder="请输入标签名称"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { getTagList, addTag, updateTag, deleteTag, batchDeleteTag } from '@/api/tag'

export default {
    name: 'Tag',
    data() {
        return {
            searchForm: { name: '' },
            tagList: [],
            loading: false,
            pageNum: 1,
            pageSize: 10,
            total: 0,
            selectedIds: [],
            dialogVisible: false,
            dialogType: 'add',
            dialogTitle: '新增标签',
            submitLoading: false,
            tagForm: {
                id: null,
                name: ''
            },
            tagRules: {
                name: [{ required: true, message: '标签名称不能为空', trigger: 'blur' }]
            }
        }
    },
    mounted() {
        this.getTagList()
    },
    methods: {
        getTagList() {
            this.loading = true
            const params = { pageNum: this.pageNum, pageSize: this.pageSize, ...this.searchForm }
            getTagList(params).then(res => {
                this.tagList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取标签列表失败')
            }).finally(() => {
                this.loading = false
            })
        },
        handleSearch() {
            this.pageNum = 1
            this.getTagList()
        },
        handleReset() {
            this.searchForm = { name: '' }
            this.pageNum = 1
            this.getTagList()
        },
        handleSizeChange(size) {
            this.pageSize = size
            this.getTagList()
        },
        handleCurrentChange(page) {
            this.pageNum = page
            this.getTagList()
        },
        handleSelectionChange(selection) {
            this.selectedIds = selection.map(item => item.id)
        },

        openDialog(type, row = null) {
            this.dialogType = type
            this.dialogTitle = type === 'add' ? '新增标签' : '编辑标签'
            this.dialogVisible = true
            this.$nextTick(() => {
                this.$refs.tagFormRef?.resetFields()
                if (type === 'add') {
                    this.tagForm = { id: null, name: '' }
                } else {
                    this.tagForm = { ...row }
                }
            })
        },
        handleSubmit() {
            this.$refs.tagFormRef.validate(valid => {
                if (!valid) return
                this.submitLoading = true
                const api = this.dialogType === 'add' ? addTag : updateTag
                api(this.tagForm).then(() => {
                    this.$message.success('操作成功')
                    this.dialogVisible = false
                    this.getTagList()
                }).catch(() => {
                    this.$message.error('操作失败')
                }).finally(() => {
                    this.submitLoading = false
                })
            })
        },

        handleDelete(id) {
            this.$confirm('确定删除该标签？', '提示', { type: 'warning' }).then(() => {
                deleteTag(id).then(() => {
                    this.$message.success('删除成功')
                    this.getTagList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },
        handleBatchDelete() {
            this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个标签？`, '提示', { type: 'warning' }).then(() => {
                batchDeleteTag(this.selectedIds).then(() => {
                    this.$message.success('批量删除成功')
                    this.getTagList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },

        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, 0)}-${String(d.getDate()).padStart(2, 0)} ${String(d.getHours()).padStart(2, 0)}:${String(d.getMinutes()).padStart(2, 0)}`
        }
    }
}
</script>

<style scoped>
.tag-management {
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
    justify-content: space-between;
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