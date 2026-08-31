<template>
    <div class="front-layout">
        <!-- 顶部导航栏 毛玻璃悬浮 -->
        <header class="front-header">
            <div class="header-container">
                <div class="header-left">
                    <NuxtLink to="/front" class="logo-link">
                        <div class="ellipse-logo">Jay.Blog</div>
                    </NuxtLink>
                </div>

                <nav class="header-nav">
                    <NuxtLink to="/front" class="nav-item" exact-active-class="active">{{ t('nav.home') }}</NuxtLink>
                    <NuxtLink to="/front/timeline" class="nav-item" active-class="active">{{ t('nav.archive') }}</NuxtLink>
                    <NuxtLink to="/front/category/all" class="nav-item" active-class="active">{{ t('nav.category') }}</NuxtLink>
                    <NuxtLink to="/front/about" class="nav-item" active-class="active">{{ t('nav.about') }}</NuxtLink>
                    <NuxtLink to="/front/profile" class="nav-item" active-class="active">{{ t('nav.profile') }}</NuxtLink>
                </nav>

                <div class="header-right">
                    <div class="search-trigger" :class="{ active: searchExpanded }" @click="toggleSearch" :title="t('common.search') + ' (Ctrl+K)'">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" stroke-linecap="round">
                            <circle cx="11" cy="11" r="8"></circle>
                            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                        </svg>
                        <span class="search-kbd">⌘K</span>
                    </div>

                    <!-- 主题切换（太阳/月亮） -->
                    <ThemeToggle />

                    <!-- 语言切换（中/EN） -->
                    <LanguageSwitch />

                    <div class="user-area">
                        <ClientOnly>
                            <div v-if="!isLogin" class="user-entry">
                                <span class="entry-link" @click="navigateTo('/login')">{{ t('nav.login') }}</span>
                            </div>
                            <el-dropdown v-else trigger="click" @command="handleDropdownClick">
                                <div class="user-info">
                                    <span class="username">{{ userName }}</span>
                                </div>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <el-dropdown-item command="profile">{{ t('nav.profile') }}</el-dropdown-item>
                                        <el-dropdown-item v-if="isAdmin" command="admin">{{ t('nav.admin') }}</el-dropdown-item>
                                        <el-dropdown-item divided command="logout">{{ t('nav.logout') }}</el-dropdown-item>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                            <template #fallback>
                                <div class="user-entry">
                                    <span class="entry-link" style="opacity:0">{{ t('nav.login') }}</span>
                                </div>
                            </template>
                        </ClientOnly>
                    </div>

                    <div class="mobile-menu-btn" @click="mobileMenuShow = !mobileMenuShow">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" stroke-linecap="round">
                            <line x1="3" y1="6" x2="21" y2="6"></line>
                            <line x1="3" y1="12" x2="21" y2="12"></line>
                            <line x1="3" y1="18" x2="21" y2="18"></line>
                        </svg>
                    </div>
                </div>
            </div>

            <transition name="search-slide">
                <div v-if="searchExpanded" class="search-bar">
                    <el-input v-model="searchKey" placeholder="搜索文章..." clearable
                        @keyup.enter.native="handleSearch(false)" @clear="handleSearch(true)"
                        ref="searchInput"></el-input>
                </div>
            </transition>

            <transition name="mobile-slide">
                <div v-if="mobileMenuShow" class="mobile-mask" @click="mobileMenuShow = false">
                    <div class="mobile-nav-box" @click.stop>
                        <div class="mobile-nav-item">
                            <NuxtLink to="/front" @click="mobileMenuShow = false">{{ t('nav.home') }}</NuxtLink>
                        </div>
                        <div class="mobile-nav-item">
                            <NuxtLink to="/front/timeline" @click="mobileMenuShow = false">{{ t('nav.archive') }}</NuxtLink>
                        </div>
                        <div class="mobile-nav-item">
                            <NuxtLink to="/front/category/all" @click="mobileMenuShow = false">{{ t('nav.category') }}</NuxtLink>
                        </div>
                        <div class="mobile-nav-item">
                            <NuxtLink to="/front/about" @click="mobileMenuShow = false">{{ t('nav.about') }}</NuxtLink>
                        </div>
                        <ClientOnly>
                            <div v-if="isAdmin" class="mobile-nav-item">
                                <a class="mobile-nav-link" @click.prevent="openAdminNewTab">{{ t('nav.admin') }}</a>
                            </div>
                            <div class="mobile-divider"></div>
                            <div v-if="!isLogin" class="mobile-nav-item">
                                <NuxtLink to="/login" @click="mobileMenuShow = false">{{ t('nav.login') }}</NuxtLink>
                            </div>
                            <div v-if="!isLogin" class="mobile-nav-item">
                                <NuxtLink to="/register" @click="mobileMenuShow = false">{{ t('nav.register') }}</NuxtLink>
                            </div>
                            <div v-if="isLogin" class="mobile-nav-item logout-text">
                                <span @click="handleLogout; mobileMenuShow = false">{{ t('nav.logout') }}</span>
                            </div>
                        </ClientOnly>
                    </div>
                </div>
            </transition>
        </header>

        <!-- 页面插槽：子页面内容渲染在这里 -->
        <main class="front-main">
            <div class="main-container">
                <slot />
            </div>
        </main>

        <BackToTop />

        <footer class="front-footer">
            <div class="footer-container">
                <p>Jay.Blog</p>
            </div>
        </footer>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { navigateTo } from 'nuxt/app'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '~/stores/store'
