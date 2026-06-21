<template>
  <div id="app">
    <svg width="0" height="0" style="position:absolute;left:-9999px;top:-9999px">
      <defs>
        <filter id="liquid-glass-filter">
          <feTurbulence type="fractalNoise" baseFrequency="0.003" numOctaves="2" seed="7" result="noise"/>
          <feGaussianBlur in="noise" stdDeviation="1.4" result="map"/>
          <feDisplacementMap in="SourceGraphic" in2="map" scale="80" xChannelSelector="R" yChannelSelector="G"/>
        </filter>
      </defs>
    </svg>
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