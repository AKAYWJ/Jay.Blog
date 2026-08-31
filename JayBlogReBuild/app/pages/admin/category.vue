<template>
    <div class="category-management">
        <h1 class="page-title">{{ t('admin.category.title') }}</h1>

        <div class="search-card">
            <el-form :model="searchForm" inline @submit.prevent="handleSearch">
                <el-form-item :label="t('admin.category.name')">
                    <el-input v-model="searchForm.name" :placeholder="t('admin.category.namePlaceholder')" clearable style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.common.status')">
                    <el-select v-model="searchForm.status" :placeholder="t('admin.category.allStatus')" clearable style="width: 140px">
                        <el-option :label="t('admin.category.visible')" :value="1"></el-option>
                        <el-option :label="t('admin.category.hidden')" :value="0"></el-option>
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
                <el-button type="primary" :icon="Plus" @click="openDialog('add')">{{ t('admin.category.addTitle') }}</el-button>
                <el-button type="danger" :icon="Delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">{{ t('admin.common.batchDelete') }}</el-button>
            </div>

            <el-table :data="categoryList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" :label="t('admin.common.id')" width="70" align="center"></el-table-column>
                <el-table-column prop="name" :label="t('admin.category.name')" min-width="180" align="center"></el-table-column>
                <el-table-column prop="sort" :label="t('admin.category.sortWeight')" width="100" align="center"></el-table-column>
                <el-table-column :label="t('admin.common.status')" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                            {{ scope.row.status === 1 ? t('admin.category.visible') : t('admin.category.hidden') }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :label="t('admin.common.createTime')" width="180" align="center">
                    <template #default="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column :label="t('admin.common.actions')" width="180" align="center" fixed="right">
                    <template #default="scope">
                        <el-button type="text" :icon="Edit" @click="openDialog('edit', scope.row)">{{ t('admin.common.edit') }}</el-button>
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

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" :close-on-click-modal="false"
            destroy-on-close>
            <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="90px">
                <el-form-item :label="t('admin.category.name')" prop="name">
                    <el-input v-model="categoryForm.name" :placeholder="t('admin.category.namePlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.category.sortWeight')" prop="sort">
                    <el-input v-model.number="categoryForm.sort" type="number" :placeholder="t('admin.category.sortPlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.common.status')" prop="status">
                    <el-radio-group v-model="categoryForm.status">
                        <el-radio :label="1">{{ t('admin.category.visible') }}</el-radio>
                        <el-radio :label="0">{{ t('admin.category.hidden') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
                    <el-button type="primary" :loading="submitLoading" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import {
    getCategoryAdminList,
    addCategory,
    updateCategory,
    deleteCategory,
    batchDeleteCategory
} from '~/api/category'
import type { Category } from '~/api/category'

const { t } = useI18n()

const searchForm = reactive({ name: '', status: null as number | null })
const categoryList = ref<Category[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const dialogTitle = ref(t('admin.category.addTitle'))
const submitLoading = ref(false)
const categoryFormRef = ref()
const categoryForm = reactive<Category>({
    id: null,
    name: '',
    sort: 0,
    status: 1
})
const categoryRules = {
    name: [{ required: true, message: t('admin.category.nameRequired'), trigger: 'blur' }]
}

function fetchCategoryList() {
    loading.value = true
    const params = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }
    getCategoryAdminList(params).then(res => {
        categoryList.value = res.data.records
        total.value = res.data.total
    }).catch(() => {
        ElMessage.error(t('admin.category.fetchFail'))
    }).finally(() => {
        loading.value = false
    })
}

function handleSearch() {
    pageNum.value = 1
    fetchCategoryList()
}

function handleReset() {
    searchForm.name = ''
    searchForm.status = null
    pageNum.value = 1
    fetchCategoryList()
}

function handleSizeChange(size: number) {
    pageSize.value = size
    fetchCategoryList()
}

function handleCurrentChange(page: number) {
    pageNum.value = page
    fetchCategoryList()
}

function handleSelectionChange(selection: Category[]) {
    selectedIds.value = selection.map(item => item.id as number)
}

function openDialog(type: 'add' | 'edit', row: Category | null = null) {
    dialogType.value = type
    dialogTitle.value = type === 'add' ? t('admin.category.addTitle') : t('admin.category.editTitle')
    dialogVisible.value = true
    nextTick(() => {
        categoryFormRef.value?.resetFields()
        if (type === 'add') {
            Object.assign(categoryForm, { id: null, name: '', sort: 0, status: 1 })
        } else if (row) {
            Object.assign(categoryForm, row)
        }
    })
}

function handleSubmit() {
    categoryFormRef.value.validate((valid: boolean) => {
        if (!valid) return
        submitLoading.value = true
        const api = dialogType.value === 'add' ? addCategory : updateCategory
        api(categoryForm).then(() => {
            ElMessage.success(t('admin.common.success'))
            dialogVisible.value = false
            fetchCategoryList()
        }).catch(() => {
            ElMessage.error(t('admin.common.fail'))
        }).finally(() => {
            submitLoading.value = false
        })
    })
}

function handleDelete(id: number) {
    ElMessageBox.confirm(t('admin.category.deleteConfirm'), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        deleteCategory(id).then(() => {
            ElMessage.success(t('admin.common.deleteSuccess'))
            fetchCategoryList()
        })
    }).catch(() => { /* cancel */ })
}

function handleBatchDelete() {
    ElMessageBox.confirm(t('admin.category.batchDeleteConfirm', { count: selectedIds.value.length }), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        batchDeleteCategory(selectedIds.value).then(() => {
            ElMessage.success(t('admin.common.batchDeleteSuccess'))
            fetchCategoryList()
        })
    }).catch(() => { /* cancel */ })
}

function formatDate(date?: string) {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
    fetchCategoryList()
})
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
