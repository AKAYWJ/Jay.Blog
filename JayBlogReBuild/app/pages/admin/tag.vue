<template>
    <div class="tag-management">
        <h1 class="page-title">{{ t('admin.tag.title') }}</h1>

        <div class="search-card">
            <el-form :model="searchForm" inline @submit.prevent="handleSearch">
                <el-form-item :label="t('admin.tag.name')">
                    <el-input v-model="searchForm.name" :placeholder="t('admin.tag.namePlaceholder')" clearable style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('admin.common.search') }}</el-button>
                    <el-button :icon="Refresh" @click="handleReset">{{ t('admin.common.reset') }}</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="table-card">
            <div class="table-header">
                <el-button type="primary" :icon="Plus" @click="openDialog('add')">{{ t('admin.tag.addTitle') }}</el-button>
                <el-button type="danger" :icon="Delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">{{ t('admin.common.batchDelete') }}</el-button>
            </div>

            <el-table :data="tagList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" :label="t('admin.common.id')" width="70" align="center"></el-table-column>
                <el-table-column prop="name" :label="t('admin.tag.name')" min-width="200" align="center"></el-table-column>
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
            <el-form ref="tagFormRef" :model="tagForm" :rules="tagRules" label-width="90px">
                <el-form-item :label="t('admin.tag.name')" prop="name">
                    <el-input v-model="tagForm.name" :placeholder="t('admin.tag.namePlaceholder')"></el-input>
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
    getTagList,
    addTag,
    updateTag,
    deleteTag,
    batchDeleteTag
} from '~/api/tag'
import type { Tag } from '~/api/tag'

const { t } = useI18n()

const searchForm = reactive({ name: '' })
const tagList = ref<Tag[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const dialogTitle = ref(t('admin.tag.addTitle'))
const submitLoading = ref(false)
const tagFormRef = ref()
const tagForm = reactive<Tag>({
    id: null,
    name: ''
})
const tagRules = {
    name: [{ required: true, message: t('admin.tag.nameRequired'), trigger: 'blur' }]
}

function fetchTagList() {
    loading.value = true
    const params = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }
    getTagList(params).then(res => {
        tagList.value = res.data.records
        total.value = res.data.total
    }).catch(() => {
        ElMessage.error(t('admin.tag.fetchFail'))
    }).finally(() => {
        loading.value = false
    })
}

function handleSearch() {
    pageNum.value = 1
    fetchTagList()
}

function handleReset() {
    searchForm.name = ''
    pageNum.value = 1
    fetchTagList()
}

function handleSizeChange(size: number) {
    pageSize.value = size
    fetchTagList()
}

function handleCurrentChange(page: number) {
    pageNum.value = page
    fetchTagList()
}

function handleSelectionChange(selection: Tag[]) {
    selectedIds.value = selection.map(item => item.id as number)
}

function openDialog(type: 'add' | 'edit', row: Tag | null = null) {
    dialogType.value = type
    dialogTitle.value = type === 'add' ? t('admin.tag.addTitle') : t('admin.tag.editTitle')
    dialogVisible.value = true
    nextTick(() => {
        tagFormRef.value?.resetFields()
        if (type === 'add') {
            Object.assign(tagForm, { id: null, name: '' })
        } else if (row) {
            Object.assign(tagForm, row)
        }
    })
}

function handleSubmit() {
    tagFormRef.value.validate((valid: boolean) => {
        if (!valid) return
        submitLoading.value = true
        const api = dialogType.value === 'add' ? addTag : updateTag
        api(tagForm).then(() => {
            ElMessage.success(t('admin.common.success'))
            dialogVisible.value = false
            fetchTagList()
        }).catch(() => {
            ElMessage.error(t('admin.common.fail'))
        }).finally(() => {
            submitLoading.value = false
        })
    })
}

function handleDelete(id: number) {
    ElMessageBox.confirm(t('admin.tag.deleteConfirm'), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        deleteTag(id).then(() => {
            ElMessage.success(t('admin.common.deleteSuccess'))
            fetchTagList()
        })
    }).catch(() => { /* cancel */ })
}

function handleBatchDelete() {
    ElMessageBox.confirm(t('admin.tag.batchDeleteConfirm', { count: selectedIds.value.length }), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        batchDeleteTag(selectedIds.value).then(() => {
            ElMessage.success(t('admin.common.batchDeleteSuccess'))
            fetchTagList()
        })
    }).catch(() => { /* cancel */ })
}

function formatDate(date?: string) {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
    fetchTagList()
})
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
