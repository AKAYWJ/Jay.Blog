<template>
    <div class="system-management">
        <!-- 页面标题 -->
        <h1 class="page-title">系统设置</h1>

        <div class="card-wrap">
            <!-- 卡片1：个人信息修改 -->
            <div class="info-card">
                <div class="card-header">
                    <h3>个人信息修改</h3>
                </div>
                <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px" class="info-form">
                    <!-- 头像上传 复用文章上传写法 -->
                    <el-form-item label="头像">
                        <div v-if="userForm.avatar" style="margin-bottom:10px">
                            <img :src="userForm.avatar"
                                style="width:100px;height:100px;object-fit:cover;border-radius:8px;">
                        </div>
                        <el-upload action="" :http-request="uploadAvatar" :on-success="handleAvatarSuccess"
                            accept="image/jpeg,image/png,image/webp">
                            <el-button icon="el-icon-upload">点击上传头像</el-button>
                        </el-upload>
                        <div class="tip-text">图片大小不超过2MB，不上传则保留原有头像</div>
                    </el-form-item>

                    <el-form-item label="用户ID" prop="id">
                        <el-input v-model="userForm.id" disabled placeholder="系统自动分配" />
                    </el-form-item>
                    <el-form-item label="用户名" prop="name">
                        <el-input v-model="userForm.name" placeholder="请输入用户名" />
                    </el-form-item>
                    <!-- 新增邮箱 -->
                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="userForm.email" placeholder="请输入电子邮箱" />
                    </el-form-item>
                    <el-form-item label="手机号" prop="phone">
                        <el-input v-model="userForm.phone" disabled placeholder="请输入手机号" />
                    </el-form-item>
                    <el-form-item label="年龄" prop="age">
                        <el-input v-model.number="userForm.age" type="number" placeholder="请输入年龄" />
                    </el-form-item>
                    <el-form-item label="性别" prop="sex">
                        <el-select v-model="userForm.sex" placeholder="请选择性别" style="width: 100%">
                            <el-option label="男" :value="1"></el-option>
                            <el-option label="女" :value="2"></el-option>
                            <el-option label="保密" :value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="重置密码" prop="password">
                        <el-input v-model="userForm.password" type="password" show-password placeholder="留空则不修改密码" />
                    </el-form-item>
                    <el-form-item class="btn-box">
                        <el-button type="primary" :loading="userLoading" @click="handleUserSubmit">保存修改</el-button>
                        <el-button @click="resetUserForm">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>

            <!-- 卡片2：博客站点配置 -->
            <div class="info-card">
                <div class="card-header">
                    <h3>博客站点配置</h3>
                </div>
                <el-form ref="configFormRef" :model="configForm" :rules="configRules" label-width="100px"
                    class="info-form">
                    <el-form-item label="站点简介" prop="aboutMe">
                        <el-input v-model="configForm.aboutMe" type="textarea" :rows="4" placeholder="填写博客介绍文案" />
                    </el-form-item>
                    <el-form-item label="联系QQ" prop="qq">
                        <el-input v-model="configForm.qq" placeholder="输入QQ号码" />
                    </el-form-item>
                    <el-form-item label="Github地址" prop="github">
                        <el-input v-model="configForm.github" placeholder="仓库链接" />
                    </el-form-item>
                    <el-form-item class="btn-box">
                        <el-button type="primary" :loading="configLoading" @click="handleConfigSubmit">保存配置</el-button>
                        <el-button @click="resetConfigForm">重置</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import { updateUser } from '@/api/user'
import { getBlogConfig, updateBlogConfig } from '@/api/front'
import axios from 'axios'

