<template>
  <div class="app-layout">
    <!-- 顶部导航 -->
    <el-header class="app-header">
      <div class="header-container">
        <div class="header-left">
          <router-link to="/" class="logo-wrapper">
            <img src="https://dummyimage.com/36x36/4f8fff/fff.png&text=LOGO" alt="业主投票系统" class="logo-img" />
            <h1 class="logo-text">业主投票系统</h1>
          </router-link>
          
          <!-- 主导航 -->
          <el-menu
            mode="horizontal"
            :ellipsis="false"
            :router="true"
            class="main-nav"
            background-color="transparent"
            text-color="#fff"
            active-text-color="#fff"
          >
            <el-menu-item index="/votes">投票</el-menu-item>
            <el-menu-item index="/announcements">公告</el-menu-item>
            <el-menu-item index="/suggestions">建议</el-menu-item>
          </el-menu>
        </div>
        
        <div class="header-right">
          <!-- 用户信息 -->
          <el-dropdown v-if="user && user.id" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" class="mr-sm">{{ userInitials }}</el-avatar>
              <span class="username">{{ user.username || '用户' }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/personal')">
                  <el-icon><user /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><switch-button /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <!-- 未登录状态显示登录按钮 -->
          <el-button v-else type="primary" @click="$router.push('/login')" class="login-btn" plain>
            登录 / 注册
          </el-button>
        </div>
      </div>
    </el-header>
    
    <!-- 内容区域 -->
    <el-main class="app-main">
      <div class="main-container">
        <slot></slot>
      </div>
    </el-main>
    
    <!-- 页脚 -->
    <el-footer class="app-footer" height="auto">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>关于我们</h4>
            <p>业主投票系统致力于提升社区治理效率，促进业主参与度</p>
          </div>
          <div class="footer-section">
            <h4>联系方式</h4>
            <p>邮箱: contact@example.com</p>
            <p>电话: 400-123-4567</p>
          </div>
        </div>
        <div class="footer-bottom">
          <span>© {{ new Date().getFullYear() }} 业主投票系统 - 让小区治理更高效、更透明、更有温度</span>
        </div>
      </div>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useUserStore } from '../store/user'
import { ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const userInitials = computed(() => {
  if (!user.value || !user.value.username) return '用'
  return user.value.username.substring(0, 1)
})

const logout = () => {
  userStore.logout()
  router.replace('/login')
}
</script>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: var(--app-header-background);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 0;
  height: 60px;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--app-spacing-md);
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
}

.logo-wrapper {
  display: flex;
  align-items: center;
  text-decoration: none;
  margin-right: var(--app-spacing-lg);
}

.logo-img {
  width: 36px;
  height: 36px;
  margin-right: var(--app-spacing-sm);
}

.logo-text {
  font-size: var(--app-font-size-lg);
  font-weight: var(--app-font-weight-bold);
  color: white;
  margin: 0;
}

.main-nav {
  display: flex;
  border-bottom: none;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: var(--app-spacing-xs) var(--app-spacing-sm);
  border-radius: var(--app-border-radius-sm);
  transition: background-color 0.2s;
  color: white;
}

.user-info:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.username {
  margin-right: var(--app-spacing-xs);
  font-weight: var(--app-font-weight-medium);
}

.login-btn {
  border-color: white;
  color: white;
}

.app-main {
  flex: 1;
  padding: var(--app-spacing-lg) var(--app-spacing-md);
  background-color: var(--app-body-background);
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
}

.app-footer {
  background-color: #304156;
  color: #dcdfe6;
  padding: var(--app-spacing-xl) var(--app-spacing-md);
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  gap: var(--app-spacing-xl);
  margin-bottom: var(--app-spacing-xl);
}

.footer-section {
  flex: 1;
  min-width: 200px;
}

.footer-section h4 {
  font-size: var(--app-font-size-md);
  font-weight: var(--app-font-weight-bold);
  color: white;
  margin-bottom: var(--app-spacing-md);
}

.footer-section p {
  margin-bottom: var(--app-spacing-sm);
  color: #a0a9b8;
}

.footer-bottom {
  padding-top: var(--app-spacing-md);
  border-top: 1px solid #485366;
  text-align: center;
  color: #909399;
  font-size: var(--app-font-size-sm);
}

@media (max-width: 768px) {
  .logo-text, .main-nav {
    display: none;
  }
  
  .header-container {
    padding: 0 var(--app-spacing-sm);
  }
  
  .app-main {
    padding: var(--app-spacing-md) var(--app-spacing-sm);
  }
  
  .footer-content {
    flex-direction: column;
    gap: var(--app-spacing-lg);
  }
}

/* 覆盖 Element Plus 的一些样式，使其适应暗色导航 */
:deep(.el-menu--horizontal > .el-menu-item) {
  height: 60px;
  line-height: 60px;
  border-bottom: none;
  font-size: var(--app-font-size-base);
  padding: 0 var(--app-spacing-md);
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  border-bottom: 2px solid white;
  font-weight: var(--app-font-weight-bold);
  background-color: rgba(255, 255, 255, 0.1) !important;
}

:deep(.el-menu--horizontal > .el-menu-item:not(.is-disabled):focus, 
       .el-menu--horizontal > .el-menu-item:not(.is-disabled):hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
}
</style> 