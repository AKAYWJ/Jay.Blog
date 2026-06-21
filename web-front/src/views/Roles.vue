<template>
    <div class="role-management">
        <!-- 页面标题 -->
        <h1 class="page-title">角色管理</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.native.prevent="handleSearch">
                <el-form-item label="角色名称">
                    <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item label="角色描述">
                    <el-input v-model="searchForm.description" placeholder="请输入角色描述" clearable
                        style="width: 200px"></el-input>
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
                <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')">新增角色</el-button>
                <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">批量删除</el-button>
            </div>

            <el-table :data="roleList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
                <el-table-column prop="roleName" label="角色名称" width="150" align="center"></el-table-column>
                <el-table-column prop="description" label="角色描述" width="200" align="center"></el-table-column>
                <el-table-column label="绑定权限" width="120" align="center">
                    <template slot-scope="scope">
                        <el-tag type="success" size="small" @click="openPermDialog(scope.row)">
                            分配权限
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" width="180" align="center">
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
            <el-form ref="roleFormRef" :model="roleForm" :rules="roleRules" label-width="80px">
                <el-form-item label="角色名称" prop="roleName">
                    <el-input v-model="roleForm.roleName" placeholder="请输入角色名称"></el-input>
                </el-form-item>
                <el-form-item label="角色描述" prop="description">
                    <el-input v-model="roleForm.description" type="text" placeholder="请输入角色描述"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
            </span>
        </el-dialog>

        <!-- 权限分配弹窗 -->
        <el-dialog title="分配权限" :visible.sync="permDialogVisible" width="600px">
            <!-- 🔥 核心：check-strictly="true" 关闭父子联动，解决自动全选！ -->
            <el-tree :data="permTreeData" show-checkbox node-key="id" ref="permTree" :props="defaultProps"
                check-strictly="true" @check-change="handleCheckChange"></el-tree>
            <div slot="footer" class="dialog-footer">
                <el-button @click="permDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveRolePerm">保存权限</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { getRoleList, addRole, updateRole, deleteRole, deleteBatchRoles, getPermTree, assignRolePerm, getRolePermIds } from '@/api/role'

export default {
    name: 'Roles',
    data() {
        return {
            searchForm: { roleName: '', description: '' },
            roleList: [], loading: false,
            pageNum: 1, pageSize: 10, total: 0,
            selectedIds: [],
            dialogVisible: false, dialogType: 'add', dialogTitle: '新增角色', submitLoading: false,
            roleForm: { id: null, roleName: '', description: '',roleCode:"user" },
            roleRules: { roleName: [{ required: true, message: '角色名称不能为空', trigger: 'blur' }] },
            // 权限弹窗（所有变量完整定义）
            permDialogVisible: false,
            currentRoleId: null,
            permTreeData: [],
            checkedKeys: [], // ✅ 修复：已定义
            defaultProps: { children: 'children', label: 'name' }
        }
    },
    mounted() {
        this.getRoleList()
        this.getPermTreeData()
    },
    methods: {
        // ====================== 数据查询 ======================
        getRoleList() {
            this.loading = true
            const params = { pageNum: this.pageNum, pageSize: this.pageSize, ...this.searchForm }
            getRoleList(params).then(res => {
                this.roleList = res.data.records
                this.total = res.data.total
            }).catch(() => {
                this.$message.error('获取角色列表失败')
            }).finally(() => {
                this.loading = false
            })
        },
        getPermTreeData() {
            return getPermTree().then(res => {
                this.permTreeData = res.data
            })
        },
        handleSearch() { this.pageNum = 1; this.getRoleList() },
        handleReset() { this.searchForm = { roleName: '', description: '' }; this.pageNum = 1; this.getRoleList() },
        handleSizeChange(size) { this.pageSize = size; this.getRoleList() },
        handleCurrentChange(page) { this.pageNum = page; this.getRoleList() },
        handleSelectionChange(selection) { this.selectedIds = selection.map(item => item.id) },

        // ====================== 弹窗操作 ======================
        openDialog(type, row = null) {
            this.dialogType = type
            this.dialogTitle = type === 'add' ? '新增角色' : '编辑角色'
            this.dialogVisible = true
            this.$nextTick(() => {
                this.$refs.roleFormRef?.resetFields()
                this.roleForm = type === 'add' ? { id: null, roleName: '', description: '',roleCode:'user' } : { ...row }
            })
        },
        handleSubmit() {
            this.$refs.roleFormRef.validate(valid => {
                if (!valid) return
                this.submitLoading = true
                const api = this.dialogType === 'add' ? addRole : updateRole
                api(this.roleForm).then(() => {
                    this.$message.success('操作成功')
                    this.dialogVisible = false
                    this.getRoleList()
                }).catch(() => {
                    this.$message.error('操作失败')
                }).finally(() => {
                    this.submitLoading = false
                })
            })
        },

        // ====================== 🔥 权限弹窗（终极修复） ======================
        async openPermDialog(row) {
            this.currentRoleId = row.id
            this.permDialogVisible = true
            // 1. 加载权限树
            await this.getPermTreeData()
            // 2. 获取已分配权限
            getRolePermIds(row.id).then(res => {
                this.checkedKeys = res.data || []
                // 3. 回显勾选（核心）
                this.$nextTick(() => {
                    this.$refs.permTree.setCheckedKeys(this.checkedKeys)
                })
            })
        },
        // ✅ 修复：方法已定义
        handleCheckChange() {
            this.checkedKeys = this.$refs.permTree.getCheckedKeys()
        },
        // 保存权限
        saveRolePerm() {
            const params = {
                roleId: this.currentRoleId,
                permissionIds: this.$refs.permTree.getCheckedKeys()
            }
            assignRolePerm(params).then(() => {
                this.$message.success('权限分配成功')
                this.permDialogVisible = false
            })
        },

        // ====================== 删除操作 ======================
        handleDelete(id) {
            this.$confirm('确定删除该角色？', '提示', { type: 'warning' }).then(() => {
                deleteRole(id).then(() => {
                    this.$message.success('删除成功')
                    this.getRoleList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },
        handleBatchDelete() {
            this.$confirm(`确定删除选中的 ${this.selectedIds.length} 个角色？`, '提示', { type: 'warning' }).then(() => {
                deleteBatchRoles(this.selectedIds).then(() => {
                    this.$message.success('批量删除成功')
                    this.getRoleList()
                })
            }).catch(()=>{ /*取消，空函数即可*/ })
        },

        // ====================== 格式化工具 ======================
        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, 0)}-${String(d.getDate()).padStart(2, 0)} ${String(d.getHours()).padStart(2, 0)}:${String(d.getMinutes()).padStart(2, 0)}`
        }
    }
}
</script>

<!-- 样式使用全局变量，与 Users 页面统一 -->
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

/* Element-UI 组件通用覆盖已收敛到 src/styles/element-override.css */

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
</style>