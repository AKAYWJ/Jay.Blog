<template>
    <ClientOnly>
    <div class="login-page">
        <!-- 浮动光斑装饰 -->
        <div class="orb-decor">
            <div class="orb orb-1"></div>
            <div class="orb orb-2"></div>
            <div class="orb orb-3"></div>
        </div>

        <!-- 左侧大图区域 -->
        <div class="left-banner">
            <div class="banner-text">
                <div class="brand-tag">Jay.Blog</div>
                <h1 class="banner-title">{{ tabType === 'login' ? t('login.welcome') : t('login.join') }}</h1>
                <p class="banner-desc">
                    {{ tabType === 'login'
                        ? t('login.banner.login')
                        : t('login.banner.register') }}
                </p>
                <div class="feature-list" v-if="tabType === 'register'">
                    <div class="feature-item">📊 {{ t('login.banner.feature.data') }}</div>
                    <div class="feature-item">⚡ {{ t('login.banner.feature.visitor') }}</div>
                    <div class="feature-item">🛡️ {{ t('login.banner.feature.role') }}</div>
                    <div class="feature-item">🌙 {{ t('login.banner.feature.dark') }}</div>
                </div>
            </div>
        </div>

        <!-- 右侧表单区域 -->
        <div class="right-form">
            <!-- 液态玻璃卡片 -->
            <div class="form-wrap liquid-glass">
                <h2 class="form-head">
                    {{ tabType === 'login' ? t('login.form.login') : t('login.form.register') }}
                </h2>

                <!-- 可滚动表单容器 -->
                <div class="form-scroll">
                    <!-- 登录表单 -->
                    <form v-if="tabType === 'login'" @submit.prevent="handleLogin">
                        <div class="input-group">
                            <label class="input-label">{{ t('login.phone') }}</label>
                            <input v-model="loginForm.phone" type="text" class="form-input" :placeholder="t('login.placeholder.phone')" required>
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('login.password') }}</label>
                            <input v-model="loginForm.password" type="password" class="form-input" :placeholder="t('login.placeholder.password')" required>
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('login.captcha') }}</label>
                            <div class="captcha-row">
                                <input v-model="loginForm.captcha" type="text" class="form-input captcha-input" :placeholder="t('login.placeholder.captcha')" maxlength="4" required>
                                <div class="captcha-img" @click="refreshCaptcha" v-html="captchaSvg" :title="t('login.captcha.refresh')"></div>
                            </div>
                        </div>
                        <button type="submit" class="submit-btn">{{ t('login.button') }}</button>
                    </form>

                    <!-- 注册表单 -->
                    <form v-if="tabType === 'register'" @submit.prevent="handleRegister">
                        <div class="input-group">
                            <label class="input-label">{{ t('register.phone') }}</label>
                            <input v-model="registerForm.phone" type="tel" class="form-input" :placeholder="t('login.placeholder.phone')" required>
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('register.password') }}</label>
                            <input v-model="registerForm.password" type="password" class="form-input" :placeholder="t('register.placeholder.password')" required>
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('register.name') }}</label>
                            <input v-model="registerForm.name" type="text" class="form-input" :placeholder="t('register.placeholder.name')" required>
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('register.email') }}</label>
                            <input v-model="registerForm.email" type="email" class="form-input" :placeholder="t('register.placeholder.email')">
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('register.age') }}</label>
                            <input v-model.number="registerForm.age" type="number" class="form-input" :placeholder="t('register.placeholder.age')">
                        </div>
                        <div class="input-group">
                            <label class="input-label">{{ t('register.sex') }}</label>
                            <select v-model="registerForm.sex" class="form-input select-input">
                                <option value="">{{ t('register.sex.select') }}</option>
                                <option value="1">{{ t('register.sex.male') }}</option>
                                <option value="0">{{ t('register.sex.female') }}</option>
                            </select>
                        </div>
                        <button type="submit" class="submit-btn">{{ t('register.button') }}</button>
                    </form>
                </div>

                <p class="tip-text">
                    {{ t('login.agree') }}
                    <a href="#" class="link-text">{{ t('login.terms') }}</a> {{ t('login.and') }}
                    <a href="#" class="link-text">{{ t('login.privacy') }}</a>
                </p>

                <p class="switch-row">
                    {{ tabType === 'login' ? t('login.noAccount') : t('login.hasAccount') }}
                    <span class="switch-link" @click="toggleTab">
                        {{ tabType === 'login' ? t('login.goRegister') : t('login.goLogin') }}
                    </span>
                </p>
            </div>
        </div>
    </div>
    </ClientOnly>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import { navigateTo } from 'nuxt/app'
import { ElNotification } from 'element-plus'
import { login, register } from '~/api/user'
import { getCaptcha } from '~/api/captcha'
import { useUserStore } from '~/stores/store'
import { useI18n } from '~/composables/useI18n'

definePageMeta({
    layout: 'login'
})

const { t } = useI18n()

