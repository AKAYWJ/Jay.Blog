<template>
    <div class="front-layout">
        <!-- 顶部导航栏 固定悬浮 -->
        <header class="front-header">
            <div class="header-container">
                <!-- 左侧LOGO 椭圆Jay.Blog -->
                <div class="header-left logo">
                    <div class="ellipse-logo">Jay.Blog</div>
                </div>

                <!-- 桌面端导航菜单 -->
                <nav class="header-nav desktop-nav">
                    <router-link to="/front/index" class="nav-item" exact-active-class="active">首页</router-link>
                    <router-link to="/front/timeline" class="nav-item" active-class="active">时间轴</router-link>
                    <router-link to="/front/category/all" class="nav-item" active-class="active">分类</router-link>
                    <router-link to="/front/about" class="nav-item" active-class="active">关于</router-link>
                    <router-link v-if="isAdmin" to="/admin/home" class="nav-item admin-entry">管理后台</router-link>
                </nav>

                <!-- 右侧搜索+用户 -->
                <div class="header-right desktop-right">
                    <el-input v-model="searchKey" placeholder="搜索文章..." class="search-input" clearable
                        @keyup.enter.native="handleSearch(false)" @clear="handleSearch(true)"></el-input>
                    <div v-if="!isLogin" class="user-btn-group">
                        <el-button class="login-btn" @click="$router.push('/login')">登录</el-button>
                        <el-button class="register-btn" @click="$router.push('/register')">注册</el-button>
                    </div>
                    <el-dropdown v-else trigger="click" class="user-dropdown" @command="handleDropdownClick">
                        <div class="user-info">
                            <span class="username">{{ userName }}</span>
                            <i class="el-icon-arrow-down arrow-icon"></i>
                        </div>
                        <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
                            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                            <el-dropdown-item command="myComment">我的评论</el-dropdown-item>
                            <el-dropdown-item v-if="isAdmin" command="admin">进入管理后台</el-dropdown-item>
                            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>

                <!-- 移动端汉堡按钮 -->
                <div class="mobile-menu-btn" @click="mobileMenuShow = !mobileMenuShow">
                    <i class="el-icon-menu"></i>
                </div>
            </div>

            <!-- 移动端侧边弹窗菜单 -->
            <transition name="mobile-slide">
                <div v-if="mobileMenuShow" class="mobile-mask" @click="mobileMenuShow = false">
                    <div class="mobile-nav-box" @click.stop>
                        <div class="mobile-nav-item">
                            <router-link to="/front/index" exact-active-class="active"
                                @click="mobileMenuShow = false">首页</router-link>
                        </div>
                        <div class="mobile-nav-item">
                            <router-link to="/front/timeline" active-class="active"
                                @click="mobileMenuShow = false">时间轴</router-link>
                        </div>
                        <div class="mobile-nav-item">
                            <router-link to="/front/category/all" active-class="active"
                                @click="mobileMenuShow = false">分类</router-link>
                        </div>
                        <div class="mobile-nav-item">
                            <router-link to="/front/about" active-class="active"
                                @click="mobileMenuShow = false">关于</router-link>
                        </div>
                        <div v-if="isAdmin" class="mobile-nav-item">
                            <router-link to="/admin/home" @click="mobileMenuShow = false">管理后台</router-link>
                        </div>

                        <div class="mobile-search">
                            <el-input v-model="searchKey" placeholder="搜索文章..."
                                @keyup.enter.native="handleSearch(false); mobileMenuShow = false"
                                @clear="handleSearch(true)"></el-input>
                        </div>

                        <div class="mobile-divider"></div>

                        <div v-if="!isLogin" class="mobile-nav-item">
                            <router-link to="/login" @click="mobileMenuShow = false">登录</router-link>
                        </div>
                        <div v-if="!isLogin" class="mobile-nav-item">
                            <router-link to="/register" @click="mobileMenuShow = false">注册</router-link>
                        </div>
                        <div v-if="isLogin" class="mobile-nav-item">
                            <router-link to="/front/user/profile" @click="mobileMenuShow = false">个人中心</router-link>
                        </div>
                        <div v-if="isLogin" class="mobile-nav-item">
                            <span @click="goMyComment; mobileMenuShow = false">我的评论</span>
                        </div>
                        <div v-if="isLogin" class="mobile-nav-item logout-text">
                            <span @click="handleLogout">退出登录</span>
                        </div>
                    </div>
                </div>
            </transition>
        </header>

        <!-- 主体内容插槽 -->
        <main class="front-main">
            <div class="main-container">
                <router-view />
            </div>
        </main>

        <!-- 页脚 -->
        <footer class="front-footer">
            <div class="footer-container">
                <div class="footer-desc">
                    <p>Jay.Blog · 轻量人文技术博客</p>
                    <p class="footer-small">SpringBoot + Vue2 全栈开发 · 简约记录日常</p>
                </div>
            </div>
        </footer>
    </div>
