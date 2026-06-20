<template>
    <div class="login-page">
        <!-- 左侧品牌介绍区 -->
        <div class="left-section">
            <h1 class="main-title">{{ tabType === 'login' ? 'Welcome back' : 'Sign up for free' }}</h1>
            <p class="subtitle">
                {{ tabType === 'login'
                    ? 'Log in to your account to continue managing your system'
                    : 'Join millions of users using our system to improve efficiency' }}
            </p>

            <!-- 功能亮点列表 -->
            <div class="features-list" v-if="tabType === 'register'">
                <div class="feature-item">
                    <span class="check-icon">✓</span>
                    <span>Intelligent data analysis and reporting</span>
                </div>
                <div class="feature-item">
                    <span class="check-icon">✓</span>
                    <span>Real-time system monitoring and alerts</span>
                </div>
                <div class="feature-item">
                    <span class="check-icon">✓</span>
                    <span>AI-powered automation tools</span>
                </div>
                <div class="feature-item">
                    <span class="check-icon">✓</span>
                    <span>Team collaboration and permission management</span>
                </div>
            </div>
        </div>

        <!-- 右侧表单卡片 -->
        <div class="right-section">
            <div class="form-card">

                <!-- 登录表单 -->
                <form v-if="tabType === 'login'" @submit.prevent="handleLogin">
                    <div class="form-group">
                        <input v-model="loginForm.phone" type="text" class="form-input"
                            placeholder="Phone number" required>
                    </div>
                    <div class="form-group">
                        <input v-model="loginForm.password" type="password" class="form-input" placeholder="Password"
                            required>
                    </div>
                    <button type="submit" class="submit-btn">
                        {{ tabType === 'login' ? 'Sign in' : 'Sign up' }}
                        <span class="arrow-icon">→</span>
                    </button>
                </form>

                <!-- 注册表单 -->
                <form v-if="tabType === 'register'" @submit.prevent="handleRegister">

                    <div class="form-group">
                        <input v-model="registerForm.phone" type="tel" class="form-input" placeholder="Phone number *"
                            required>
                    </div>
                    <div class="form-group">
                        <input v-model="registerForm.password" type="password" class="form-input"
                            placeholder="Password *" required>
                    </div>
                    <div class="form-group">
                        <input v-model="registerForm.name" type="text" class="form-input" placeholder="Username *"
                            required>
                    </div>
                    <div class="form-group">
                        <input v-model.number="registerForm.age" type="number" class="form-input"
                            placeholder="Age (optional)">
                    </div>
                    <div class="form-group">
                        <select v-model="registerForm.sex" class="form-input form-select">
                            <option value="">Gender (optional)</option>
                            <option value="male">Male</option>
                            <option value="female">Female</option>
                            <option value="secret">Prefer not to say</option>
                        </select>
                    </div>
                    <button type="submit" class="submit-btn">
                        Sign up
                        <span class="arrow-icon">→</span>
                    </button>
                </form>

                <!-- 底部条款 -->
                <p class="terms-text">
                    By clicking {{ tabType === 'login' ? 'Sign in with Google' : 'Sign up with Google' }} or {{ tabType
                        === 'login' ? 'Sign in' : 'Sign up' }} with email you agree to our
                    <a href="#" class="terms-link">Terms of Service</a> and
                    <a href="#" class="terms-link">Privacy Policy</a>
                </p>

                <!-- 登录/注册切换 -->
                <p class="switch-text">
                    {{ tabType === 'login' ? "Don't have an account?" : "Already have an account?" }}
                    <span class="switch-link" @click="toggleTab">
                        {{ tabType === 'login' ? 'Sign up' : 'Sign in' }}
                    </span>
                </p>
            </div>
        </div>
    </div>
</template>

