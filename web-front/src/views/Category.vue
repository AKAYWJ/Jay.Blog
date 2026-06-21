<template>
    <div class="category-management">
        <!-- 页面标题 -->
        <h1 class="page-title">分类管理</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.native.prevent="handleSearch">
                <el-form-item label="分类名称">
                    <el-input v-model="searchForm.name" placeholder="请输入分类名称" clearable style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="searchForm.status" placeholder="全部状态" clearable style="width: 140px">
                        <el-option label="显示" :value="1"></el-option>
                        <el-option label="隐藏" :value="0"></el-option>
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
                <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')">新增分类</el-button>
                <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">批量删除</el-button>
            </div>

            <el-table :data="categoryList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="70" align="center"></el-table-column>
                <el-table-column prop="name" label="分类名称" width="180" align="center"></el-table-column>
                <el-table-column prop="sort" label="排序权重" width="100" align="center"></el-table-column>
                <el-table-column label="状态" width="100" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                            {{ scope.row.status === 1 ? '显示' : '隐藏' }}
                        </el-tag>
                    </template>
                </el-table-column>
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
            <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="90px">
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="categoryForm.name" placeholder="请输入分类名称"></el-input>
                </el-form-item>
                <el-form-item label="排序权重" prop="sort">
                    <el-input v-model.number="categoryForm.sort" type="number" placeholder="数字越大越靠前"></el-input>
                </el-form-item>
                <el-form-item label="状态" prop="status">
                    <el-radio-group v-model="categoryForm.status">
                        <el-radio :label="1">显示</el-radio>
                        <el-radio :label="0">隐藏</el-radio>
                    </el-radio-group>
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
import { getCategoryAdminList, addCategory, updateCategory, deleteCategory, batchDeleteCategory } from '@/api/category'

export default {
    name: 'Category',
    data() {
        return {
            searchForm: { name: '', status: null },
            categoryList: [],
            loading: false,
            pageNum: 1,
            pageSize: 10,
            total: 0,
            selectedIds: [],
            dialogVisible: false,
            dialogType: 'add',
            dialogTitle: '新增分类',
            submitLoading: false,
            categoryForm: {
                id: null,
                name: '',
                sort: 0,
                status: 1
            },
            categoryRules: {
                name: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }]
            }
        }
    },
    mounted() {
        this.getCategoryAdminList()
    },
    methods: {
        getCategoryAdminList() {
            this.loading = true
            const params = { pageNum: this.pageNum, pageSize: this.pageSize, ...this.searchForm }
            getCategoryAdminList(params).then(res => {
                this.categoryList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取分类列表失败')
            }).finally(() => {
                this.loading = false
            })
        },
        handleSearch() {
            this.pageNum = 1
            this.getCategoryAdminList()
        },
        handleReset() {
            this.searchForm = { name: '', status: null }
            this.pageNum = 1
            this.getCategoryAdminList()
        },
        handleSizeChange(size) {
            this.pageSize = size
            this.getCategoryAdminList()
        },
        handleCurrentChange(page) {
            this.pageNum = page
            this.getCategoryAdminList()
        },
        handleSelectionChange(selection) {
            this.selectedIds = selection.map(item => item.id)
        },

        openDialog(type, row = null) {
            this.dialogType = type
            this.dialogTitle = type === 'add' ? '新增分类' : '编辑分类'
            this.dialogVisible = true
            this.$nextTick(() => {
                this.$refs.categoryFormRef?.resetFields()
                if (type === 'add') {
                    this.categoryForm = { id: null, name: '', sort: 0, status: 1 }
                } else {
                    this.categoryForm = { ...row }
                }
            })
        },
        handleSubmit() {
            this.$refs.categoryFormRef.validate(valid => {
                if (!valid) return
                this.submitLoading = true
                const api = this.dialogType === 'add' ? addCategory : updateCategory
                api(this.categoryForm).then(() => {
                    this.$message.success('操作成功')
                    this.dialogVisible = false
                    this.getCategoryAdminList()
                }).catch(() => {
                    this.$message.error('操作失败')
                }).finally(() => {
                    this.submitLoading = false
                })
            })
        },

        handleDelete(id) {
            this.$confirm('确定删除该分类？', '提示', { type: 'warning' }).then(() => {
                deleteCategory(id).then(() => {
                    this.$message.success('删除成功')
                    this.getCategoryAdminList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },
        handleBatchDelete() {
            this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个分类？`, '提示', { type: 'warning' }).then(() => {
                batchDeleteCategory(this.selectedIds).then(() => {
                    this.$message.success('批量删除成功')
                    this.getCategoryAdminList()
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
.category-management {
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
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}
</style>