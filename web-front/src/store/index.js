import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  // ========== 状态存储 ==========
  state: {
    token: localStorage.getItem('token') || '',
    // 新增phone字段
    userInfo: {
      userId: localStorage.getItem('userId') || '',
      username: localStorage.getItem('username') || '',
      roleId: localStorage.getItem('roleId') || '',
      phone: localStorage.getItem('phone') || ''
    },
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]'),
    menus: JSON.parse(localStorage.getItem('menus') || '[]')
  },

  // ========== 派生状态 ==========
  getters: {
    isLoggedIn: state => !!state.token,
    getToken: state => state.token,
    getUserId: state => state.userInfo.userId,
    getUsername: state => state.userInfo.username,
    getRoleId: state => state.userInfo.roleId,
    // 新增获取手机号
    getUserPhone: state => state.userInfo.phone,
    isAdmin: state => state.userInfo.roleId === '1',

    getPermissions: state => state.permissions,
    getMenus: state => state.menus,
    hasPermission: (state) => (permissionCode) => {
      return state.permissions.includes(permissionCode)
    }
  },

  // ========== 同步修改状态 ==========
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },

    // 接收phone并存入本地存储
    SET_USER_INFO(state, userInfo) {
      state.userInfo = {
        userId: userInfo.userId,
        username: userInfo.username,
        roleId: userInfo.roleId,
        phone: userInfo.phone || ''
      }
      localStorage.setItem('userId', userInfo.userId)
      localStorage.setItem('username', userInfo.username)
      localStorage.setItem('roleId', userInfo.roleId)
      // 持久化手机号
      localStorage.setItem('phone', userInfo.phone || '')
    },

    SET_PERMISSIONS(state, permissions) {
      state.permissions = permissions
      localStorage.setItem('permissions', JSON.stringify(permissions))
    },

    SET_MENUS(state, menus) {
      state.menus = menus
      localStorage.setItem('menus', JSON.stringify(menus))
    },

    CLEAR_USER_STATE(state) {
      state.token = ''
      state.userInfo = {
        userId: '',
        username: '',
        roleId: '',
        phone: ''
      }
      state.permissions = []
      state.menus = []

      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('roleId')
      localStorage.removeItem('phone') // 清空手机号缓存
      localStorage.removeItem('permissions')
      localStorage.removeItem('menus')
    }
  },

  // ========== 异步操作 ==========
  actions: {
    /**
     * 登录成功保存用户状态，接收后端返回phone
     * @param {Object} userData { token, userId, username, roleId, phone, menus }
     */
    login({ commit }, userData) {
      commit('SET_TOKEN', userData.token)
      // 传入phone给mutation
      commit('SET_USER_INFO', {
        userId: userData.userId,
        username: userData.username,
        roleId: userData.roleId,
        phone: userData.phone
      })
      commit('SET_MENUS', userData.menus)
    },

    logout({ commit }) {
      commit('CLEAR_USER_STATE')
    }
  },

  modules: {
  }
})