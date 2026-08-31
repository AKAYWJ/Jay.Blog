import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css' // 必须引入样式

// 全局样式（顺序：变量 → Element UI覆盖 → 页面通用样式）
import '@/styles/variables.css'
import '@/styles/element-override.css'
import '@/styles/global.css'

import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

Vue.use(mavonEditor)

Vue.use(ElementUI)



Vue.config.productionTip = false

// 刷新页面时在首次路由解析前从 localStorage 恢复的后端菜单动态注册后台路由
// 否则 F5 停在 /admin/xx 时会被通配符 '*' 提前重定向到前台
import { loadAsyncMenus } from './router'
loadAsyncMenus()

new Vue({
  router,
  store,
  render: function (h) { return h(App) }
}).$mount('#app')
