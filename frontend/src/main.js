import './assets/main.css'
import router from './router'
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import { createPinia } from 'pinia'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axiosPlugin from './plugins/axios'
import ErrorHandler from './components/ErrorHandler.vue'

const app = createApp(App)
app.use(router)
app.use(ElementPlus)

const pinia = createPinia()
app.use(pinia)

axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

axios.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      let errorMsg = res.message || '未知错误'
      if (res.details) {
        try {
          if (typeof res.details === 'object') {
            const details = Object.entries(res.details)
              .map(([field, msg]) => `${field}: ${msg}`)
              .join('; ')
            errorMsg = `${errorMsg} (${details})`
          } else {
            errorMsg = `${errorMsg} (${res.details})`
          }
        } catch (e) {
          console.error('处理错误详情失败', e)
        }
      }
      
      if (res.code === 401) {
        ElMessage.error('登录已过期，请重新登录')
        if (router.currentRoute.value.path !== '/login') {
          router.push('/login')
        }
      } else {
        ElMessage.error(errorMsg)
      }
      
      return Promise.reject(new Error(errorMsg))
    }
    return res
  },
  error => {
    let errorMsg = '网络连接失败'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          errorMsg = '请求参数错误'
          break
        case 401:
          errorMsg = '未授权，请登录'
          localStorage.removeItem('token')
          if (router.currentRoute.value.path !== '/login') {
            router.push('/login')
          }
          break
        case 403:
          errorMsg = '拒绝访问'
          break
        case 404:
          errorMsg = '请求的资源不存在'
          break
        case 500:
          errorMsg = '服务器内部错误'
          break
        default:
          errorMsg = `请求失败 (${error.response.status})`
      }
    } else if (error.request) {
      errorMsg = '服务器无响应'
    }
    
    ElMessage.error(errorMsg)
    return Promise.reject(error)
  }
)

app.config.globalProperties.$axios = axios

app.config.errorHandler = (err, vm, info) => {
  console.error('全局错误:', err)
  console.error('错误信息:', info)
  if (!err.message?.includes('network') && !err.message?.includes('请求失败')) {
    ElMessage.error('应用发生错误，请联系管理员')
  }
}

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(axiosPlugin)

app.component('ErrorHandler', ErrorHandler)

app.mount('#app')