</template>

<script>
import { addVisitorRecord } from '@/api/visitor'
import { mapGetters } from 'vuex'
export default {
    name: 'FrontLayout',
    data() {
        return {
            searchKey: '',
            mobileMenuShow: false
        }
    },
    created() {
        this.recordVisitor()
    },
    computed: {
        ...mapGetters(['isLoggedIn', 'getUsername', 'getRoleId']),
        isLogin() {
            return this.isLoggedIn
        },
        userName() {
            return this.getUsername
        },
        isAdmin() {
            const rid = this.getRoleId
            return String(rid) === '1' || String(rid) === '2'
        }
    },
    methods: {
        async recordVisitor() {
            try {
                await addVisitorRecord()
            } catch (err) { }
        },
        handleDropdownClick(cmd) {
            switch (cmd) {
                case 'profile':
                    this.$router.push('/front/user/profile')
                    break
                case 'myComment':
                    this.goMyComment()
                    break
                case 'admin':
                    this.$router.push('/admin/home')
                    break
                case 'logout':
                    this.handleLogout()
                    break
            }
        },
        handleSearch(clear) {
            if (clear) this.searchKey = ''
            this.$router.push({
                path: '/front/index',
                query: { key: this.searchKey.trim() }
            })
        },
        goMyComment() {
            this.$router.push('/front/user/comment')
        },
        handleLogout() {
            this.$confirm('确定退出登录？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'info'
            }).then(() => {
                this.$store.dispatch('logout')
            }).catch(() => { })
        }
    }
}
</script>

<style scoped>
/* 全局基础 柔和浅灰白底色 人文极简 */
.front-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: #f9f9fd;
    font-family: system-ui, -apple-system, "Segoe UI", Roboto, sans-serif;
}

/* 导航栏 柔和悬浮阴影 不厚重 */
.front-header {
    position: sticky;
    top: 0;
    z-index: 999;
    background: rgba(255, 255, 255, 0.92);
    backdrop-filter: saturate(180%) blur(8px);
    box-shadow: 0 1px 10px rgba(99, 85, 216, 0.05);
}

.header-container {
    width: 1200px;
    margin: 0 auto;
    height: 72px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
}

/* LOGO 柔和渐变椭圆 */
.header-left {
    display: flex;
    align-items: center;
}

.ellipse-logo {
    width: 116px;
    height: 42px;
    line-height: 42px;
    text-align: center;
    background: linear-gradient(135deg, #7c6ef0, #6355d8);
    color: #fff;
    border-radius: 999px;
    font-size: 18px;
    font-weight: 600;
    letter-spacing: 0.5px;
    box-shadow: 0 3px 10px rgba(99, 85, 216, 0.16);
    transition: all 0.24s ease;
}

.ellipse-logo:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 14px rgba(99, 85, 216, 0.22);
}

/* 桌面导航 */
.desktop-nav {
    display: flex;
    gap: 42px;
    align-items: center;
}

.nav-item {
    font-size: 15px;
    color: #444;
    position: relative;
    text-decoration: none;
    padding: 6px 0;
    transition: color 0.22s ease;
}

.nav-item:hover {
    color: #6355d8;
}

.nav-item.admin-entry {
    color: #7c6ef0;
}

/* 激活下划线 细渐变柔和线条，摒弃粗硬边框 */
.nav-item.active {
    color: #6355d8;
    font-weight: 500;
}

.nav-item.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 2.5px;
    background: linear-gradient(90deg, #7c6ef0, #6355d8);
    border-radius: 99px;
}

/* 右侧栏 */
.desktop-right {
    display: flex;
    align-items: center;
    gap: 20px;
}

.search-input /deep/ .el-input__inner {
    background: #f5f5fc;
    border: none;
    border-radius: 999px;
    padding: 0 16px;
    height: 36px;
    font-size: 14px;
    transition: 0.2s;
}

.search-input /deep/ .el-input__inner:focus {
    background: #efedfb;
    box-shadow: 0 0 0 2px rgba(99, 85, 216, 0.12);
}

