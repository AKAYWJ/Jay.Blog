<template>
    <div class="profile-page">
        <!-- 液态玻璃卡片 -->
        <div class="liquid-glass form-card">
            <h2 class="page-title">{{ t('profile.pageTitle') }}</h2>

            <el-form ref="profileFormRef" :model="user" label-position="top" class="profile-form">
                <!-- 头像上传行 -->
                <div class="avatar-row">
                    <div class="avatar-preview">
                        <img v-if="user.avatar" :src="user.avatar" :alt="t('profile.avatar.alt')" />
                        <svg v-else width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="1.5" stroke-linecap="round">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                            <circle cx="12" cy="7" r="4"></circle>
                        </svg>
                    </div>
                    <el-upload action="" :http-request="uploadAvatar" :on-success="handleAvatarSuccess"
                        accept="image/jpeg,image/png,image/webp" :show-file-list="false">
                        <el-button class="avatar-btn" size="small" plain>{{ t('profile.avatar.change') }}</el-button>
                    </el-upload>
                </div>

                <!-- 用户名 -->
                <el-form-item :label="t('profile.form.username')">
                    <el-input v-model="user.username" :placeholder="t('profile.form.username.placeholder')"></el-input>
                </el-form-item>

                <!-- 邮箱 -->
                <el-form-item :label="t('profile.form.email')">
                    <el-input v-model="user.email" :placeholder="t('profile.form.email.placeholder')"></el-input>
                </el-form-item>

                <!-- 手机号（只读） -->
                <el-form-item :label="t('profile.form.phone')">
                    <el-input v-model="user.phone" disabled></el-input>
                </el-form-item>

                <!-- 年龄 -->
                <el-form-item :label="t('profile.form.age')">
                    <el-input v-model.number="user.age" type="number" :placeholder="t('profile.form.age.placeholder')"></el-input>
                </el-form-item>

                <!-- 性别 -->
                <el-form-item :label="t('profile.form.gender')">
                    <el-select v-model="user.sex" :placeholder="t('profile.form.gender.placeholder')" style="width:100%">
                        <el-option :label="t('profile.form.gender.male')" :value="1"></el-option>
                        <el-option :label="t('profile.form.gender.female')" :value="2"></el-option>
                        <el-option :label="t('profile.form.gender.secret')" :value="0"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 重置密码 -->
                <el-form-item :label="t('profile.form.password')">
                    <el-input v-model="user.password" type="password" show-password :placeholder="t('profile.form.password.placeholder')"></el-input>
                </el-form-item>

                <!-- 提交按钮 -->
                <el-form-item>
                    <el-button class="submit-btn" type="primary" :loading="saveLoading"
                        @click="handleSave">{{ t('profile.form.save') }}</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updateSelf } from '~/api/user'
import { useUserStore } from '~/stores/store'
import { request } from '~/api/request'
import { useI18n } from '~/composables/useI18n'

const { t } = useI18n()

// Pinia 用户仓库
const userStore = useUserStore()
const userInfo = userStore.userInfo

// 加载状态
const saveLoading = ref(false)

// 表单数据
const user = ref({
    username: '',
    email: '',
    phone: '',
    age: null,
    sex: null,
    avatar: '',
    password: ''
})

// 页面初始化赋值用户信息
const loadUserInfo = () => {
    user.value.username = userInfo.username || ''
    user.value.email = userInfo.email || ''
    user.value.phone = userInfo.phone || ''
    user.value.age = userInfo.age || null
    user.value.sex = userInfo.sex
    user.value.avatar = userInfo.avatar || ''
}

// 头像上传
const uploadAvatar = async (param) => {
    const file = param.file
    const limit = 2 * 1024 * 1024
    if (file.size > limit) {
        ElMessage.error(t('profile.avatar.tooLarge'))
        param.onError(new Error('超出大小'))
        return
    }
    const formData = new FormData()
    formData.append('file', file)
    try {
        const res = await request({
            method: 'POST',
            url: '/upload/cover',
            data: formData,
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
        param.onSuccess(res)
    } catch (err) {
        console.log(err);
        
        ElMessage.error(t('profile.avatar.uploadFail'))
        param.onError(err)
    }
}

// 上传成功回调
const handleAvatarSuccess = (res) => {
    if (res.code === 200) {
        user.value.avatar = res.data
        ElMessage.success(t('profile.avatar.uploadSuccess'))
    } else {
        ElMessage.error(res.msg)
    }
}

// 保存个人信息
const handleSave = async () => {
    saveLoading.value = true
    try {
        // 只提交白名单字段（不能改 id/phone/roleId，后端 /user/self/update 已限制）
        const params = {
            name: user.value.username,
            email: user.value.email,
            age: user.value.age,
            sex: user.value.sex,
            avatar: user.value.avatar
        }
        // 密码非空才传递
        if (user.value.password && user.value.password.trim() !== '') {
            params.password = user.value.password
        }
        await updateSelf(params)
        // 更新Pinia用户信息
        userStore.setUserInfo({
            ...userInfo,
            username: user.value.username,
            email: user.value.email,
            age: user.value.age,
            sex: user.value.sex,
            avatar: user.value.avatar,
            phone: user.value.phone
        })
        ElMessage.success(t('profile.saveSuccess'))
        // 清空密码框
        user.value.password = ''
    } catch (e) {
        ElMessage.error(e?.data?.msg || e?.msg || t('profile.saveFail'))
        console.error(e)
    } finally {
        saveLoading.value = false
    }
}

onMounted(() => {
    loadUserInfo()
})
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

/* ===== Element Plus 深度样式 :deep() ===== */
:deep(.el-form-item__label) {
    font-size: 14px;
    color: var(--color-text-regular);
    font-weight: 500;
    line-height: 1.4;
    padding-bottom: 6px;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner),
:deep(.el-select__input) {
    border-radius: 12px;
    background: var(--color-bg-primary);
    border-color: var(--color-border);
    transition: border-color 0.2s;
}

:deep(.el-input__inner:focus),
:deep(.el-textarea__inner:focus),
:deep(.el-select__input:focus) {
    border-color: var(--color-primary);
}

:deep(.el-input.is-disabled .el-input__inner) {
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
:deep(body.dark-mode) .liquid-glass {
    background: var(--liquid-glass-bg);
    border: var(--liquid-glass-border);
    box-shadow: var(--liquid-glass-shadow);
}

:deep(body.dark-mode) .page-title {
    color: var(--color-dark-text);
}

:deep(body.dark-mode) .avatar-preview {
    background: rgba(91, 79, 207, 0.15);
}

:deep(body.dark-mode) .el-form-item__label {
    color: var(--color-dark-text-muted);
}

@media screen and (max-width: 768px) {
    .form-card {
        padding: 24px 20px;
    }
}
</style>