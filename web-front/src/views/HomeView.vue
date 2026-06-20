<template>
  <div class="home-page">
    <!-- 顶部欢迎横幅（参考图柔和渐变风格） -->
    <div class="welcome-banner">
      <div class="banner-top">
        <div>
          <h1 class="welcome-title">欢迎回来，{{ username }} 👋</h1>
          <p class="welcome-subtitle">今天是 {{ formattedDate }}，{{ holiday }}</p>
        </div>
        <!-- 仅管理员角色显示 返回前台 按钮 -->
        <el-button v-if="roleId === '1' || roleId === '2'" icon="el-icon-house" @click="$router.push('/front/index')"
          class="front-back-btn">
          返回博客前台
        </el-button>
      </div>
    </div>

    <!-- 主内容区：大日历 + 用户信息卡片 -->
    <div class="main-content">
      <!-- 左侧：大日历模块 -->
      <div class="calendar-card">
        <div class="card-header">
          <h3 class="card-title">📅 我的日程日历</h3>
          <p class="card-desc">高亮显示今天，点击日期可查看日程</p>
        </div>
        <div class="calendar-wrapper">
          <el-calendar v-model="currentDate">
            <!-- 自定义日历头部 -->
            <template slot="header">
              <div class="calendar-header">
                <span class="calendar-month">{{ currentDate.getFullYear() }}年{{ currentDate.getMonth() + 1 }}月</span>
              </div>
            </template>
            <!-- 自定义日期单元格，高亮今天 -->
            <template slot="dateCell" slot-scope="{ date, data }">
              <div class="calendar-day" :class="{ 'is-today': isToday(data.day) }">
                {{ data.day.split('-').slice(1).join('/') }}
              </div>
            </template>
          </el-calendar>
        </div>
      </div>

      <!-- 右侧：用户信息卡片 -->
      <div class="user-info-card">
        <div class="card-header">
          <h3 class="card-title">👤 我的信息</h3>
        </div>
        <div class="user-info-content">
          <!-- 用户头像 -->
          <el-avatar :size="80" class="user-avatar">
            <img src="https://cube.elemecdn.com/0/88/03de5774d34b4cdd59a27da7a481e.jpeg" alt="用户头像">
          </el-avatar>

          <!-- 用户信息列表 -->
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ username }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">用户ID</span>
              <span class="info-value">{{ userId }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">角色</span>
              <span class="info-value">{{ roleId === '1' ? '管理员' : roleId === '2' ? '运营' : '普通用户' }}</span>
            </div>
          </div>

          <!-- 退出登录按钮（和主题风格统一） -->
          <el-button type="primary" icon="el-icon-switch-button" @click="handleLogout" class="logout-btn">
            退出登录
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import { loadAsyncMenus } from '@/router'
export default {
  name: 'HomeView',
  data() {
    return {
      // 日历当前显示的日期
      currentDate: new Date(),
      // 今天的日期（假数据，可替换为真实接口）
      today: new Date(),
      // 节日假数据（可根据实际情况修改）
      holiday: '无特殊节日，普通工作日',
      // 星期数组
      weekDays: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
    }
  }, created() {
    loadAsyncMenus()
  },
  computed: {
    // 从Vuex获取用户信息
    username() {
      return this.$store.getters.getUsername
    },
    userId() {
      return this.$store.getters.getUserId
    },
    roleId() {
      return this.$store.getters.getRoleId
    },
    // 格式化今天的日期，显示为「YYYY年MM月DD日 星期X」
    formattedDate() {
      const year = this.today.getFullYear()
      const month = this.today.getMonth() + 1
      const day = this.today.getDate()
      const weekDay = this.weekDays[this.today.getDay()]
      return `${year}年${month}月${day}日 ${weekDay}`
    }
  },
  methods: {
    // 判断日期是否为今天，用于高亮显示
    isToday(day) {
      const todayStr = `${this.today.getFullYear()}-${String(this.today.getMonth() + 1).padStart(2, '0')}-${String(this.today.getDate()).padStart(2, '0')}`
      return day === todayStr
    },
    // 退出登录逻辑
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('logout')
      }).catch(()=>{ /*取消，空函数即可*/ })
    }
  }
}
</script>

<style scoped>
/* 页面整体样式 */
.home-page {
  background-color: #f0f0ff;
  min-height: 100vh;
  padding: var(--spacing-page);
  font-family: var(--font-family);
}

