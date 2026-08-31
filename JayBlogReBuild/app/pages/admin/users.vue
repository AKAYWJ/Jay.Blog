<template>
    <div class="user-management">
        <h1 class="page-title">{{ t('admin.users.title') }}</h1>

        <div class="search-card">
            <el-form :model="searchForm" inline @submit.prevent="handleSearch">
                <el-form-item :label="t('admin.users.name')">
                    <el-input v-model="searchForm.name" :placeholder="t('admin.users.namePlaceholder')" clearable style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.users.phone')">
                    <el-input v-model="searchForm.phone" :placeholder="t('admin.users.phonePlaceholder')" clearable style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.users.role')">
                    <el-select v-model="searchForm.roleId" :placeholder="t('admin.users.rolePlaceholder')" clearable style="width: 150px">
                        <el-option :label="t('admin.users.admin')" :value="1"></el-option>
                        <el-option :label="t('admin.users.normalUser')" :value="2"></el-option>
                        <el-option :label="t('admin.users.guest')" :value="3"></el-option>
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
                <el-button type="primary" :icon="Plus" @click="openDialog('add')">{{ t('admin.users.addTitle') }}</el-button>
                <el-button type="danger" :icon="Delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">{{ t('admin.common.batchDelete') }}</el-button>
            </div>

            <el-table :data="userList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" :label="t('admin.common.id')" width="80" align="center"></el-table-column>
                <el-table-column prop="no" :label="t('admin.users.account')" min-width="160" align="center"></el-table-column>
                <el-table-column prop="name" :label="t('admin.users.name')" min-width="140" align="center"></el-table-column>
                <el-table-column prop="age" :label="t('admin.users.age')" width="80" align="center"></el-table-column>
                <el-table-column prop="sex" :label="t('admin.users.sex')" width="80" align="center">
                    <template #default="scope">
                        <span>{{ formatSex(scope.row.sex) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="phone" :label="t('admin.users.phone')" width="150" align="center"></el-table-column>
                <el-table-column prop="roleId" :label="t('admin.users.role')" width="100" align="center">
                    <template #default="scope">
                        <el-tag :type="getRoleTagType(scope.row.roleId)" size="small">
                            {{ formatRole(scope.row.roleId) }}
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
            <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="80px">
                <el-form-item :label="t('admin.users.name')" prop="name">
                    <el-input v-model="userForm.name" :placeholder="t('admin.users.namePlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.users.password')" prop="password">
                    <el-input v-model="userForm.password" type="password"
                        :placeholder="dialogType === 'add' ? t('admin.users.passwordPlaceholder') : t('admin.users.passwordEditPlaceholder')" show-password></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.users.phone')" prop="phone">
                    <el-input v-model="userForm.phone" :placeholder="t('admin.users.phonePlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.users.age')" prop="age">
                    <el-input v-model.number="userForm.age" type="number" :placeholder="t('admin.users.agePlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.users.sex')" prop="sex">
                    <el-select v-model="userForm.sex" :placeholder="t('admin.users.sexPlaceholder')" style="width: 100%">
                        <el-option :label="t('admin.users.male')" :value="1"></el-option>
                        <el-option :label="t('admin.users.female')" :value="2"></el-option>
                        <el-option :label="t('admin.users.secret')" :value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('admin.users.role')" prop="roleId">
                    <el-select v-model="userForm.roleId" :placeholder="t('admin.users.rolePlaceholder')" style="width: 100%">
                        <el-option :label="t('admin.users.superAdmin')" :value="1"></el-option>
                        <el-option :label="t('admin.users.admin')" :value="2"></el-option>
                        <el-option :label="t('admin.users.normalUser')" :value="3"></el-option>
                    </el-select>
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
import { ref, reactive, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit } from '@element-plus/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, deleteBatchUsers } from '~/api/user'
import type { User } from '~/api/user'

const { t } = useI18n()

const searchForm = reactive<{ name: string; phone: string; roleId: number | null }>({
    name: '',
    phone: '',
    roleId: null
})
const userList = ref<User[]>([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const dialogTitle = ref(t('admin.users.addTitle'))
const submitLoading = ref(false)
const userFormRef = ref()
const userForm = reactive<User>({
    id: null,
    name: '',
    password: '',
    phone: '',
    age: null,
    sex: null,
    roleId: null
})

function validatePassword(_rule: any, value: string, callback: (err?: Error) => void) {
    if (dialogType.value === 'add' && !value) {
        callback(new Error(t('admin.users.passwordRequired')))
    } else {
        callback()
    }
}

const userRules = {
    name: [{ required: true, message: t('admin.users.nameRequired'), trigger: 'blur' }],
    password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
    phone: [
        { required: true, pattern: /^1[3-9]\d{9}$/, message: t('admin.users.phoneInvalid'), trigger: 'blur' }
    ],
    age: [
        { pattern: /^\d{1,3}$/, message: t('admin.users.ageInvalid'), trigger: 'blur' }
    ]
}

function fetchUserList() {
    loading.value = true
    const params: any = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        ...searchForm
    }
    if (params.roleId === null) delete params.roleId

    getUserList(params)
        .then((res) => {
            userList.value = res.data.records
            total.value = res.data.total
        })
        .catch(() => {
            ElMessage.error(t('admin.users.fetchFail'))
        })
        .finally(() => {
            loading.value = false
        })
}

function handleSearch() {
    pageNum.value = 1
    fetchUserList()
}

function handleReset() {
    searchForm.name = ''
    searchForm.phone = ''
    searchForm.roleId = null
    pageNum.value = 1
    fetchUserList()
}

function handleSizeChange(size: number) {
    pageSize.value = size
    fetchUserList()
}

function handleCurrentChange(page: number) {
    pageNum.value = page
    fetchUserList()
}

function handleSelectionChange(selection: User[]) {
    selectedIds.value = selection.map(item => item.id as number)
}

function openDialog(type: 'add' | 'edit', row: User | null = null) {
    dialogType.value = type
    dialogTitle.value = type === 'add' ? t('admin.users.addTitle') : t('admin.users.editTitle')
    dialogVisible.value = true

    nextTick(() => {
        userFormRef.value?.resetFields()
        if (type === 'add') {
            Object.assign(userForm, {
                id: null,
                name: '',
                password: '',
                phone: '',
                age: null,
                sex: null,
                roleId: 3
            })
        } else if (row) {
            Object.assign(userForm, row)
            userForm.password = ''
        }
    })
}

function handleSubmit() {
    userFormRef.value.validate((valid: boolean) => {
        if (!valid) return
        submitLoading.value = true
        const onSuccess = () => {
            ElMessage.success(dialogType.value === 'add' ? t('admin.users.addSuccess') : t('admin.users.editSuccess'))
            dialogVisible.value = false
            fetchUserList()
        }
        const onFail = () => {
            ElMessage.error(t('admin.users.operationFail'))
        }
        if (dialogType.value === 'add') {
            addUser(userForm).then(onSuccess).catch(onFail).finally(() => {
                submitLoading.value = false
            })
        } else {
            const updateData: User = { ...userForm }
            if (!updateData.password) {
                delete updateData.password
            }
            updateUser(updateData).then(onSuccess).catch(onFail).finally(() => {
                submitLoading.value = false
            })
        }
    })
}

function handleDelete(id: number) {
    ElMessageBox.confirm(t('admin.users.deleteConfirm'), t('admin.common.prompt'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
    }).then(() => {
        deleteUser(id).then(() => {
            ElMessage.success(t('admin.common.deleteSuccess'))
            fetchUserList()
        })
    }).catch(() => { /* cancel */ })
}

function handleBatchDelete() {
    ElMessageBox.confirm(t('admin.users.batchDeleteConfirm', { count: selectedIds.value.length }), t('admin.common.prompt'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning'
    }).then(() => {
        deleteBatchUsers(selectedIds.value).then(() => {
            ElMessage.success(t('admin.common.batchDeleteSuccess'))
            fetchUserList()
        })
    }).catch(() => { /* cancel */ })
}

function formatSex(sex?: number) {
    const sexMap: Record<number, string> = { 1: t('admin.users.male'), 2: t('admin.users.female'), 0: t('admin.users.secret') }
    return sexMap[sex as number] || t('admin.users.unknown')
}

function formatRole(roleId?: number) {
    const roleMap: Record<number, string> = { 1: t('admin.users.superAdmin'), 2: t('admin.users.admin'), 3: t('admin.users.normalUser') }
    return roleMap[roleId as number] || t('admin.users.unknown')
}

function getRoleTagType(roleId?: number) {
    const typeMap: Record<number, string> = { 1: 'danger', 2: 'primary', 3: 'info' }
    return typeMap[roleId as number] || 'info'
}

function formatDate(date?: string) {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
}

onMounted(() => {
    fetchUserList()
})
</script>

<style scoped>
.user-management {
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
