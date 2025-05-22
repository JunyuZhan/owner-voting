<template>
  <ErrorHandler ref="errorHandler" />
  <el-header class="global-header" v-if="$route.path !== '/'">
    <div class="logo">业主投票系统</div>
    <div class="user-info" v-if="user && user.id">
      <span style="margin-right: 16px;">{{ user.username || '用户' }}</span>
      <el-button size="small" @click="logout">退出登录</el-button>
    </div>
  </el-header>
  <router-view />
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useUserStore } from './store/user'

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const logout = () => {
  userStore.logout()
  router.replace('/login')
}
</script>

<style scoped>
.global-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
  height: 56px;
  background: #409eff;
  color: #fff;
  margin-bottom: 24px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
}
.user-info {
  display: flex;
  align-items: center;
}
</style>
