<template>
    <div class="front-layout">
        <!-- 顶部导航栏 毛玻璃悬浮 -->
        <header class="front-header">
            <div class="header-container">
                <div class="header-left">
                    <router-link to="/front/index" class="logo-link">
                        <div class="ellipse-logo">Jay.Blog</div>
                    </router-link>
                </div>

                <nav class="header-nav">
                    <router-link to="/front/index" class="nav-item" exact-active-class="active">首页</router-link>
                    <router-link to="/front/timeline" class="nav-item" active-class="active">归档</router-link>
                    <router-link to="/front/category/all" class="nav-item" active-class="active">分类</router-link>
                    <router-link to="/front/about" class="nav-item" active-class="active">关于</router-link>
                    <router-link to="/front/profile" class="nav-item" active-class="active">个人中心</router-link>
                </nav>

                <div class="header-right">
                    <!-- 搜索图标触发 -->
                    <div class="search-trigger" :class="{ active: searchExpanded }" @click="toggleSearch">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                        </svg>
                    </div>

                    <div v-if="!isLogin" class="user-entry">
                        <span class="entry-link" @click="$router.push('/login')">登录</span>
                    </div>
                    <el-dropdown v-else trigger="click" @command="handleDropdownClick">
                        <div class="user-info">
                            <span class="username">{{ userName }}</span>
                        </div>
                        <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                            <el-dropdown-item v-if="isAdmin" command="admin">管理后台</el-dropdown-item>
                            <el-dropdown-item divided command="logout">退出</el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>

                    <div class="mobile-menu-btn" @click="mobileMenuShow = !mobileMenuShow">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round">
                            <line x1="3" y1="6" x2="21" y2="6"></line>
                            <line x1="3" y1="12" x2="21" y2="12"></line>
                            <line x1="3" y1="18" x2="21" y2="18"></line>
                        </svg>
                    </div>
                </div>
            </div>

            <!-- 搜索展开条 -->
            <transition name="search-slide">
                <div v-if="searchExpanded" class="search-bar">
                    <el-input v-model="searchKey" placeholder="搜索文章..." clearable
                        @keyup.enter.native="handleSearch(false)" @clear="handleSearch(true)"
                        ref="searchInput"></el-input>
                </div>
            </transition>

            <!-- 移动端菜单 -->
            <transition name="mobile-slide">
                <div v-if="mobileMenuShow" class="mobile-mask" @click="mobileMenuShow = false">
                    <div class="mobile-nav-box" @click.stop>
                        <div class="mobile-nav-item">
                            <router-link to="/front/index" @click="mobileMenuShow = false">首页</router-link>
                        </div>
                        <div class="mobile-nav-item">
                            <router-link to="/front/timeline" @click="mobileMenuShow = false">归档</router-link>
                        </div>
                        <div class="mobile-nav-item">
                            <router-link to="/front/category/all" @click="mobileMenuShow = false">分类</router-link>
                        </div>
                        <div class="mobile-nav-item">
                            <router-link to="/front/about" @click="mobileMenuShow = false">关于</router-link>
                        </div>
                        <div v-if="isAdmin" class="mobile-nav-item">
                            <router-link to="/admin/home" @click="mobileMenuShow = false">管理后台</router-link>
                        </div>
                        <div class="mobile-divider"></div>
                        <div v-if="!isLogin" class="mobile-nav-item">
                            <router-link to="/login" @click="mobileMenuShow = false">登录</router-link>
                        </div>
                        <div v-if="!isLogin" class="mobile-nav-item">
                            <router-link to="/register" @click="mobileMenuShow = false">注册</router-link>
                        </div>
                        <div v-if="isLogin" class="mobile-nav-item logout-text">
                            <span @click="handleLogout; mobileMenuShow = false">退出登录</span>
                        </div>
                    </div>
                </div>
            </transition>
        </header>

        <main class="front-main">
            <div class="main-container">
                <router-view />
            </div>
        </main>

        <footer class="front-footer">
            <div class="footer-container">
                <p>Jay.Blog</p>
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
            searchExpanded: false,
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
            try { await addVisitorRecord() } catch (err) { }
        },
        toggleSearch() {
            this.searchExpanded = !this.searchExpanded
            if (this.searchExpanded) {
                this.$nextTick(() => {
                    this.$refs.searchInput && this.$refs.searchInput.focus()
                })
            }
        },
        handleDropdownClick(cmd) {
            switch (cmd) {
                case 'profile': this.$router.push('/front/profile'); break
                case 'admin': this.$router.push('/admin/home'); break
                case 'logout': this.handleLogout(); break
            }
        },
        handleSearch(clear) {
            if (clear) this.searchKey = ''
            this.$router.push({ path: '/front/index', query: { key: this.searchKey.trim() } })
            this.searchExpanded = false
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
.front-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: var(--color-bg);
}

/* ===== 导航栏 毛玻璃 ===== */
.front-header {
    position: sticky;
    top: 0;
    z-index: 999;
    background: var(--glass-bg);
    backdrop-filter: var(--glass-blur);
    -webkit-backdrop-filter: var(--glass-blur);
    border-bottom: 1px solid var(--glass-border);
}

