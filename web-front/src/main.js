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

new Vue({
  router,
  store,
  render: function (h) { return h(App) }
}).$mount('#app')
