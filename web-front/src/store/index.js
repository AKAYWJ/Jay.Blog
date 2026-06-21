import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    // 新增avatar、email
    userInfo: {
      userId: localStorage.getItem('userId') || '',
      username: localStorage.getItem('username') || '',
      roleId: localStorage.getItem('roleId') || '',
      phone: localStorage.getItem('phone') || '',
      avatar: localStorage.getItem('avatar') || '',
      email: localStorage.getItem('email') || ''
    },
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]'),
    menus: JSON.parse(localStorage.getItem('menus') || '[]')
  },

  getters: {
    isLoggedIn: state => !!state.token,
    getToken: state => state.token,
    getUserId: state => state.userInfo.userId,
    getUsername: state => state.userInfo.username,
    getRoleId: state => state.userInfo.roleId,
    getUserPhone: state => state.userInfo.phone,
    // 新增获取头像、邮箱
    getUserAvatar: state => state.userInfo.avatar,
    getUserEmail: state => state.userInfo.email,
    isAdmin: state => state.userInfo.roleId === '1',
    getPermissions: state => state.permissions,
    getMenus: state => state.menus,
    hasPermission: (state) => (permissionCode) => {
      return state.permissions.includes(permissionCode)
    }
  },

  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },

    SET_USER_INFO(state, userInfo) {
      state.userInfo = {
        userId: userInfo.userId,
        username: userInfo.username,
        roleId: userInfo.roleId,
        phone: userInfo.phone || '',
        // 新增
        avatar: userInfo.avatar || '',
        email: userInfo.email || ''
      }
      localStorage.setItem('userId', userInfo.userId)
      localStorage.setItem('username', userInfo.username)
      localStorage.setItem('roleId', userInfo.roleId)
      localStorage.setItem('phone', userInfo.phone || '')
      // 持久化头像邮箱
      localStorage.setItem('avatar', userInfo.avatar || '')
      localStorage.setItem('email', userInfo.email || '')
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
        phone: '',
        avatar: '',
        email: ''
      }
      state.permissions = []
      state.menus = []

      localStorage.removeItem('token')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('roleId')
      localStorage.removeItem('phone')
      // 清除头像邮箱缓存
      localStorage.removeItem('avatar')
      localStorage.removeItem('email')
      localStorage.removeItem('permissions')
      localStorage.removeItem('menus')
    }
  },

  actions: {
    login({ commit }, userData) {
      commit('SET_TOKEN', userData.token)
      commit('SET_USER_INFO', {
        userId: userData.userId,
        username: userData.username,
        roleId: userData.roleId,
        phone: userData.phone,
        // 传递后端返回的头像邮箱
        avatar: userData.avatar,
        email: userData.email
      })
      commit('SET_PERMISSIONS', userData.permissions)
      commit('SET_MENUS', userData.menus)
    },

    logout({ commit }) {
      commit('CLEAR_USER_STATE')
    }
  },

  modules: {
  }
})