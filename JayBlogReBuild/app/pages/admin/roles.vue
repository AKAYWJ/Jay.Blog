<template>
    <div class="role-management">
        <h1 class="page-title">{{ t('admin.roles.title') }}</h1>

        <div class="search-card">
            <el-form :model="searchForm" inline @submit.prevent="handleSearch">
                <el-form-item :label="t('admin.roles.name')">
                    <el-input v-model="searchForm.roleName" :placeholder="t('admin.roles.namePlaceholder')" clearable style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.roles.description')">
                    <el-input v-model="searchForm.description" :placeholder="t('admin.roles.descriptionPlaceholder')" clearable
                        style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">{{ t('admin.common.search') }}</el-button>
                    <el-button @click="handleReset">{{ t('admin.common.reset') }}</el-button>
                </el-form-item>
            </el-form>
        </div>

        <div class="table-card">
            <div class="table-header">
                <el-button type="primary" @click="openDialog('add')">{{ t('admin.roles.addTitle') }}</el-button>
                <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">{{ t('admin.common.batchDelete') }}</el-button>
            </div>

            <el-table :data="roleList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" :label="t('admin.common.id')" width="80" align="center"></el-table-column>
                <el-table-column prop="roleName" :label="t('admin.roles.name')" width="150" align="center"></el-table-column>
                <el-table-column prop="description" :label="t('admin.roles.description')" width="200" align="center"></el-table-column>
                <el-table-column :label="t('admin.roles.permColumn')" width="120" align="center">
                    <template #default="scope">
                        <el-tag type="success" size="small" @click="openPermDialog(scope.row)">
                            {{ t('admin.roles.assignPerm') }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" :label="t('admin.common.createTime')" width="180" align="center">
                    <template #default="scope">
                        {{ formatDate(scope.row.createTime) }}
                    </template>
                </el-table-column>
                <el-table-column :label="t('admin.common.actions')" width="180" align="center">
                    <template #default="scope">
                        <el-button type="text" @click="openDialog('edit', scope.row)">{{ t('admin.common.edit') }}</el-button>
                        <el-button type="text" style="color: #ef4444" @click="handleDelete(scope.row.id)">{{ t('admin.common.delete') }}</el-button>
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
            <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="80px">
                <el-form-item :label="t('admin.roles.name')" prop="roleName">
                    <el-input v-model="roleForm.roleName" :placeholder="t('admin.roles.namePlaceholder')"></el-input>
                </el-form-item>
                <el-form-item :label="t('admin.roles.description')" prop="description">
                    <el-input v-model="roleForm.description" type="text" :placeholder="t('admin.roles.descriptionPlaceholder')"></el-input>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
                    <el-button type="primary" :loading="submitLoading" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>

        <el-dialog v-model="permDialogVisible" :title="t('admin.roles.permDialogTitle')" width="600px">
            <el-tree :data="permTreeData" show-checkbox node-key="id" ref="permTree" :props="defaultProps"
                check-strictly @check="handleCheckChange"></el-tree>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="permDialogVisible = false">{{ t('common.cancel') }}</el-button>
                    <el-button type="primary" @click="saveRolePerm">{{ t('admin.roles.savePerm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
    getRoleList,
    addRole,
    updateRole,
    deleteRole,
    deleteBatchRoles,
    getPermTree,
    assignRolePerm,
    getRolePermIds
} from '~/api/role'

const { t } = useI18n()

const searchForm = reactive({ roleName: '', description: '' })
const roleList = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedIds = ref([])

const dialogVisible = ref(false)
const dialogType = ref('add')
const dialogTitle = ref(t('admin.roles.addTitle'))
const submitLoading = ref(false)
const roleForm = reactive({ id: null, roleName: '', description: '', roleCode: 'user' })
const roleFormRef = ref()
const roleRules = {
    roleName: [{ required: true, message: t('admin.roles.nameRequired'), trigger: 'blur' }]
}

const permDialogVisible = ref(false)
const currentRoleId = ref(null)
const permTreeData = ref([])
const checkedKeys = ref([])
const permTree = ref()
const defaultProps = { children: 'children', label: 'name' }

const loadRoleList = () => {
    loading.value = true
    const params = { pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm }
    getRoleList(params).then(res => {
        roleList.value = res.data.records
        total.value = res.data.total
    }).catch(() => {
        ElMessage.error(t('admin.roles.fetchFail'))
    }).finally(() => {
        loading.value = false
    })
}

const loadPermTree = () => {
    return getPermTree().then(res => {
        permTreeData.value = res.data
    })
}

onMounted(() => {
    loadRoleList()
    loadPermTree()
})

const handleSearch = () => { pageNum.value = 1; loadRoleList() }
const handleReset = () => {
    searchForm.roleName = ''
    searchForm.description = ''
    pageNum.value = 1
    loadRoleList()
}
const handleSizeChange = (size) => { pageSize.value = size; loadRoleList() }
const handleCurrentChange = (page) => { pageNum.value = page; loadRoleList() }
const handleSelectionChange = (selection) => {
    selectedIds.value = selection.map(item => item.id)
}

const openDialog = (type, row = null) => {
    dialogType.value = type
    dialogTitle.value = type === 'add' ? t('admin.roles.addTitle') : t('admin.roles.editTitle')
    dialogVisible.value = true
    nextTick(() => {
        roleFormRef.value?.resetFields()
        if (type === 'add') {
            roleForm.id = null
            roleForm.roleName = ''
            roleForm.description = ''
            roleForm.roleCode = 'user'
        } else if (row) {
            roleForm.id = row.id ?? null
            roleForm.roleName = row.roleName ?? ''
            roleForm.description = row.description ?? ''
            roleForm.roleCode = row.roleCode ?? 'user'
        }
    })
}

const handleSubmit = () => {
    roleFormRef.value.validate(valid => {
        if (!valid) return
        submitLoading.value = true
        const api = dialogType.value === 'add' ? addRole : updateRole
        api(roleForm).then(() => {
            ElMessage.success(t('admin.common.success'))
            dialogVisible.value = false
            loadRoleList()
        }).catch(() => {
            ElMessage.error(t('admin.common.fail'))
        }).finally(() => {
            submitLoading.value = false
        })
    })
}

const openPermDialog = async (row) => {
    currentRoleId.value = row.id
    permDialogVisible.value = true
    await loadPermTree()
    getRolePermIds(row.id).then(res => {
        checkedKeys.value = res.data || []
        nextTick(() => {
            permTree.value?.setCheckedKeys(checkedKeys.value)
        })
    })
}

const handleCheckChange = () => {
    checkedKeys.value = permTree.value?.getCheckedKeys() || []
}

const saveRolePerm = () => {
    const params = {
        roleId: currentRoleId.value,
        permissionIds: permTree.value?.getCheckedKeys() || []
    }
    assignRolePerm(params).then(() => {
        ElMessage.success(t('admin.roles.permAssignSuccess'))
        permDialogVisible.value = false
    })
}

const handleDelete = (id) => {
    ElMessageBox.confirm(t('admin.roles.deleteConfirm'), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        deleteRole(id).then(() => {
            ElMessage.success(t('admin.common.deleteSuccess'))
            loadRoleList()
        })
    }).catch(() => {})
}

const handleBatchDelete = () => {
    ElMessageBox.confirm(t('admin.roles.batchDeleteConfirm', { count: selectedIds.value.length }), t('admin.common.prompt'), { type: 'warning' }).then(() => {
        deleteBatchRoles(selectedIds.value).then(() => {
            ElMessage.success(t('admin.common.batchDeleteSuccess'))
            loadRoleList()
        })
    }).catch(() => {})
}

const formatDate = (date) => {
    if (!date) return '-'
    const d = new Date(date)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}
</script>

<style scoped>
.role-management {
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