/* 欢迎横幅 */
.welcome-banner {
  background: var(--welcome-bg);
  border-radius: var(--radius-xl);
  padding: var(--spacing-card-lg) 32px;
  margin-bottom: var(--spacing-gap-lg);
  color: var(--welcome-text);
}

/* 横幅左右布局：文字 + 返回前台按钮 */
.banner-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px;
}

.welcome-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.8;
}

/* 返回前台按钮样式 */
.front-back-btn {
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.4);
  color: var(--welcome-text);
}

.front-back-btn:hover {
  background: rgba(255, 255, 255, 0.35);
}

/* 主内容区 */
.main-content {
  display: flex;
  gap: var(--spacing-gap-lg);
  flex-wrap: wrap;
}

/* 日历卡片 */
.calendar-card {
  flex: 2;
  min-width: 320px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  padding: var(--spacing-card-lg);
}

/* 用户信息卡片 */
.user-info-card {
  flex: 1;
  min-width: 280px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  padding: var(--spacing-card-lg);
}

/* 通用卡片头部 */
.card-header {
  margin-bottom: var(--spacing-gap);
}

.card-title {
  font-size: var(--font-size-card-title);
  font-weight: 600;
  color: var(--welcome-text);
  margin: 0 0 4px;
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-placeholder);
  margin: 0;
}

/* 日历容器 */
.calendar-wrapper {
  width: 100%;
}

/* 自定义日历头部 */
.calendar-header {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 16px;
  font-weight: 500;
  color: var(--welcome-text);
}

/* 自定义日期单元格 */
.calendar-day {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  transition: var(--transition-fast);
}

.calendar-day.is-today {
  background-color: var(--welcome-calendar-today-bg);
  color: var(--welcome-text);
  font-weight: 600;
}

.calendar-day:hover {
  background-color: #f0f0ff;
}

/* 覆盖Element UI日历默认样式 */
/deep/ .el-calendar {
  border: none;
}

/deep/ .el-calendar__header {
  display: none;
}

/deep/ .el-calendar-day {
  height: 80px;
  padding: 4px;
}

/deep/ .el-calendar-table td,
/deep/ .el-calendar-table th {
  border: none;
}

/deep/ .el-calendar-table thead th {
  color: var(--color-text-placeholder);
  font-weight: 500;
  padding: 12px 0;
}

/* 用户信息内容 */
.user-info-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-gap-lg);
}

.user-avatar {
  border: 3px solid var(--welcome-avatar-border);
}

.info-list {
  width: 100%;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-size: var(--font-size-body);
  color: var(--color-text-placeholder);
}

.info-value {
  font-size: var(--font-size-body);
  color: var(--welcome-text);
  font-weight: 500;
}

/* 退出按钮 */
.logout-btn {
  width: 100%;
  background: var(--welcome-logout-bg);
  border: none;
  color: var(--welcome-text);
  font-weight: 500;
  border-radius: var(--radius-md);
  padding: 10px 0;
}

.logout-btn:hover {
  background: var(--welcome-logout-hover-bg);
}

/* 暗黑模式适配 */
body.dark-mode .home-page {
  background-color: var(--color-dark-bg-alt);
}

body.dark-mode .welcome-banner {
  background: linear-gradient(135deg, var(--color-dark-surface-alt) 0%, var(--color-dark-border) 100%);
  color: var(--color-dark-text-regular);
}

body.dark-mode .front-back-btn {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
}

body.dark-mode .calendar-card,
body.dark-mode .user-info-card {
  background-color: var(--color-dark-surface-alt);
}

body.dark-mode .card-title {
  color: var(--color-dark-text-regular);
}

body.dark-mode .calendar-header {
  color: var(--color-dark-text-regular);
}

body.dark-mode .calendar-day.is-today {
  background-color: var(--color-dark-border);
  color: var(--color-dark-text-regular);
}

body.dark-mode .info-label {
  color: var(--color-dark-text-muted);
}

body.dark-mode .info-value {
  color: var(--color-dark-text-regular);
}

/* 移动端适配 */
@media screen and (max-width:768px) {
  .banner-top {
    flex-direction: column;
    align-items: flex-start;
  }

  .welcome-title {
    font-size: 22px;
  }
}
</style>