// Pinia 用户仓库
const userStore = useUserStore()

// 切换登录/注册标签
const tabType = ref('login')

// 登录表单数据
const loginForm = ref({
    phone: '',
    password: '',
    captcha: '',
    captchaId: ''
})

// 注册表单数据
const registerForm = ref({
    name: '',
    password: '',
    phone: '',
    email: '',
    age: '',
    sex: ''
})

// 验证码 SVG（来自后端）
const captchaSvg = ref('')
const refreshCaptcha = async () => {
    try {
        const res = await getCaptcha()
        if (res.code === 200) {
            captchaSvg.value = res.data.svg
            loginForm.value.captchaId = res.data.id
            loginForm.value.captcha = ''
        }
    } catch {
        captchaSvg.value = `<text x="20" y="32" font-size="22" fill="#fff">${t('login.captcha.fail')}</text>`
    }
}

// 切换标签时刷新验证码
watch(tabType, (val) => {
    if (val === 'login') refreshCaptcha()
})

// 切换标签
const toggleTab = () => {
    tabType.value = tabType.value === 'login' ? 'register' : 'login'
}

// 登录提交
const handleLogin = async () => {
    if (!loginForm.value.captcha) {
        ElNotification.error({ title: t('notif.error'), message: t('login.captchaRequired') })
        return
    }
    try {
        const res = await login(loginForm.value)
        if (res.code === 200) {
            userStore.setUserInfo(res.data)
            userStore.setToken(res.data.token)
            ElNotification({
                title: t('notif.success'),
                message: res.msg,
                type: 'success'
            })
            await navigateTo('/front')
        } else {
            ElNotification.error({
                title: t('notif.error'),
                message: res.msg
            })
            refreshCaptcha()
        }
    } catch (err) {
        ElNotification.error({
            title: t('notif.requestFail'),
            message: err?.message || t('notif.networkError')
        })
        refreshCaptcha()
    }
}

// 注册提交
const handleRegister = async () => {
    try {
        const form = { ...registerForm.value }
        const res = await register(form)
        if (res.code === 200) {
            ElNotification({
                title: t('notif.success'),
                message: res.data || t('register.success'),
                type: 'success'
            })
            // 切到登录页 + 自动填充刚才注册的手机号 + 聚焦密码框
            loginForm.value.phone = registerForm.value.phone
            loginForm.value.password = ''
            registerForm.value.password = ''
            tabType.value = 'login'
            refreshCaptcha()
            nextTick(() => {
                const pwdInput = document.querySelector('input[type="password"]') as HTMLInputElement | null
                pwdInput?.focus()
            })
        } else {
            ElNotification.error({
                title: t('notif.error'),
                message: res.data || t('register.fail')
            })
        }
    } catch (err) {
        ElNotification.error({
            title: t('notif.requestFail'),
            message: err?.message || t('notif.networkError')
        })
    }
}

onMounted(() => {
    refreshCaptcha()
})
</script>

<style scoped>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: system-ui, -apple-system, sans-serif;
}

.login-page {
    position: relative;
    width: 100vw;
    height: 100vh;
    display: flex;
    overflow: hidden;
}

/* 浮动光斑装饰 */
.orb-decor {
    position: absolute;
    inset: 0;
    pointer-events: none;
    z-index: -1;
}

.orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    opacity: 0.5;
    animation: orb-float 12s ease-in-out infinite;
}

