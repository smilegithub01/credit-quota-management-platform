import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

// 创建Store实例
const store = createStore({
  state: {
    // 用户信息
    user: {
      id: null,
      username: '',
      roles: [],
      permissions: [],
      token: null
    },
    // 系统配置
    systemConfig: {
      apiUrl: process.env.VUE_APP_API_URL || 'http://localhost:8080/api/unified-credit',
      theme: 'default',
      language: 'zh-CN'
    },
    // 当前登录状态
    isLoggedIn: false,
    // 全局加载状态
    loading: false,
    // 通知消息
    notifications: []
  },

  mutations: {
    // 设置用户信息
    SET_USER_INFO(state, userInfo) {
      state.user = { ...state.user, ...userInfo }
      state.isLoggedIn = !!userInfo.token
    },

    // 清除用户信息
    CLEAR_USER_INFO(state) {
      state.user = {
        id: null,
        username: '',
        roles: [],
        permissions: [],
        token: null
      }
      state.isLoggedIn = false
      localStorage.removeItem('token')
    },

    // 设置登录状态
    SET_LOGIN_STATUS(state, status) {
      state.isLoggedIn = status
    },

    // 设置加载状态
    SET_LOADING(state, status) {
      state.loading = status
    },

    // 添加通知
    ADD_NOTIFICATION(state, notification) {
      const id = Date.now()
      state.notifications.push({
        id,
        ...notification,
        timestamp: new Date()
      })
      
      // 自动移除通知（5秒后）
      setTimeout(() => {
        const index = state.notifications.findIndex(n => n.id === id)
        if (index !== -1) {
          state.notifications.splice(index, 1)
        }
      }, 5000)
    },

    // 移除通知
    REMOVE_NOTIFICATION(state, id) {
      const index = state.notifications.findIndex(n => n.id === id)
      if (index !== -1) {
        state.notifications.splice(index, 1)
      }
    },

    // 设置系统配置
    SET_SYSTEM_CONFIG(state, config) {
      state.systemConfig = { ...state.systemConfig, ...config }
    }
  },

  actions: {
    // 用户登录
    async login({ commit }, credentials) {
      commit('SET_LOADING', true)
      try {
        // 这里应该是实际的API调用
        // const response = await api.login(credentials)
        // const { token, user } = response.data
        
        // 模拟登录成功
        const mockUser = {
          id: 1,
          username: credentials.username,
          roles: ['admin'],
          permissions: ['read', 'write', 'admin'],
          token: 'mock-token-' + Date.now()
        }
        
        commit('SET_USER_INFO', mockUser)
        localStorage.setItem('token', mockUser.token)
        return { success: true, user: mockUser }
      } catch (error) {
        commit('ADD_NOTIFICATION', {
          type: 'error',
          message: error.message || '登录失败'
        })
        return { success: false, error: error.message }
      } finally {
        commit('SET_LOADING', false)
      }
    },

    // 用户登出
    logout({ commit }) {
      commit('CLEAR_USER_INFO')
      commit('ADD_NOTIFICATION', {
        type: 'info',
        message: '已成功退出登录'
      })
    },

    // 添加通知
    addNotification({ commit }, notification) {
      commit('ADD_NOTIFICATION', notification)
    },

    // 更新系统配置
    updateSystemConfig({ commit }, config) {
      commit('SET_SYSTEM_CONFIG', config)
    }
  },

  getters: {
    // 获取当前用户
    currentUser: state => state.user,
    
    // 检查用户是否已登录
    isLoggedIn: state => state.isLoggedIn,
    
    // 检查用户权限
    hasPermission: (state) => (permission) => {
      return state.user.permissions.includes(permission)
    },
    
    // 获取系统配置
    systemConfig: state => state.systemConfig,
    
    // 获取加载状态
    isLoading: state => state.loading,
    
    // 获取通知列表
    notifications: state => state.notifications
  },

  // 使用持久化插件保存状态
  plugins: [
    createPersistedState({
      paths: ['user', 'systemConfig']
    })
  ]
})

export default store