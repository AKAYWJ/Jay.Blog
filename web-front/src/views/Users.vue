<template>
    <div class="user-management">
        <!-- 页面标题 -->
        <h1 class="page-title">用户管理</h1>

        <!-- 搜索区域 -->
        <div class="search-card">
            <el-form :model="searchForm" inline @submit.native.prevent="handleSearch">
                <el-form-item label="用户名">
                    <el-input v-model="searchForm.name" placeholder="请输入用户名" clearable style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item label="手机号">
                    <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable style="width: 200px"></el-input>
                </el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="searchForm.roleId" placeholder="请选择角色" clearable style="width: 150px">
                        <el-option label="管理员" :value="1"></el-option>
                        <el-option label="普通用户" :value="2"></el-option>
                        <el-option label="访客" :value="3"></el-option>
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
                <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')">新增用户</el-button>
                <el-button type="danger" icon="el-icon-delete" @click="handleBatchDelete"
                    :disabled="selectedIds.length === 0">批量删除</el-button>
            </div>

            <el-table :data="userList" border stripe v-loading="loading" @selection-change="handleSelectionChange"
                style="width: 100%">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
                <el-table-column prop="no" label="账号" width="180" align="center"></el-table-column>
                <el-table-column prop="name" label="用户名" width="150" align="center"></el-table-column>
                <el-table-column prop="age" label="年龄" width="80" align="center"></el-table-column>
                <el-table-column prop="sex" label="性别" width="80" align="center">
                    <template slot-scope="scope">
                        <span>{{ formatSex(scope.row.sex) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="phone" label="手机号" width="150" align="center"></el-table-column>
                <el-table-column prop="roleId" label="角色" width="100" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="getRoleTagType(scope.row.roleId)" size="small">
                            {{ formatRole(scope.row.roleId) }}
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
            <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="80px">
                <el-form-item label="用户名" prop="name">
                    <el-input v-model="userForm.name" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="userForm.password" type="password"
                        :placeholder="dialogType === 'add' ? '请输入密码' : '留空则不修改密码'" show-password></el-input>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                    <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                    <el-input v-model.number="userForm.age" type="number" placeholder="请输入年龄"></el-input>
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                    <el-select v-model="userForm.sex" placeholder="请选择性别" style="width: 100%">
                        <el-option label="男" :value="1"></el-option>
                        <el-option label="女" :value="2"></el-option>
                        <el-option label="保密" :value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="角色" prop="roleId">
                    <el-select v-model="userForm.roleId" placeholder="请选择角色" style="width: 100%">
                        <el-option label="超级管理员" :value="1"></el-option>
                        <el-option label="管理员" :value="2"></el-option>
                        <el-option label="普通用户" :value="3"></el-option>
                    </el-select>
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
import { getUserList, register, updateUser, deleteUser, deleteBatchUsers } from '@/api/user'

export default {
    name: 'User',
    data() {
        return {
            // 搜索表单
            searchForm: {
                name: '',
                phone: '',
                roleId: null
            },
            // 表格数据
            userList: [],
            loading: false,
            // 分页参数
            pageNum: 1,
            pageSize: 10,
            total: 0,
            // 批量选中的ID
            selectedIds: [],
            // 弹窗相关
            dialogVisible: false,
            dialogType: 'add', // add/edit
            dialogTitle: '新增用户',
            submitLoading: false,
            // 用户表单
            userForm: {
                id: null,
                name: '',
                password: '',
                phone: '',
                age: null,
                sex: null,
                roleId: null
            },
            // 表单校验规则
            userRules: {
                name: [
                    { required: true, message: '用户名不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur', validator: null }
                ],
                phone: [
                    { required: true,pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
                ],
                age: [
                    { pattern: /^\d{1,3}$/, message: '请输入合法年龄', trigger: 'blur' }
                ]
            }
        }
    },
    created() {
        // 绑定密码校验函数的this指向
        this.userRules.password[0].validator = this.validatePassword
    },
    mounted() {
        this.getUserList()
    },
    methods: {
        // ====================== 数据查询（完全保留你的.then写法，添加错误处理） ======================
        async getUserList() {
            this.loading = true
            const params = {
                pageNum: this.pageNum,
                pageSize: this.pageSize,
                ...this.searchForm
            }

            getUserList(params)
                .then((res) => {
                    this.userList = res.data.records
                    this.total = res.data.total
                })
                .catch((e) => {
                    console.error('获取用户列表失败：', e)
                    this.$message.error('获取用户列表失败')
                })
                .finally(() => {
                    this.loading = false
                })
        },

        // 搜索
        handleSearch() {
            this.pageNum = 1
            this.getUserList()
        },

        // 重置搜索
        handleReset() {
            this.searchForm = {
                name: '',
                phone: '',
                roleId: null
            }
            this.pageNum = 1
            this.getUserList()
        },

        // 分页变化
        handleSizeChange(size) {
            this.pageSize = size
            this.getUserList()
        },

        handleCurrentChange(page) {
            this.pageNum = page
            this.getUserList()
        },

        // 多选变化
        handleSelectionChange(selection) {
            this.selectedIds = selection.map(item => item.id)
        },

        // ====================== 弹窗操作（已修复resetFields错误） ======================
        openDialog(type, row = null) {
            this.dialogType = type
            this.dialogTitle = type === 'add' ? '新增用户' : '编辑用户'

            // 先显示弹窗，让DOM渲染
            this.dialogVisible = true

            // 等DOM更新完成后再操作表单
            this.$nextTick(() => {
                if (this.$refs.userFormRef) {
                    this.$refs.userFormRef.resetFields()
                }

                if (type === 'add') {
                    this.userForm = {
                        id: null,
                        name: '',
                        password: '',
                        phone: '',
                        age: null,
                        sex: null,
                        roleId: 3 // 默认访客角色
                    }
                } else {
                    this.userForm = { ...row }
                    this.userForm.password = ''
                }
            })
        },

        // 密码校验：新增时必填，编辑时可选
        validatePassword(rule, value, callback) {
            if (this.dialogType === 'add' && !value) {
                callback(new Error('密码不能为空'))
            } else {
                callback()
            }
        },

        // 提交表单
        async handleSubmit() {
            this.$refs.userFormRef.validate(async valid => {
                if (!valid) return

                this.submitLoading = true
                try {
                    if (this.dialogType === 'add') {
                        await register(this.userForm)
                        this.$message.success('新增用户成功')
                    } else {
                        const updateData = { ...this.userForm }
                        if (!updateData.password) {
                            delete updateData.password
                        }
                        await updateUser(updateData)
                        this.$message.success('修改用户成功')
                    }

                    this.dialogVisible = false
                    this.getUserList()
                } catch (e) {
                    console.error('提交失败：', e)
                    this.$message.error('操作失败，请稍后重试')
                } finally {
                    this.submitLoading = false
                }
            })
        },

        // ====================== 删除操作 ======================
        async handleDelete(id) {
            try {
                await this.$confirm('确定要删除该用户吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).catch(()=>{ /*取消，空函数即可*/ })

                await deleteUser(id)
                this.$message.success('删除成功')
                this.getUserList()
            } catch (e) {
                // 取消删除
            }
        },

        async handleBatchDelete() {
            try {
                await this.$confirm(`确定要删除选中的 ${this.selectedIds.length} 个用户吗？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).catch(()=>{ /*取消，空函数即可*/ })

                await deleteBatchUsers(this.selectedIds)
                this.$message.success('批量删除成功')
                this.getUserList()
            } catch (e) {
                // 取消删除
            }
        },

        // ====================== 格式化工具 ======================
        formatSex(sex) {
            const sexMap = { 1: '男', 2: '女', 0: '保密' }
            return sexMap[sex] || '未知'
        },

        formatRole(roleId) {
            const roleMap = { 1: '超级管理员', 2: '管理员', 3: '普通用户' }
            return roleMap[roleId] || '未知'
        },

        getRoleTagType(roleId) {
            const typeMap = { 1: 'danger', 2: 'primary', 3: 'info' }
            return typeMap[roleId] || 'info'
        },

        formatDate(date) {
            if (!date) return '-'
            const d = new Date(date)
            return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
        }
    }
}
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