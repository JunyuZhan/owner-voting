import axios from 'axios';
import router from '@/router';
import { useUserStore } from '@/stores/user';

// 创建axios实例
const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 请求拦截器
axiosInstance.interceptors.request.use(
  (config) => {
    // 获取用户token
    const userStore = useUserStore();
    const token = userStore.token;
    
    // 如果有token，则添加到请求头
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    // 添加CSRF令牌
    const csrfToken = document.cookie
      .split('; ')
      .find(row => row.startsWith('XSRF-TOKEN='))
      ?.split('=')[1];
      
    if (csrfToken) {
      config.headers['X-XSRF-TOKEN'] = csrfToken;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
  (response) => {
    // 直接返回响应数据
    return response.data;
  },
  (error) => {
    // 处理错误响应
    if (error.response) {
      // 处理401未授权错误 - 清除token并跳转到登录页
      if (error.response.status === 401) {
        const userStore = useUserStore();
        
        // 避免循环跳转
        if (userStore.isLoggedIn) {
          userStore.logout();
          router.push('/login');
        }
      }
      
      // 处理403权限错误
      if (error.response.status === 403) {
        router.push('/403');
      }
    }
    
    // 传递错误到组件进行处理
    return Promise.reject(error);
  }
);

// 封装HTTP方法
export const http = {
  get: (url, params = {}, config = {}) => {
    return axiosInstance.get(url, { params, ...config });
  },
  post: (url, data = {}, config = {}) => {
    return axiosInstance.post(url, data, config);
  },
  put: (url, data = {}, config = {}) => {
    return axiosInstance.put(url, data, config);
  },
  delete: (url, config = {}) => {
    return axiosInstance.delete(url, config);
  },
  // 提供原始axios实例
  instance: axiosInstance
};

// 全局错误处理注入函数
export function setupErrorHandler(app) {
  // 添加全局属性
  app.config.globalProperties.$http = http;
  
  // 全局异常处理
  app.config.errorHandler = (err, vm, info) => {
    console.error('Vue错误:', err);
    console.error('错误信息:', info);
    
    // 获取全局错误处理组件
    const errorHandler = vm.$root.$refs.errorHandler;
    if (errorHandler && errorHandler.showError) {
      errorHandler.showError({
        title: '应用错误',
        message: err.message || '应用发生未知错误',
        details: process.env.NODE_ENV === 'development' ? `${info}\n${err.stack}` : null
      });
    }
  };
}

export default {
  install: (app) => {
    setupErrorHandler(app);
  }
}; 