<script>
import { register,login } from '@/api/user'
export default {
    name: 'LoginRegister',
    data() {
        return {
            tabType: 'login', // login / register
            loginForm: {
                phone: '19999999999',
                password: ''
            },
            registerForm: {
                name: '',
                password: '',
                phone: '19999999999',
                age: '',
                sex: ''
            }
        }
    },
    methods: {
        toggleTab() {
            this.tabType = this.tabType === 'login' ? 'register' : 'login'
        },
        handleLogin() {
            login(this.loginForm).then((res) => {
                if(res.code == 200){
                    this.$store.dispatch("login",res.data)
                    this.$notify({
                        title: '成功',
                        message: res.msg,
                        type: 'success'
                    });
                    this.$router.push("/home")
                }else{
                    this.$notify.error({
                        title: '错误',
                        message: res.msg
                    });
                }
            })
            
        },
        handleRegister() {

            register(this.registerForm).then((res) => {

                if (res.code == 200) {

                    this.$notify({
                        title: '成功',
                        message: res.data,
                        type: 'success'
                    });
                    this.tabType = 'login'
                } else {
                    this.$notify.error({
                        title: '错误',
                        message: res.data
                    });
                }
            })
        }
    }
}
</script>

<style scoped>
/* 页面整体：深紫渐变背景，全屏高度 */
.login-page {
    width: 100vw;
    height: 100vh;
    background: var(--login-bg);
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 0 8%;
    box-sizing: border-box;
    font-family: var(--font-family);
}

/* 左侧区域 */
.left-section {
    flex: 1;
    max-width: 500px;
    color: white;
}

.main-title {
    font-size: 64px;
    font-weight: 800;
    line-height: 1.1;
    margin-bottom: 24px;
    letter-spacing: -1px;
}

.subtitle {
    font-size: 28px;
    line-height: 1.4;
    margin-bottom: 48px;
    opacity: 0.9;
}

.features-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.feature-item {
    display: flex;
    align-items: center;
    font-size: 18px;
    gap: 12px;
}

.check-icon {
    color: var(--color-success-light);
    font-weight: bold;
    font-size: 20px;
}

/* 右侧区域 */
.right-section {
    flex: 1;
    display: flex;
    justify-content: flex-end;
}

/* 表单卡片 */
.form-card {
    width: 440px;
    background: var(--color-white);
    border-radius: var(--radius-round);
    padding: 36px;
    box-shadow: var(--shadow-form);
}

/* Google登录按钮 */
.google-btn {
    width: 100%;
    height: 52px;
    border: 2px solid var(--color-google);
    border-radius: var(--radius-full);
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    font-size: 16px;
    font-weight: 500;
    color: var(--color-google);
    cursor: pointer;
    transition: var(--transition-fast);
    margin-bottom: 24px;
}

.google-btn:hover {
    background: #f8faff;
}

.google-icon {
    width: 24px;
    height: 24px;
}

/* OR分隔线 */
.divider {
    display: flex;
    align-items: center;
    margin-bottom: 24px;
    gap: 16px;
}

.divider-line {
    flex: 1;
    height: 1px;
    background: var(--color-border-light);
}

.divider-text {
    color: #6b7280;
    font-size: var(--font-size-body);
    font-weight: 500;
}

/* 表单输入框 */
.form-group {
    margin-bottom: 20px;
}

.form-input {
    width: 100%;
    height: 52px;
    border: 2px solid var(--color-border-light);
    border-radius: var(--radius-full);
    padding: 0 20px;
    font-size: 16px;
    box-sizing: border-box;
    transition: var(--transition-fast);
    outline: none;
}

.form-input:focus {
    border-color: var(--color-google);
    box-shadow: 0 0 0 3px rgba(0, 140, 255, 0.1);
}

.form-select {
    appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%236b7280'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 20px center;
    background-size: 20px;
}

/* 提交按钮 */
.submit-btn {
    width: 100%;
    height: 56px;
    background: var(--color-primary-gradient);
    border: none;
    border-radius: var(--radius-pill);
    color: white;
    font-size: 18px;
    font-weight: 600;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    cursor: pointer;
    transition: var(--transition-fast);
    margin-top: 8px;
}

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: var(--color-primary-hover-shadow);
}

.arrow-icon {
    font-size: 20px;
    font-weight: bold;
}

/* 底部文字 */
.terms-text {
    font-size: 13px;
    color: #6b7280;
    text-align: center;
    margin-top: 24px;
    line-height: 1.5;
}

.terms-link {
    color: var(--color-primary);
    text-decoration: none;
}

.switch-text {
    font-size: var(--font-size-body);
    color: var(--color-text-dark);
    text-align: center;
    margin-top: 20px;
}

.switch-link {
    color: var(--color-primary);
    font-weight: 600;
    cursor: pointer;
    margin-left: 4px;
}
</style>