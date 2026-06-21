<template>
    <div class="profile-page">
        <!-- 液态玻璃卡片 -->
        <div class="liquid-glass form-card">
            <h2 class="page-title">个人设置</h2>

            <el-form ref="profileFormRef" :model="user" label-position="top" class="profile-form">
                <!-- 头像上传行（复用项目统一上传逻辑） -->
                <div class="avatar-row">
                    <div class="avatar-preview">
                        <img v-if="user.avatar" :src="user.avatar" alt="头像" />
                        <svg v-else width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="1.5" stroke-linecap="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </div>
                    <el-upload action="" :http-request="uploadAvatar" :on-success="handleAvatarSuccess"
                        accept="image/jpeg,image/png,image/webp" :show-file-list="false">
                        <el-button class="avatar-btn" size="small" plain>更换头像</el-button>
                    </el-upload>
                </div>

                <!-- 用户名 -->
                <el-form-item label="用户名">
                    <el-input v-model="user.username" placeholder="请输入用户名"></el-input>
                </el-form-item>

                <!-- 邮箱 -->
                <el-form-item label="电子邮箱">
                    <el-input v-model="user.email" placeholder="请填写电子邮箱"></el-input>
                </el-form-item>

                <!-- 手机号（只读） -->
                <el-form-item label="手机号">
                    <el-input v-model="user.phone" disabled></el-input>
                </el-form-item>

                <!-- 年龄 -->
                <el-form-item label="年龄">
                    <el-input v-model.number="user.age" type="number" placeholder="请输入年龄"></el-input>
                </el-form-item>

                <!-- 性别 -->
                <el-form-item label="性别">
                    <el-select v-model="user.sex" placeholder="请选择性别" style="width:100%">
                        <el-option label="男" :value="1"></el-option>
                        <el-option label="女" :value="2"></el-option>
                        <el-option label="保密" :value="0"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 重置密码 -->
                <el-form-item label="修改密码">
                    <el-input v-model="user.password" type="password" show-password placeholder="留空则不修改密码"></el-input>
                </el-form-item>

                <!-- 提交按钮 -->
                <el-form-item>
                    <el-button class="submit-btn" type="primary" :loading="saveLoading"
                        @click="handleSave">保存修改</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script>
import { updateUser } from '@/api/user'
import axios from 'axios'

export default {
    name: 'Profile',
    data() {
        return {
            saveLoading: false,
            user: {
                username: '',
                email: '',
                phone: '',
                age: null,
                sex: null,
                avatar: '',
                password: ''
            }
        }
    },
    created() {
        // 从Vuex完整读取用户信息
        const info = this.$store.state.userInfo
        this.user.username = info.username || ''
        this.user.email = info.email || ''
        this.user.phone = info.phone || ''
        this.user.age = info.age || null
        this.user.sex = info.sex
        this.user.avatar = info.avatar || ''
    },
    methods: {
        // 统一头像上传，和文章封面上传逻辑完全一致
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
        // 上传成功赋值头像
        handleAvatarSuccess(res) {
            if (res.code === 200) {
                this.user.avatar = res.data
                this.$message.success('头像上传成功')
            } else {
                this.$message.error(res.msg)
            }
        },
        // 保存全部个人信息
        async handleSave() {
            const userInfo = this.$store.state.userInfo
            this.saveLoading = true
            try {
                const params = {
                    id: userInfo.userId,
                    name: this.user.username,
                    email: this.user.email,
                    phone: this.user.phone,
                    age: this.user.age,
                    sex: this.user.sex,
                    avatar: this.user.avatar,
                    bio: this.user.bio
                }
                // 密码非空才传递，后端加密更新
                if (this.user.password && this.user.password.trim() !== '') {
                    params.password = this.user.password
                }

                await updateUser(params)
                // 更新Vuex全局用户缓存
                this.$store.commit('SET_USER_INFO', {
                    ...userInfo,
                    username: this.user.username,
                    email: this.user.email,
                    age: this.user.age,
                    sex: this.user.sex,
                    avatar: this.user.avatar,
                    phone: this.user.phone
                })
                this.$message.success('个人信息保存成功')
                // 清空密码输入框
                this.user.password = ''
            } catch (e) {
                this.$message.error(e?.response?.data?.message || '保存失败')
                console.error(e)
            } finally {
                this.saveLoading = false
            }
        }
    }
}
</script>

<style scoped>
.profile-page {
    max-width: 800px;
    margin: -40px auto;
    padding: 40px 0 80px;
}

/* ===== 液态玻璃通用 ===== */
.liquid-glass {
    position: relative;
    overflow: hidden;
    background: var(--liquid-glass-bg);
    backdrop-filter: var(--liquid-glass-blur);
    -webkit-backdrop-filter: var(--liquid-glass-blur);
    border: var(--liquid-glass-border);
    border-radius: var(--radius-xl);
    box-shadow: var(--liquid-glass-shadow);
}

.liquid-glass>* {
    position: relative;
    z-index: 1;
}

/* ===== 表单卡片 ===== */
.form-card {
    padding: 40px;
}

.page-title {
    font-size: 22px;
    font-weight: 700;
    color: var(--color-text-primary);
    margin: 0 0 32px;
    text-align: center;
}

/* ===== 表单 ===== */
.profile-form {
    max-width: 400px;
    margin: 0 auto;
}

/* 头像行 */
.avatar-row {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 22px;
}

.avatar-preview {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: var(--color-primary-light-bg);
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--color-primary);
    box-shadow: 0 4px 20px rgba(91, 79, 207, 0.12);
    flex-shrink: 0;
    overflow: hidden;
}

.avatar-preview img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.avatar-btn {
    border-radius: 12px;
}

/* ===== 覆盖 Element UI 表单样式 ===== */
.profile-form ::v-deep .el-form-item__label {
    font-size: 14px;
    color: var(--color-text-regular);
    font-weight: 500;
    line-height: 1.4;
    padding-bottom: 6px;
}

.profile-form ::v-deep .el-input__inner,
.profile-form ::v-deep .el-textarea__inner,
.profile-form ::v-deep .el-select__input {
    border-radius: 12px;
    background: var(--color-bg-primary);
    border-color: var(--color-border);
    transition: border-color 0.2s;
}

.profile-form ::v-deep .el-input__inner:focus,
.profile-form ::v-deep .el-textarea__inner:focus,
.profile-form ::v-deep .el-select__input:focus {
    border-color: var(--color-primary);
}

.profile-form ::v-deep .el-input.is-disabled .el-input__inner {
    background: var(--color-bg-muted);
    color: var(--color-text-muted);
    cursor: not-allowed;
}

/* 提交按钮 */
.submit-btn {
    width: 120px;
    border-radius: 12px;
}

/* ===== 暗黑模式 ===== */
body.dark-mode .liquid-glass {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

body.dark-mode .page-title {
    color: var(--color-dark-text);
}

body.dark-mode .avatar-preview {
    background: rgba(91, 79, 207, 0.15);
}

body.dark-mode .profile-form ::v-deep .el-form-item__label {
    color: var(--color-dark-text-muted);
}
</style>