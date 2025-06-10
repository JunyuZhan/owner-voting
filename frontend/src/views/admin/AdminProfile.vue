<template>
  <div class="admin-profile">
    <el-card>
      <template #header>
        <h2>个人设置</h2>
      </template>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="profile-card">
            <template #header>
              <h3>基本信息</h3>
            </template>
            
            <el-form :model="profileForm" label-width="80px">
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled />
              </el-form-item>
              <el-form-item label="姓名">
                <el-input v-model="profileForm.name" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="profileForm.phone" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" />
              </el-form-item>
              <el-form-item label="角色">
                <el-input :value="getRoleText(profileForm.role)" disabled />
              </el-form-item>
              <el-form-item label="所属小区" v-if="profileForm.community">
                <el-input :value="profileForm.community.name" disabled />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="updateProfile" :loading="profileLoading">
                  保存信息
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="password-card">
            <template #header>
              <h3>修改密码</h3>
            </template>
            
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="changePassword" :loading="passwordLoading">
                  修改密码
                </el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const passwordFormRef = ref(null)

// 个人信息表单
const profileForm = ref({
  username: '',
  name: '',
  phone: '',
  email: '',
  role: '',
  community: null
})

// 密码修改表单
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileLoading = ref(false)
const passwordLoading = ref(false)

// 密码验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 获取角色显示文本
const getRoleText = (role) => {
  switch (role) {
    case 'SYSTEM_ADMIN':
      return '系统管理员'
    case 'COMMUNITY_ADMIN':
      return '小区管理员'
    case 'OPERATOR':
      return '操作员'
    default:
      return '未知角色'
  }
}

// 初始化用户信息
const initUserProfile = () => {
  const user = userStore.user
  if (user) {
    profileForm.value = {
      username: user.username || '',
      name: user.name || '',
      phone: user.phone || '',
      email: user.email || '',
      role: user.role || '',
      community: user.community || null
    }
  }
}

// 更新个人信息
const updateProfile = async () => {
  profileLoading.value = true
  try {
    const response = await fetch('/api/v1/auth/admin/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userStore.token}`
      },
      body: JSON.stringify({
        name: profileForm.value.name,
        phone: profileForm.value.phone,
        email: profileForm.value.email
      })
    })
    
    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('个人信息更新成功')
      // 更新store中的用户信息
      await userStore.fetchUserInfo()
      initUserProfile()
    } else {
      ElMessage.error(result.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('更新个人信息失败:', error)
  } finally {
    profileLoading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }
  
  passwordLoading.value = true
  try {
    const response = await fetch('/api/v1/auth/admin/change-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${userStore.token}`
      },
      body: JSON.stringify({
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword
      })
    })
    
    const result = await response.json()
    if (result.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      resetPasswordForm()
      
      // 提示用户重新登录
      await ElMessageBox.confirm('密码已修改，需要重新登录', '提示', {
        confirmButtonText: '立即登录',
        cancelButtonText: '稍后',
        type: 'success'
      })
      
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error(result.message || '密码修改失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('修改密码失败:', error)
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }
  if (passwordFormRef.value) {
    passwordFormRef.value.clearValidate()
  }
}

onMounted(() => {
  initUserProfile()
})
</script>

<style scoped>
.admin-profile {
  padding: 20px;
}

.profile-card, .password-card {
  height: 100%;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-card {
  margin-bottom: 20px;
}
</style> 