import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    token: localStorage.getItem('token') || '',
    currentCommunity: JSON.parse(localStorage.getItem('currentCommunity') || 'null')
  }),
  
  getters: {
    // 检查是否为系统管理员
    isSystemAdmin: (state) => {
      return state.user?.role === 'SYSTEM_ADMIN'
    },
    
    // 检查是否为小区管理员
    isCommunityAdmin: (state) => {
      return state.user?.role === 'COMMUNITY_ADMIN'
    },
    
    // 检查是否为操作员
    isOperator: (state) => {
      return state.user?.role === 'OPERATOR'
    },
    
    // 获取当前用户的权限级别
    userRole: (state) => {
      return state.user?.role || 'GUEST'
    },
    
    // 获取当前小区ID
    currentCommunityId: (state) => {
      return state.currentCommunity?.id || state.user?.communityId
    },
    
    // 获取当前小区名称
    currentCommunityName: (state) => {
      return state.currentCommunity?.name || '未知小区'
    }
  },
  
  actions: {
    setUser(user, token) {
      this.user = user
      this.token = token
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('token', token)
      
      // 如果用户有关联的小区，设置当前小区信息
      if (user.community) {
        this.setCurrentCommunity(user.community)
      }
    },
    
    setCurrentCommunity(community) {
      this.currentCommunity = community
      localStorage.setItem('currentCommunity', JSON.stringify(community))
    },
    
    logout() {
      this.user = {}
      this.token = ''
      this.currentCommunity = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      localStorage.removeItem('currentCommunity')
    },
    
    init() {
      this.user = JSON.parse(localStorage.getItem('user') || '{}')
      this.token = localStorage.getItem('token') || ''
      this.currentCommunity = JSON.parse(localStorage.getItem('currentCommunity') || 'null')
    },
    
    // 更新当前小区信息
    async fetchCurrentCommunity() {
      try {
        const response = await fetch('/api/community/current', {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        if (response.ok) {
          const result = await response.json()
          if (result.code === 200 && result.data.community) {
            this.setCurrentCommunity(result.data.community)
          }
        }
      } catch (error) {
        console.error('获取当前小区信息失败:', error)
      }
    },

    // 获取当前用户信息
    async fetchUserInfo() {
      try {
        const response = await fetch('/api/v1/auth/me', {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
        if (response.ok) {
          const result = await response.json()
          if (result.code === 200) {
            this.user = result.data
            localStorage.setItem('user', JSON.stringify(result.data))
            
            // 如果用户有关联的小区，设置当前小区信息
            if (result.data.community) {
              this.setCurrentCommunity(result.data.community)
            }
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
      }
    }
  }
})