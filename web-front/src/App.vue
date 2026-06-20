<template>
  <div id="app">
    <!-- 前台路由 /front/*：用FrontLayout + 内部router-view -->
    <div v-if="$route.path.startsWith('/front')">
      <router-view />
    </div>

    <!-- 后台路由 /admin/*：用后台Layout，完全独立渲染，不和前台布局产生路由竞争 -->
    <div v-else-if="$route.path.startsWith('/admin')">
      <router-view />
    </div>

    <!-- 登录、注册无布局页面 -->
    <div v-else>
      <router-view />
    </div>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  width: 100%;
  height: 100%;
}
</style>