export default {
    name: 'System',
    data() {
        return {
            // 个人用户表单（新增 avatar、email）
            userForm: {
                id: '',
                name: '',
                email: '',
                phone: '',
                age: null,
                sex: null,
                password: '',
                avatar: ''
            },
            userLoading: false,
            userRules: {
                name: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
                email: [
                    { pattern: /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,10})+$/, message: '请输入合法邮箱', trigger: 'blur' }
                ],
                phone: [
                    { required: true, message: '手机号不能为空', trigger: 'blur' },
                    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
                ],
                age: [{ pattern: /^\d{1,3}$/, message: '请输入合法年龄', trigger: 'blur' }]
            },

            // 博客配置表单
            configForm: {
                id: 1,
                aboutMe: '',
                qq: '',
                github: ''
            },
            configLoading: false,
            configRules: {
                aboutMe: [{ required: true, message: '站点简介不能为空', trigger: 'blur' }],
                qq: [{ pattern: /^\d{5,12}$/, message: 'QQ格式不正确', trigger: 'blur' }]
            }
        }
    },
    created() {
        this.loadUserInfo()
        this.loadBlogConfig()
    },
    methods: {
        // 自定义头像上传 与文章封面上传逻辑完全统一
        uploadAvatar(param) {
            const file = param.file
            const limit = 2 * 1024 * 1024
            if (file.size > limit) {
                this.$message.error('图片不能超过2MB')
                param.onError(new Error('超出大小'))
                return
            }
            const formData = new FormData()
            formData.append('file', file)
            // 上传接口统一使用 /api/upload/cover 和文章共用
            axios.post('/api/upload/cover', formData, {
                headers: {
                    Authorization: 'Bearer ' + localStorage.getItem('token')
                }
            }).then(res => {
                param.onSuccess(res.data)
            }).catch(err => {
                this.$message.error('上传失败，请重新登录')
                param.onError(err)
            })
        },

        // 上传成功回调赋值头像
        handleAvatarSuccess(res) {
            if (res.code === 200) {
                this.userForm.avatar = res.data
                this.$message.success('头像上传成功')
            } else {
                this.$message.error(res.msg)
            }
        },

        // ========== 个人信息相关 ==========
        loadUserInfo() {
            const userInfo = this.$store.state.userInfo
            this.userForm = {
                id: userInfo.userId || '',
                name: userInfo.username || '',
                email: userInfo.email || '',
                phone: userInfo.phone || '',
                age: userInfo.age || null,
                sex: userInfo.sex || null,
                password: '',
                avatar: userInfo.avatar || ''
            }
        },
        handleUserSubmit() {
            this.$refs.userFormRef.validate(async (valid) => {
                if (!valid) return
                this.userLoading = true
                try {
                    const params = { ...this.userForm }
                    // 密码为空则不传，后端不更新密码
                    if (!params.password) delete params.password
                    await updateUser(params)
                    // 更新Vuex全局用户信息，包含头像邮箱
                    this.$store.commit('SET_USER_INFO', {
                        userId: this.userForm.id,
                        username: this.userForm.name,
                        roleId: this.$store.state.userInfo.roleId,
                        phone: this.userForm.phone,
                        age: this.userForm.age,
                        sex: this.userForm.sex,
                        email: this.userForm.email,
                        avatar: this.userForm.avatar
                    })
                    this.$message.success('个人信息修改成功！')
                } catch (e) {
                    this.$message.error('修改失败')
                    console.error(e)
                } finally {
                    this.userLoading = false
                }
            })
        },
        resetUserForm() {
            this.$refs.userFormRef.resetFields()
            this.loadUserInfo()
        },

        // ========== 博客配置相关 ==========
        async loadBlogConfig() {
            try {
                const res = await getBlogConfig()
                this.configForm = res.data
            } catch (err) {
                console.error('加载配置失败', err)
            }
        },
        handleConfigSubmit() {
            this.$refs.configFormRef.validate(async valid => {
                if (!valid) return
                this.configLoading = true
                try {
                    await updateBlogConfig(this.configForm)
                    this.$message.success('博客配置保存成功')
                    this.$store.commit('SET_BLOG_CONFIG', this.configForm)
                } catch (err) {
                    this.$message.error('配置保存失败')
                } finally {
                    this.configLoading = false
                }
            })
        },
        resetConfigForm() {
            this.$refs.configFormRef.resetFields()
            this.loadBlogConfig()
        }
    }
}
</script>

<style scoped>
.system-management {
    padding: 0;
}

.page-title {
    font-size: var(--font-size-page-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin-bottom: var(--spacing-page);
}

.card-wrap {
    display: flex;
    flex-wrap: wrap;
    gap: 24px;
}

.info-card {
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    box-shadow: var(--liquid-glass-shadow);
    padding: var(--spacing-card-lg);
    flex: 1;
    min-width: 620px;
    max-width: 700px;
}

.info-card>* {
    position: relative;
    z-index: 1;
}

.card-header {
    margin-bottom: var(--spacing-gap-lg);
    border-bottom: 1px solid var(--color-border);
    padding-bottom: 16px;
}

.card-header h3 {
    font-size: var(--font-size-card-title);
    font-weight: 600;
    color: var(--color-text-primary);
    margin: 0;
}

.info-form {
    width: 100%;
}

.btn-box {
    text-align: right;
    margin-top: var(--spacing-gap);
}

.tip-text {
    font-size: 12px;
    color: var(--color-text-muted);
    margin-top: 6px;
}

/* 暗黑模式 */
body.dark-mode .page-title {
    color: var(--color-dark-text);
}

body.dark-mode .info-card {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

body.dark-mode .card-header {
    border-color: var(--color-dark-border);
}

body.dark-mode .card-header h3 {
    color: var(--color-dark-text);
}

body.dark-mode /deep/ .el-form-item__label {
    color: var(--color-dark-text-muted);
}

body.dark-mode .tip-text {
    color: var(--color-dark-text-muted);
}
</style>