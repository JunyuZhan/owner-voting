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
  mobile: ''
})

const rules = {
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, callback) => {
      if (value !== form.value.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const refreshCaptcha = () => {
  captchaUrl.value = '/api/v1/user/getCaptchaImage?' + Date.now()
}

const handleRegister = async () => {
  await registerForm.value.validate()
  loading.value = true
  try {
    const res = await fetch('/api/owner/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: form.value.userName,
        password: form.value.password,
        phone: form.value.mobile,
        idCard: '' // 身份证号暂时为空，后续在业主认证时填写
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