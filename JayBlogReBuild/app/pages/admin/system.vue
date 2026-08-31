<template>
    <div class="system-management">
        <h1 class="page-title">{{ t('admin.system.title') }}</h1>

        <div class="card-wrap">
            <div class="info-card">
                <div class="card-header">
                    <h3>{{ t('admin.system.siteConfig') }}</h3>
                </div>
                <el-form ref="configFormRef" :model="configForm" :rules="configRules" label-width="100px"
                    class="info-form">
                    <el-form-item :label="t('admin.system.siteName')" prop="blogName">
                        <el-input v-model="configForm.blogName" :placeholder="t('admin.system.siteNamePlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('admin.system.avatar')" prop="blogAvatar">
                        <div v-if="configForm.blogAvatar" style="margin-bottom: 10px">
                            <img :src="configForm.blogAvatar"
                                style="width: 100px; height: 100px; object-fit: cover; border-radius: 8px;" />
                        </div>
                        <el-upload :http-request="uploadAvatar" :on-success="handleAvatarSuccess"
                            accept="image/jpeg,image/png,image/webp" :show-file-list="false">
                            <el-button>{{ t('admin.system.uploadAvatar') }}</el-button>
                        </el-upload>
                        <div class="tip-text">{{ t('admin.system.avatarTip') }}</div>
                    </el-form-item>

                    <el-form-item :label="t('admin.system.signature')" prop="blogSignature">
                        <el-input v-model="configForm.blogSignature" type="text" :placeholder="t('admin.system.signaturePlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('admin.system.aboutMe')" prop="aboutMe">
                        <el-input v-model="configForm.aboutMe" type="textarea" :rows="4" :placeholder="t('admin.system.aboutMePlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('admin.system.github')" prop="github">
                        <el-input v-model="configForm.github" :placeholder="t('admin.system.githubPlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('admin.system.qq')" prop="qq">
                        <el-input v-model="configForm.qq" :placeholder="t('admin.system.qqPlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('admin.system.email')" prop="email">
                        <el-input v-model="configForm.email" :placeholder="t('admin.system.emailPlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('admin.system.allowComment')" prop="allowComment">
                        <el-switch v-model="configForm.allowComment" :active-value="1" :inactive-value="0" />
                    </el-form-item>

                    <el-form-item class="btn-box">
                        <el-button type="primary" :loading="configLoading" @click="handleConfigSubmit">{{ t('admin.system.saveConfig') }}</el-button>
                        <el-button @click="resetConfigForm">{{ t('admin.common.reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBlogConfig, updateBlogConfig } from '~/api/system'
import { request } from '~/api/request'

const { t } = useI18n()

const configForm = reactive({
    id: 1,
    blogName: '',
    blogAvatar: '',
    blogSignature: '',
    aboutMe: '',
    github: '',
    qq: '',
    email: '',
    allowComment: 1
})
const configLoading = ref(false)
const configFormRef = ref()
const configRules = {
    blogName: [{ required: true, message: t('admin.system.siteNameRequired'), trigger: 'blur' }],
    aboutMe: [{ required: true, message: t('admin.system.aboutMeRequired'), trigger: 'blur' }],
    qq: [{ pattern: /^\d{5,12}$/, message: t('admin.system.qqInvalid'), trigger: 'blur' }],
    email: [
        { pattern: /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,10})+$/, message: t('admin.system.emailInvalid'), trigger: 'blur' }
    ]
}

const loadBlogConfig = async () => {
    try {
        const res = await getBlogConfig()
        Object.assign(configForm, res.data || {})
    } catch (err) {
        console.error('加载配置失败', err)
    }
}

onMounted(() => {
    loadBlogConfig()
})

const uploadAvatar = (param) => {
    const file = param.file
    const limit = 2 * 1024 * 1024
    if (file.size > limit) {
        ElMessage.error(t('admin.system.avatarSizeLimit'))
        param.onError(new Error('超出大小'))
        return
    }
    const formData = new FormData()
    formData.append('file', file)
    request({
        method: 'POST',
        url: '/upload/cover',
        data: formData,
        headers: { 'Content-Type': 'multipart/form-data' }
    }).then(res => {
        param.onSuccess(res.data)
    }).catch(err => {
        ElMessage.error(t('admin.system.uploadFail'))
        param.onError(err)
    })
}

const handleAvatarSuccess = (res) => {
    if (res.code === 200) {
        configForm.blogAvatar = res.data
        ElMessage.success(t('admin.system.avatarUploadSuccess'))
    } else {
        ElMessage.error(res.msg)
    }
}

const handleConfigSubmit = () => {
    configFormRef.value.validate(async valid => {
        if (!valid) return
        configLoading.value = true
        try {
            await updateBlogConfig(configForm)
            ElMessage.success(t('admin.system.configSaveSuccess'))
        } catch (err) {
            ElMessage.error(t('admin.system.configSaveFail'))
        } finally {
            configLoading.value = false
        }
    })
}

const resetConfigForm = () => {
    configFormRef.value.resetFields()
    loadBlogConfig()
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

.info-card > * {
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

body.dark-mode :deep(.el-form-item__label) {
    color: var(--color-dark-text-muted);
}

body.dark-mode .tip-text {
    color: var(--color-dark-text-muted);
}
</style>
