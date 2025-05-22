<template>
  <div class="register-page">
    <AppCard class="register-card" :body-style="{padding: '40px 32px 32px 32px'}" title="用户注册" subtitle="注册新账号，开启业主自治体验">
      <el-form :model="form" :rules="rules" ref="registerForm" @submit.prevent="handleRegister" class="register-form">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" size="large" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" size="large" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" size="large" />
        </el-form-item>
        <el-form-item label="验证码" prop="code" class="captcha-item">
          <el-input v-model="form.code" size="large" style="width: 120px; margin-right: 8px;" />
          <img :src="captchaUrl" @click="refreshCaptcha" class="captcha-image" title="点击刷新验证码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister" size="large" round>注册</el-button>
        </el-form-item>
      </el-form>
      <div class="form-footer">
        <el-link type="primary" @click="goLogin">已有账号？去登录</el-link>
      </div>
    </AppCard>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppCard from '../components/AppCard.vue'

const router = useRouter()
const registerForm = ref(null)
const loading = ref(false)
const captchaUrl = ref('/api/v1/user/getCaptchaImage?' + Date.now())

const form = ref({
  userName: '',
  password: '',
  confirmPassword: '',
  mobile: '',
  code: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value) => value === form.value.password, message: '两次密码不一致', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = () => {
  captchaUrl.value = '/api/v1/user/getCaptchaImage?' + Date.now()
}

const handleRegister = async () => {
  await registerForm.value.validate()
  loading.value = true
  try {
    const res = await fetch('/api/v1/user/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userName: form.value.userName,
        password: form.value.password,
        mobile: form.value.mobile,
        code: form.value.code
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.replace('/login')
    } else {
      ElMessage.error(data.message || '注册失败')
      refreshCaptcha()
    }
  } catch (e) {
    ElMessage.error('网络错误')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

const goLogin = () => {
  router.replace('/login')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
.register-card {
  width: 100%;
  max-width: 420px;
  margin: 80px auto;
}
.captcha-image {
  height: 40px;
  border-radius: var(--app-border-radius-sm);
  cursor: pointer;
}
.form-footer {
  text-align: right;
  margin-top: 12px;
}
</style> 