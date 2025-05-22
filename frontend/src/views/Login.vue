<template>
  <div class="login-container">
    <el-card>
      <h2>用户登录</h2>
      <el-form :model="form" :rules="rules" ref="loginForm" @submit.prevent="handleLogin">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" autocomplete="tel" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-input v-model="form.code" style="width: 120px; margin-right: 8px;" />
          <img :src="captchaUrl" @click="refreshCaptcha" style="height: 32px; cursor: pointer;" title="点击刷新验证码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div style="margin-top: 12px; text-align: right;">
        <el-link type="primary" @click="goRegister">没有账号？去注册</el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref(null)
const loading = ref(false)
const captchaUrl = ref('/api/v1/user/getCaptchaImage?' + Date.now())

const form = ref({
  userName: '',
  password: '',
  mobile: '',
  code: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = () => {
  captchaUrl.value = '/api/v1/user/getCaptchaImage?' + Date.now()
}

const handleLogin = async () => {
  await loginForm.value.validate()
  loading.value = true
  try {
    const res = await fetch('/api/v1/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(form.value)
    })
    const data = await res.json()
    if (data.code === 200 && data.data && data.data.token) {
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('user', JSON.stringify(data.data.user || {}))
      ElMessage.success('登录成功')
      router.replace('/votes')
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
.login-container {
  max-width: 400px;
  margin: 80px auto;
}
</style> 