import ThemeToggle from '~/components/ThemeToggle.vue'
import LanguageSwitch from '~/components/LanguageSwitch.vue'
// import { addVisitorRecord } from '~/api/visitor'

const userStore = useUserStore()
const { t } = useI18n()

const searchKey = ref('')
const searchExpanded = ref(false)
const mobileMenuShow = ref(false)
const searchInput = ref(null)

const isLogin = computed(() => userStore.isLoggedIn)
const userName = computed(() => userStore.getUsername)
const isAdmin = computed(() => {
    const rid = userStore.getRoleId
    return String(rid) === '1' || String(rid) === '2'
})

// onMounted(async () => {
//   try {
//     await addVisitorRecord()
//   } catch (err) {
//     console.log('访客记录异常', err)
//   }
// })

const toggleSearch = () => {
    searchExpanded.value = !searchExpanded.value
    if (searchExpanded.value) {
        nextTick(() => searchInput.value?.focus())
    }
}

// 监听全局快捷键（Ctrl/Cmd+K 聚焦搜索；Esc 关闭）
onMounted(() => {
    window.addEventListener('shortcut:search', toggleSearch)
    window.addEventListener('shortcut:escape', () => {
        if (searchExpanded.value) {
            searchExpanded.value = false
            searchKey.value = ''
        }
    })
})

onBeforeUnmount(() => {
    window.removeEventListener('shortcut:search', toggleSearch)
})

const handleDropdownClick = (cmd) => {
    switch (cmd) {
        case 'profile': navigateTo('/front/profile'); break
        case 'admin': window.open('/admin', '_blank'); break
        case 'logout': handleLogout(); break
    }
}

const openAdminNewTab = () => {
    mobileMenuShow.value = false
    window.open('/admin', '_blank')
}

const handleSearch = (clear) => {
    if (clear) searchKey.value = ''
    navigateTo({ path: '/front', query: { key: searchKey.value.trim() } })
    searchExpanded.value = false
}

const handleLogout = () => {
    ElMessageBox.confirm(t('nav.logout') + '?', t('common.confirm'), {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'info'
    }).then(() => {
        userStore.logout()
        navigateTo('/login')
    }).catch(() => { })
}
</script>

<style scoped>
.front-layout {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: var(--color-bg);
}

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
    gap: 4px;
}

.search-trigger:hover,
.search-trigger.active {
    background: var(--color-primary-light-bg);
    color: var(--color-primary);
}

.search-kbd {
    display: none;
    font-size: 11px;
    font-family: 'JetBrains Mono', monospace;
    color: var(--color-text-muted);
    padding: 1px 5px;
    border: 1px solid var(--color-border);
    border-radius: 4px;
    line-height: 1;
}

@media (min-width: 1024px) {
    .search-trigger {
        width: auto;
        padding: 0 10px;
        border: 1px solid var(--color-border);
        border-radius: 999px;
    }
    .search-kbd {
        display: inline-block;
    }
}

.search-bar {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 32px 14px;
}

.search-bar :deep(.el-input__inner) {
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

.search-bar :deep(.el-input__inner):focus {
    border-color: var(--color-primary-light);
    box-shadow: 0 0 0 3px rgba(91, 79, 207, 0.08);
}

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

.front-main {
    flex: 1;
    padding: 40px 0;
}

.main-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 32px;
}

.front-footer {
    padding: 36px 0;
    text-align: center;
    border-top: 1px solid var(--color-border);
}

.footer-container {
    color: var(--color-text-muted);
    font-size: 13px;
}

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

.mobile-slide-enter-active {
    transition: opacity 0.25s ease;
}

.mobile-slide-leave-active {
    transition: opacity 0.2s ease;
}

.mobile-slide-enter,
.mobile-slide-leave-to {
    opacity: 0;
}

@media screen and (max-width: 768px) {
    .header-nav {
        display: none;
    }

    .header-right .user-entry,
    .header-right .user-info,
    .header-right .el-dropdown {
        display: none;
    }

    .mobile-menu-btn {
        display: flex;
    }

    .header-container {
        padding: 0 16px;
        gap: 8px;
    }

    /* 移动端缩小搜索触发器避免拥挤 */
    .search-trigger {
        width: 30px;
        height: 30px;
    }

    .mobile-menu-btn {
        width: 30px;
        height: 30px;
    }

    .main-container {
        padding: 0 16px;
    }

    .ellipse-logo {
        width: 92px;
        height: 34px;
        line-height: 34px;
        font-size: 14px;
    }

    .front-main {
        padding: 20px 0;
    }

    .header-right {
        gap: 8px;
    }

    /* 移动端搜索栏更紧凑 */
    .search-bar {
        padding: 0 16px 12px;
    }
}


::v-deep .el-input__wrapper {
    box-shadow:none;
}

::v-deep .el-input__inner{

    padding: 10px;
}
</style>