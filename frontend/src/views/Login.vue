<template>
  <div class="login-page">
    <AppCard class="login-card" :body-style="{padding: '40px 32px 32px 32px'}">
      <div class="login-header">
        <div class="login-logo">
          <img src="https://dummyimage.com/48x48/4f8fff/fff.png&text=LOGO" alt="业主投票系统" />
        </div>
        <h1 class="login-title">业主投票系统</h1>
        <p class="login-subtitle">社区共治，从业主参与开始</p>
      </div>
      <div class="login-switch">
        <el-radio-group v-model="loginType" size="small">
          <el-radio-button label="owner">业主登录</el-radio-button>
          <el-radio-button label="admin">管理员登录</el-radio-button>
        </el-radio-group>
      </div>
      <el-form :model="form" :rules="rules" ref="loginForm" @submit.prevent="handleLogin" class="login-form">
        <el-form-item v-if="loginType==='admin'" prop="userName">
          <el-input 
            v-model="form.userName" 
            placeholder="用户名" 
            prefix-icon="User"
            autocomplete="username" 
            size="large" 
          />
        </el-form-item>
        <el-form-item v-if="loginType==='owner'" prop="mobile">
          <el-input 
            v-model="form.mobile" 
            placeholder="手机号" 
            prefix-icon="Iphone"
            autocomplete="tel" 
            size="large" 
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            prefix-icon="Lock"
            autocomplete="current-password" 
            size="large" 
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            :loading="loading" 
            @click="handleLogin"
            class="submit-btn"
            size="large"
            round
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="form-footer">
        <el-link type="primary" @click="goRegister" class="register-link" v-if="loginType==='owner'">没有账号？立即注册</el-link>
      </div>
      <div class="login-tips">
        <p><el-icon class="tips-icon"><InfoFilled /></el-icon> 初次使用需完成业主身份认证</p>
      </div>
    </AppCard>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Key, InfoFilled } from '@element-plus/icons-vue'
import AppCard from '../components/AppCard.vue'

const router = useRouter()
const loginForm = ref(null)
const loading = ref(false)
const loginType = ref('owner')
const captchaUrl = ref('/api/v1/user/getCaptchaImage?' + Date.now())

const form = ref({
  userName: '',
  password: '',
  mobile: ''
})

const rules = computed(() => {
  if (loginType.value === 'admin') {
    return {
      userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }
  } else {
    return {
      mobile: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
      ],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }
  }
})

const refreshCaptcha = () => {
  captchaUrl.value = '/api/v1/user/getCaptchaImage?' + Date.now()
}

const handleLogin = async () => {
  await loginForm.value.validate()
  loading.value = true
  try {
    let url = ''
    let body = {}
    if (loginType.value === 'admin') {
      url = '/api/v1/auth/admin/login'
      body = {
        username: form.value.userName,
        password: form.value.password
      }
    } else {
      url = '/api/v1/auth/login'
      body = {
        phone: form.value.mobile,
        password: form.value.password
      }
    }
    const res = await fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    const data = await res.json()
    if (data.code === 200 && data.data && data.data.token) {
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('user', JSON.stringify(data.data.user_info || {}))
      ElMessage.success('登录成功')
      if (loginType.value === 'admin') {
        router.replace('/admin/dashboard')
      } else {
        router.replace('/votes')
      }
    } else {
      ElMessage.error(data.message || '登录失败')
      refreshCaptcha()
    }
  } catch (e) {
    ElMessage.error('网络错误')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

const goRegister = () => {
  router.replace('/register')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
.login-card {
  width: 100%;
  max-width: 420px;
  margin: 80px auto;
}
.login-header {
  text-align: center;
  margin-bottom: var(--app-spacing-xl);
}
.login-logo {
  margin-bottom: var(--app-spacing-md);
}
.login-logo img {
  width: 48px;
  height: 48px;
}
.login-title {
  font-size: var(--app-font-size-xxl);
  color: var(--el-color-text-primary);
  margin-bottom: var(--app-spacing-sm);
}
.login-subtitle {
  font-size: var(--app-font-size-base);
  color: var(--el-color-text-secondary);
}
.login-form {
  margin-bottom: var(--app-spacing-md);
}
.captcha-item {
  display: flex;
  gap: var(--app-spacing-sm);
}
.captcha-image {
  height: 40px;
  border-radius: var(--app-border-radius-sm);
  cursor: pointer;
}
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: var(--app-font-size-md);
  font-weight: var(--app-font-weight-medium);
}
.form-footer {
  text-align: right;
  margin-bottom: var(--app-spacing-lg);
}
.register-link {
  font-size: var(--app-font-size-base);
  font-weight: var(--app-font-weight-medium);
}
.login-tips {
  background-color: var(--el-color-primary-light-9);
  padding: var(--app-spacing-md);
  border-radius: var(--app-border-radius-sm);
  font-size: var(--app-font-size-sm);
  color: var(--el-color-text-secondary);
}
.tips-icon {
  color: var(--el-color-primary);
  margin-right: var(--app-spacing-xs);
}
.login-switch {
  display: flex;
  justify-content: center;
  margin-bottom: 18px;
}
@media screen and (max-width: 768px) {
  .login-card {
    padding: var(--app-spacing-md);
    margin: 32px auto;
  }
}
</style> 