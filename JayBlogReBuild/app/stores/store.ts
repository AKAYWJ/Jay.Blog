import { defineStore } from 'pinia'

// 用户信息类型
interface UserInfo {
  userId: string
  username: string
  roleId: string
  phone?: string
  avatar?: string
  email?: string
  age?: number
  sex?: number
}

// 登录入参类型
interface LoginPayload {
  token: string
  userId: string
  username: string
  roleId: string
  phone?: string
  avatar?: string
  email?: string
  age?: number
  sex?: number
  permissions: string[]
  menus: Array<Record<string, any>>
}

export const useUserStore = defineStore('user', {
    state: () => ({
      token: '',
      userInfo: {
        userId: '',
        username: '',
        roleId: '',
        phone: '',
        avatar: '',
        email: '',
        age: undefined,
        sex: undefined
      } as UserInfo,
      permissions: [] as string[],
      menus: [] as Array<Record<string, any>>
    }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    getToken: (state) => state.token,
    getUserId: (state) => state.userInfo.userId,
    getUsername: (state) => state.userInfo.username,
    getRoleId: (state) => state.userInfo.roleId,
    getUserPhone: (state) => state.userInfo.phone,
    getUserAvatar: (state) => state.userInfo.avatar,
    getUserEmail: (state) => state.userInfo.email,
    isAdmin: (state) => state.userInfo.roleId === '1',
    getPermissions: (state) => state.permissions,
    getMenus: (state) => state.menus,
    hasPermission: (state) => (permissionCode: string): boolean => {
      return state.permissions.includes(permissionCode)
    }
  },

  actions: {
    setToken(token: string) {
      this.token = token
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = {
        userId: userInfo.userId,
        username: userInfo.username,
        roleId: userInfo.roleId,
        phone: userInfo.phone ?? '',
        avatar: userInfo.avatar ?? '',
        email: userInfo.email ?? '',
        age: userInfo.age,
        sex: userInfo.sex
      }
    },

    setPermissions(permissions: string[]) {
      this.permissions = permissions
    },

    setMenus(menus: Array<Record<string, any>>) {
      this.menus = menus
    },

    clearUserState() {
      this.token = ''
      this.userInfo = {
        userId: '',
        username: '',
        roleId: '',
        phone: '',
        avatar: '',
        email: '',
        age: undefined,
        sex: undefined
      }
      this.permissions = []
      this.menus = []
    },

    login(userData: LoginPayload) {
      this.setToken(userData.token)
      this.setUserInfo({
        userId: userData.userId,
        username: userData.username,
        roleId: userData.roleId,
        phone: userData.phone,
        avatar: userData.avatar,
        email: userData.email,
        age: userData.age,
        sex: userData.sex
      })
      this.setPermissions(userData.permissions)
      this.setMenus(userData.menus)
    },

    logout() {
      this.clearUserState()
    }
  },

  // 最简配置，插件自动适配SSR/客户端localStorage，无需手动写storage
  persist: true
})