.search-input {
    width: 230px;
}

/* 登录注册按钮组 */
.user-btn-group {
    display: flex;
    gap: 12px;
    align-items: center;
}

/* 登录按钮：浅紫底色，无厚重边框 */
.login-btn {
    background: #f0eefb;
    color: #6355d8;
    border: none;
    border-radius: 999px;
    padding: 0 18px;
    height: 36px;
    font-size: 14px;
    transition: all 0.22s ease;
}

.login-btn:hover {
    background: #e4e0f8;
    color: #5244c9;
}

/* 注册按钮：柔和渐变，精致微阴影 */
.register-btn {
    color: #222;
    border-radius: 999px;
    background: linear-gradient(135deg, #7c6ef0, #6355d8);
    border: none;
    padding: 0 18px;
    height: 36px;
    font-size: 14px;
    box-shadow: 0 2px 8px rgba(99, 85, 216, 0.18);
    transition: all 0.22s ease;
}

.register-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(99, 85, 216, 0.24);
    background: linear-gradient(135deg, #7263e8, #584acc);
}

/* 用户下拉 */
.user-info {
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    padding: 6px 10px;
    border-radius: 8px;
    transition: background 0.2s;
}

.user-info:hover {
    background: #f5f5fc;
}

.username {
    font-size: 14px;
    color: #222;
}

.arrow-icon {
    font-size: 12px;
    color: #999;
}

.user-dropdown-menu /deep/ .el-dropdown-menu__item {
    font-size: 14px;
    padding: 9px 18px;
}

.user-dropdown-menu /deep/ .el-dropdown-menu {
    border-radius: 10px;
    box-shadow: 0 6px 20px rgba(99, 85, 216, 0.09);
    border: none;
}

/* 移动端汉堡 */
.mobile-menu-btn {
    display: none;
    font-size: 24px;
    color: #333;
    cursor: pointer;
    padding: 4px;
    border-radius: 8px;
    transition: background 0.2s;
}

.mobile-menu-btn:hover {
    background: #f5f5fc;
}

/* 主体区域 更大留白 */
.front-main {
    flex: 1;
    padding: 48px 0;
}

.main-container {
    width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
}

/* 页脚 柔和浅紫底色 */
.front-footer {
    background: #f3f2fc;
    padding: 42px 0;
    margin-top: 60px;
}

.footer-container {
    width: 1200px;
    margin: 0 auto;
    text-align: center;
    color: #555;
}

.footer-desc p {
    margin: 6px 0;
    font-size: 15px;
}

.footer-small {
    font-size: 13px !important;
    color: #888;
}

/* ========== 移动端侧边栏 磨砂柔和风格 ========== */
.mobile-mask {
    position: fixed;
    top: 72px;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.32);
    backdrop-filter: blur(2px);
    z-index: 998;
}

.mobile-nav-box {
    width: 270px;
    height: 100%;
    background: #fff;
    padding: 24px;
    box-sizing: border-box;
    box-shadow: 2px 0 16px rgba(0, 0, 0, 0.06);
}

.mobile-nav-item {
    padding: 13px 0;
    font-size: 16px;
}

.mobile-nav-item a {
    color: #333;
    text-decoration: none;
}

.mobile-nav-item a.active {
    color: #6355d8;
    font-weight: 500;
}

.logout-text {
    color: #e06060;
}

.mobile-divider {
    height: 1px;
    background: #eeeef8;
    margin: 16px 0;
}

.mobile-search {
    margin-top: 24px;
}

.mobile-search /deep/ .el-input__inner {
    background: #f5f5fc;
    border: none;
    border-radius: 999px;
}

/* 滑入动画 */
.mobile-slide-enter-active {
    transition: opacity 0.26s ease;
}

.mobile-slide-leave-active {
    transition: opacity 0.2s ease;
}

.mobile-slide-enter,
.mobile-slide-leave-to {
    opacity: 0;
}

/* ========== 移动端适配 768px ========== */
@media screen and (max-width: 768px) {

    .desktop-nav,
    .desktop-right {
        display: none;
    }

    .mobile-menu-btn {
        display: block;
    }

    .header-container,
    .main-container,
    .footer-container {
        width: 100%;
    }

    .ellipse-logo {
        width: 100px;
        height: 38px;
        line-height: 38px;
        font-size: 16px;
    }

    .front-main {
        padding: 24px 0;
    }

    .front-footer {
        padding: 30px 0;
    }
}
</style>