.header-container {
    max-width: 1200px;
    margin: 0 auto;
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 32px;
}

.header-left {
    display: flex;
    align-items: center;
}

.logo-link {
    text-decoration: none;
}

.ellipse-logo {
    width: 104px;
    height: 38px;
    line-height: 38px;
    text-align: center;
    background: var(--color-primary-gradient);
    color: #fff;
    border-radius: 999px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 0.5px;
    box-shadow: 0 2px 10px rgba(91, 79, 207, 0.18);
    transition: var(--transition-fast);
}

.ellipse-logo:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(91, 79, 207, 0.26);
}

/* ===== 导航链接 ===== */
.header-nav {
    display: flex;
    gap: 36px;
    align-items: center;
}

.nav-item {
    font-size: 15px;
    color: var(--color-text-regular);
    text-decoration: none;
    padding: 4px 0;
    position: relative;
    transition: color 0.2s ease;
    font-weight: 450;
}

.nav-item:hover {
    color: var(--color-primary);
}

.nav-item.active {
    color: var(--color-primary);
    font-weight: 500;
}

.nav-item.active::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 100%;
    height: 2px;
    background: var(--color-primary);
    border-radius: 99px;
}

/* ===== 右侧 ===== */
.header-right {
    display: flex;
    align-items: center;
    gap: 18px;
}

.search-trigger {
    width: 34px;
    height: 34px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    color: var(--color-text-muted);
    cursor: pointer;
    transition: var(--transition-fast);
}

.search-trigger:hover,
.search-trigger.active {
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
}

.search-bar {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 32px 14px;
}

.search-bar /deep/ .el-input__inner {
    background: var(--glass-bg);
    backdrop-filter: var(--glass-blur);
    -webkit-backdrop-filter: var(--glass-blur);
    border: 1px solid var(--glass-border);
    border-radius: 999px;
    height: 40px;
    padding: 0 20px;
    font-size: 14px;
    color: var(--color-text-primary);
    transition: var(--transition-fast);
}

.search-bar /deep/ .el-input__inner:focus {
    border-color: var(--color-primary-light);
    box-shadow: 0 0 0 3px rgba(91, 79, 207, 0.08);
}

/* 搜索滑入动画 */
.search-slide-enter-active {
    transition: all 0.25s ease;
}
.search-slide-leave-active {
    transition: all 0.2s ease;
}
.search-slide-enter,
.search-slide-leave-to {
    opacity: 0;
    transform: translateY(-6px);
}

/* 用户入口 */
.user-entry {
    display: flex;
    align-items: center;
}

.entry-link {
    font-size: 14px;
    color: var(--color-text-regular);
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
    transition: var(--transition-fast);
}

.entry-link:hover {
    color: var(--color-primary);
    background: var(--color-primary-light-bg);
}

.user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
    padding: 6px 12px;
    border-radius: 8px;
    transition: var(--transition-fast);
    font-size: 14px;
    color: var(--color-text-primary);
}

.user-info:hover {
    background: var(--color-primary-light-bg);
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
    display: none;
    width: 34px;
    height: 34px;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    color: var(--color-text-regular);
    cursor: pointer;
    transition: var(--transition-fast);
}

.mobile-menu-btn:hover {
    background: var(--color-primary-light-bg);
}

/* ===== 主体 ===== */
.front-main {
    flex: 1;
    padding: 40px 0;
}

.main-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 32px;
}

/* ===== 页脚 ===== */
.front-footer {
    padding: 36px 0;
    text-align: center;
    border-top: 1px solid var(--color-border);
}

.footer-container {
    color: var(--color-text-muted);
    font-size: 13px;
}

/* ===== 移动端侧边栏 ===== */
.mobile-mask {
    position: fixed;
    top: 64px;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(4px);
    -webkit-backdrop-filter: blur(4px);
    z-index: 998;
}

.mobile-nav-box {
    width: 260px;
    height: 100%;
    background: var(--glass-bg);
    backdrop-filter: var(--glass-blur);
    -webkit-backdrop-filter: var(--glass-blur);
    padding: 24px;
    box-shadow: 2px 0 20px rgba(0, 0, 0, 0.08);
}

.mobile-nav-item {
    padding: 14px 0;
    font-size: 16px;
}

.mobile-nav-item a {
    color: var(--color-text-primary);
    text-decoration: none;
}

.logout-text {
    color: var(--color-danger);
    cursor: pointer;
}

.mobile-divider {
    height: 1px;
    background: var(--color-border);
    margin: 12px 0;
}

.mobile-slide-enter-active { transition: opacity 0.25s ease; }
.mobile-slide-leave-active { transition: opacity 0.2s ease; }
.mobile-slide-enter,
.mobile-slide-leave-to { opacity: 0; }

/* ===== 响应式 ===== */
@media screen and (max-width: 768px) {
    .header-nav { display: none; }
    .header-right .user-entry,
    .header-right .user-info,
    .header-right .el-dropdown { display: none; }
    .mobile-menu-btn { display: flex; }
    .header-container { padding: 0 20px; }
    .main-container { padding: 0 20px; }
    .ellipse-logo { width: 92px; height: 34px; line-height: 34px; font-size: 14px; }
    .front-main { padding: 24px 0; }
}


</style>
