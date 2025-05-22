import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    token: localStorage.getItem('token') || ''
  }),
  actions: {
    setUser(user, token) {
      this.user = user
      this.token = token
      localStorage.setItem('user', JSON.stringify(user))
      localStorage.setItem('token', token)
    },
    logout() {
      this.user = {}
      this.token = ''
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    },
    init() {
      this.user = JSON.parse(localStorage.getItem('user') || '{}')
      this.token = localStorage.getItem('token') || ''
    }
  }
}) 