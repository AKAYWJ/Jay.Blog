<template>
    <div class="article-management">
        <!-- 页面标题 -->
        <h1 class="page-title">{{ t('admin.article.title') }}</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.prevent="handleSearch">
                <el-form-item :label="t('admin.article.articleTitle')">
                    <el-input v-model="searchForm.title" :placeholder="t('admin.article.titlePlaceholder')" clearable
                        style="width: 220px"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.article.category')">
                    <el-select v-model="searchForm.categoryId" :placeholder="t('admin.article.allCategory')" clearable style="width: 180px">
                        <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('admin.article.publishStatus')">
                    <el-select v-model="searchForm.status" :placeholder="t('admin.article.allStatus')" clearable style="width: 140px">
                        <el-option :label="t('admin.article.draft')" :value="1"></el-option>
                        <el-option :label="t('admin.article.published')" :value="2"></el-option>
                        <el-option :label="t('admin.article.private')" :value="3"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('admin.common.search') }}</el-button>
                    <el-button :icon="Refresh" @click="handleReset">{{ t('admin.common.reset') }}</el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 操作按钮 + 表格区域 -->
        <div class="table-card">
            <div class="table-header">
                <el-button type="primary" :icon="Plus" @click="openDialog('add')">{{ t('admin.article.addTitle') }}</el-button>
                <el-button type="danger" :icon="Delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">{{ t('admin.common.batchDelete') }}</el-button>
            </div>

            <el-table :data="articleList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" :label="t('admin.common.id')" width="70" align="center"></el-table-column>
                <el-table-column prop="title" :label="t('admin.article.articleTitle')" min-width="220" align="left"></el-table-column>
                <el-table-column prop="categoryName" :label="t('admin.article.category')" width="120" align="center"></el-table-column>
                <el-table-column :label="t('admin.article.tags')" width="160" align="center">
                    <template #default="scope">
                        <span v-if="scope.row.tagNameList && scope.row.tagNameList.length > 0">
                            <el-tag v-for="tag in scope.row.tagNameList" :key="tag" size="small" style="margin:0 2px;">{{
                                tag }}</el-tag>
                        </span>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column :label="t('admin.article.topStatus')" width="90" align="center">
                    <template #default="scope">
                        <el-tag v-if="scope.row.isTop === 1" type="warning" size="small">{{ t('admin.article.top') }}</el-tag>
                        <span v-else>-</span>
                    </template>
                </el-table-column>
                <el-table-column :label="t('admin.article.publishStatus')" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 2 ? 'success' : scope.row.status === 1 ? 'info' : 'danger'"
                            size="small">
                            {{ scope.row.status === 1 ? t('admin.article.draft') : scope.row.status === 2 ? t('admin.article.published') : t('admin.article.private') }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="viewCount" :label="t('admin.article.viewCount')" width="90" align="center"></el-table-column>
                <el-table-column prop="createTime" :label="t('admin.common.createTime')" width="170" align="center">
                    <template #default="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column :label="t('admin.common.actions')" width="180" align="center">
                    <template #default="scope">
                        <el-button type="primary" link :icon="Edit" @click="openDialog('edit', scope.row)">{{ t('admin.common.edit') }}</el-button>
                        <el-button type="primary" link :icon="Delete" style="color: #ef4444"
                            @click="handleDelete(scope.row.id)">{{ t('admin.common.delete') }}</el-button>
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

        <!-- 新增/编辑文章弹窗 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="750px" :close-on-click-modal="false"
            destroy-on-close>
            <el-form ref="articleFormRef" :model="articleForm" :rules="articleRules" label-width="100px">
                <el-form-item :label="t('admin.article.articleTitle')" prop="title">
                    <el-input v-model="articleForm.title" :placeholder="t('admin.article.titlePlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.article.category')" prop="categoryId">
                    <el-select v-model="articleForm.categoryId" :placeholder="t('admin.article.categoryRequired')" style="width: 100%">
                        <el-option v-for="c in categoryList" :key="c.id" :label="c.name" :value="c.id"></el-option>
                    </el-select>
                </el-form-item>
                <!-- 标签多选 -->
                <el-form-item :label="t('admin.article.tags')">
                    <el-select v-model="articleForm.tagIdList" multiple :placeholder="t('admin.article.tagsPlaceholder')" style="width:100%">
                        <el-option v-for="t in allTagList" :key="t.id" :label="t.name" :value="t.id"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('admin.article.summary')" prop="summary">
                    <el-input v-model="articleForm.summary" type="textarea" :rows="2"
                        :placeholder="t('admin.article.summaryPlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.article.cover')">
                    <div v-if="articleForm.cover" style="margin-bottom:10px">
                        <img :src="articleForm.cover"
                            style="width:180px;height:110px;object-fit:cover;border-radius:6px;">
                    </div>
                    <el-upload action="" :http-request="uploadFile" :on-success="handleUploadSuccess"
                        accept="image/jpeg,image/png,image/webp" :show-file-list="false">
                        <el-button :icon="Upload">{{ t('admin.article.uploadCover') }}</el-button>
                    </el-upload>
                    <div style="color:#999;font-size:12px;margin-top:4px">{{ t('admin.article.coverEmptyTip') }}</div>
                </el-form-item>
                <el-form-item :label="t('admin.article.isTop')">
                    <el-radio-group v-model="articleForm.isTop">
                        <el-radio :value="1">{{ t('admin.article.top') }}</el-radio>
                        <el-radio :value="0">{{ t('admin.article.notTop') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('admin.article.publishStatus')" prop="status">
                    <el-radio-group v-model="articleForm.status">
                        <el-radio :value="1">{{ t('admin.article.draft') }}</el-radio>
                        <el-radio :value="2">{{ t('admin.article.published') }}</el-radio>
                        <el-radio :value="3">{{ t('admin.article.private') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('admin.article.mdContent')" prop="contentMd">
                    <MdEditor v-model="articleForm.contentMd" style="height: 500px;" :theme="'light'" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, Upload } from '@element-plus/icons-vue'
import { defineAsyncComponent } from 'vue'
import 'md-editor-v3/lib/style.css'

// md-editor-v3 体积大（~300KB gzip），只在打开弹窗时再加载
const MdEditor = defineAsyncComponent(() => import('md-editor-v3').then(m => m.MdEditor))
import {
    getArticleList,
    addArticle,
    updateArticle,
    deleteArticle,
    batchDeleteArticle,
    type Article,
} from '~/api/article'
import { getCategoryAll, type Category } from '~/api/category'
import { getTagAll, type Tag } from '~/api/tag'
import { request } from '~/api/request'

const { t } = useI18n()

// 搜索表单
const searchForm = reactive({ title: '', categoryId: null as number | null, status: null as number | null })
const articleList = ref<Article[]>([])
const categoryList = ref<Category[]>([])
const allTagList = ref<Tag[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref<number[]>([])

// 弹窗
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const dialogTitle = ref(t('admin.article.addTitle'))
const submitLoading = ref(false)

const defaultArticleForm = (): Article => ({
    id: undefined,
    userId: undefined,
    title: '',
    categoryId: undefined,
    summary: '',
    cover: '',
    contentMd: '',
    contentHtml: '',
    isTop: 0,
    status: 1,
    tagIdList: [],
})

const articleForm = reactive<Article>(defaultArticleForm())
const articleFormRef = ref()

const articleRules = {
    title: [{ required: true, message: t('admin.article.titleRequired'), trigger: 'blur' }],
    categoryId: [{ required: true, message: t('admin.article.categoryRequired'), trigger: 'change' }],
    contentMd: [{ required: true, message: t('admin.article.contentRequired'), trigger: 'blur' }],
}

const formatDate = (date?: string) => {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 加载全部分类（下拉框）
const loadCategory = async () => {
    const res = await getCategoryAll()
    categoryList.value = res.data || []
}

// 加载全部标签（标签多选下拉）
const loadAllTag = async () => {
    const res = await getTagAll()
    allTagList.value = res.data || []
}

// 查询文章列表
const fetchArticleList = async () => {
    loading.value = true
    try {
        const params = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }
        const res = await getArticleList(params)
        articleList.value = res.data.records
        total.value = res.data.total
    } catch {
        ElMessage.error(t('admin.article.fetchFail'))
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    pageNum.value = 1
    fetchArticleList()
}

const handleReset = () => {
    searchForm.title = ''
    searchForm.categoryId = null
    searchForm.status = null
    pageNum.value = 1
    fetchArticleList()
}

const handleSizeChange = (size: number) => {
    pageSize.value = size
    fetchArticleList()
}

const handleCurrentChange = (page: number) => {
    pageNum.value = page
    fetchArticleList()
}

const handleSelectionChange = (selection: Article[]) => {
    selectedIds.value = selection.map(item => item.id!).filter(id => id !== undefined)
}

// 封面图上传
const uploadFile = async (param: { file: File; onSuccess: (res: any) => void; onError: (err: any) => void }) => {
    const file = param.file
    const limit = 2 * 1024 * 1024
    if (file.size > limit) {
        ElMessage.error(t('admin.article.coverSizeLimit'))
        param.onError(new Error('超出大小'))
        return
    }
    const formData = new FormData()
    formData.append('file', file)
    try {
        const res: any = await request({
            method: 'POST',
            url: '/upload/cover',
            data: formData,
            headers: { 'Content-Type': 'multipart/form-data' },
        })
        param.onSuccess(res)
    } catch {
        ElMessage.error(t('admin.article.uploadFail'))
        param.onError(new Error('upload failed'))
    }
}

const handleUploadSuccess = (res: any) => {
    if (res.code === 200) {
        articleForm.cover = res.data
        ElMessage.success(t('admin.article.coverUploadSuccess'))
    } else {
        ElMessage.error(res.msg)
    }
}

// 弹窗打开
const openDialog = (type: 'add' | 'edit', row: Article | null = null) => {
    dialogType.value = type
    dialogTitle.value = type === 'add' ? t('admin.article.addTitle') : t('admin.article.editTitle')
    dialogVisible.value = true
    nextTick(() => {
        articleFormRef.value?.resetFields()
        if (type === 'add') {
            Object.assign(articleForm, defaultArticleForm())
        } else if (row) {
            Object.assign(articleForm, row)
        }
    })
}

const handleSubmit = async () => {
    const valid = await articleFormRef.value?.validate().catch(() => false)
    if (!valid) return
    submitLoading.value = true
    try {
        const api = dialogType.value === 'add' ? addArticle : updateArticle
        await api(articleForm)
        ElMessage.success(t('admin.common.success'))
        dialogVisible.value = false
        fetchArticleList()
    } catch (err) {
        console.error(err)
        ElMessage.error(t('admin.common.fail'))
    } finally {
        submitLoading.value = false
    }
}

// 单条删除
const handleDelete = (id: number) => {
    ElMessageBox.confirm(t('admin.article.deleteConfirm'), t('admin.common.prompt'), { type: 'warning' })
        .then(() => deleteArticle(id))
        .then(() => {
            ElMessage.success(t('admin.common.deleteSuccess'))
            fetchArticleList()
        })
        .catch(() => { })
}

// 批量删除
const handleBatchDelete = () => {
    ElMessageBox.confirm(t('admin.article.batchDeleteConfirm', { count: selectedIds.value.length }), t('admin.common.prompt'), { type: 'warning' })
        .then(() => batchDeleteArticle(selectedIds.value))
        .then(() => {
            ElMessage.success(t('admin.common.batchDeleteSuccess'))
            fetchArticleList()
        })
        .catch(() => { })
}

onMounted(() => {
    fetchArticleList()
    loadCategory()
    loadAllTag()
})
</script>

<style scoped>
.article-management {
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

.search-card>*,
.table-card>* {
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

/* 暗黑模式适配 */
body.dark-mode .page-title {
    color: var(--color-dark-text);
}

body.dark-mode .search-card,
body.dark-mode .table-card {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

/* 阻止 md-editor-v3 加载远程中文字体（节省 ~600KB） */
:deep(.md-editor-icon) {
    font-family: system-ui, -apple-system, sans-serif !important;
}
:deep(.md-editor-input),
:deep(.md-editor-preview-wrapper) {
    font-family: system-ui, -apple-system, sans-serif !important;
}
</style>