.orb-1 {
    width: 380px;
    height: 380px;
    background: radial-gradient(circle, #6b5cf0, transparent);
    top: 15%;
    left: 10%;
}

.orb-2 {
    width: 320px;
    height: 320px;
    background: radial-gradient(circle, #ec4899, transparent);
    bottom: 10%;
    right: 8%;
    animation-delay: 3s;
}

.orb-3 {
    width: 280px;
    height: 280px;
    background: radial-gradient(circle, #06b6d4, transparent);
    top: 55%;
    left: 45%;
    animation-delay: 6s;
}

@keyframes orb-float {
    0%, 100% { transform: translate(0, 0) scale(1); }
    33% { transform: translate(40px, -30px) scale(1.08); }
    66% { transform: translate(-30px, 40px) scale(0.92); }
}

/* 品牌标签 */
.brand-tag {
    display: inline-block;
    padding: 6px 16px;
    background: rgba(255, 255, 255, 0.18);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.28);
    border-radius: 999px;
    font-size: 13px;
    font-weight: 600;
    letter-spacing: 1px;
    margin-bottom: 24px;
    color: #fff;
}

/* 验证码行 */
.captcha-row {
    display: flex;
    gap: 10px;
    align-items: stretch;
}

.captcha-input {
    flex: 1;
}

.captcha-img {
    width: 110px;
    height: 44px;
    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 6px;
    background: rgba(255, 255, 255, 0.08);
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    transition: border-color 0.2s, background 0.2s;
}

.captcha-img:hover {
    border-color: rgba(148, 184, 248, 0.8);
    background: rgba(255, 255, 255, 0.12);
}

.captcha-img :deep(svg) {
    width: 100%;
    height: 100%;
    display: block;
}

/* 全局背景图 */
.login-page::before {
    content: "";
    position: absolute;
    inset: 0;
    background: url("~/assets/background.png") center center / cover no-repeat;
    z-index: -2;
}

/* 全局暗色遮罩，提升玻璃文字可读性 */
.login-page::after {
    content: "";
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.35);
    z-index: -1;
}

/* 左侧大图区域 50%宽度 */
.left-banner {
    width: 50%;
    position: relative;
    color: #fff;
    display: flex;
    align-items: flex-end;
    padding: 60px;
}

.banner-text {
    position: relative;
    z-index: 2;
}

.banner-title {
    font-size: 42px;
    font-weight: 500;
    margin-bottom: 16px;
}

.banner-desc {
    font-size: 16px;
    opacity: 0.9;
    line-height: 1.6;
    margin-bottom: 32px;
    max-width: 480px;
}

.feature-list {
    display: flex;
    flex-direction: column;
    gap: 14px;
}

.feature-item {
    font-size: 15px;
    opacity: 0.95;
}

/* 右侧表单区域 50%宽度 */
.right-form {
    width: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    height: 100vh;
}

/* 液态玻璃核心样式 */
.liquid-glass {
    position: relative;
    width: 100%;
    max-width: 420px;
    padding: 32px;
    max-height: 88vh;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.12);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid rgba(255, 255, 255, 0.22);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
}

.liquid-glass>* {
    position: relative;
    z-index: 2;
}

.form-head {
    font-size: 32px;
    font-weight: 500;
    color: #ffffff;
    margin-bottom: 24px;
    flex-shrink: 0;
}

/* 表单滚动容器：超出自动滚动 */
.form-scroll {
    overflow-y: auto;
    padding-right: 6px;
    flex: 1;
    scrollbar-width: thin;
    scrollbar-color: rgba(255, 255, 255, 0.3) transparent;
}

.form-scroll::-webkit-scrollbar {
    width: 4px;
}

.form-scroll::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 4px;
}

.form-scroll::-webkit-scrollbar-track {
    background: transparent;
}

/* 表单输入框 */
.input-group {
    margin-bottom: 18px;
}

.input-label {
    display: block;
    font-size: 14px;
    color: #f1f1f1;
    margin-bottom: 6px;
}

.form-input {
    width: 100%;
    height: 44px;
    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 6px;
    padding: 0 14px;
    font-size: 15px;
    outline: none;
    transition: border 0.2s;
    background: rgba(255, 255, 255, 0.08);
    color: #fff;
}

.form-input::placeholder {
    color: rgba(255, 255, 255, 0.55);
}

.form-input:focus {
    border-color: rgba(148, 184, 248, 0.8);
    background: rgba(255, 255, 255, 0.12);
}

.select-input {
    appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg fill='rgba(255,255,255,0.7)' width='16' height='16' viewBox='0 0 24 24' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M7 10l5 5 5-5z'/%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 12px center;
}

/* 提交按钮 贴合原图浅蓝纯色 */
.submit-btn {
    width: 100%;
    height: 44px;
    background: rgba(179, 204, 250, 0.75);
    border: none;
    border-radius: 6px;
    font-size: 16px;
    color: #0f0f0f;
    cursor: pointer;
    transition: all 0.2s;
    margin-top: 6px;
    backdrop-filter: blur(4px);
}

.submit-btn:hover {
    background: rgba(160, 191, 238, 0.9);
}

/* 底部小字固定底部不滚动 */
.tip-text {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.72);
    margin-top: 20px;
    line-height: 1.5;
    flex-shrink: 0;
}

.link-text {
    color: #ffffff;
    text-decoration: underline;
    margin: 0 4px;
}

.switch-row {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.85);
    margin-top: 16px;
    text-align: center;
    flex-shrink: 0;
}

.switch-link {
    color: #ffffff;
    font-weight: 500;
    cursor: pointer;
    margin-left: 6px;
    text-decoration: underline;
}

/* 移动端适配 */
@media (max-width: 1024px) {
    .login-page {
        flex-direction: column;
    }

    .left-banner,
    .right-form {
        width: 100%;
        height: 50vh;
    }

    .right-form {
        height: 50vh;
    }

    .banner-title {
        font-size: 32px;
    }

    .form-head {
        font-size: 26px;
        margin-bottom: 20px;
    }

    .liquid-glass {
        padding: 24px;
        max-height: 82vh;
    }
}

@media (max-width: 640px) {
    .left-banner {
        padding: 30px 20px;
    }

    .right-form {
        padding: 20px;
    }

    .banner-title {
        font-size: 26px;
    }

    .liquid-glass {
        padding: 20px 16px;
    }

    .input-group {
        margin-bottom: 14px;
    }
}
</style>