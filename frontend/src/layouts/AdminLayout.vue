<template>
  <div class="admin-layout">
    <el-container class="admin-container">
      <!-- 侧边栏 -->
      <el-aside width="240px" class="aside-menu">
        <div class="logo-container">
          <img src="https://dummyimage.com/36x36/4f8fff/fff.png&text=LOGO" alt="业主投票系统" class="logo-img" />
          <h1 class="logo-title">业主投票系统</h1>
        </div>
        
        <el-scrollbar>
          <el-menu
            :default-active="activeMenu"
            class="el-menu-vertical"
            router
          >
            <el-menu-item index="/admin/dashboard">
              <el-icon><Monitor /></el-icon>
              <span>控制台</span>
            </el-menu-item>

            <el-menu-item index="/admin/votes">
              <el-icon><Ticket /></el-icon>
              <span>投票管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/announcements">
              <el-icon><Bell /></el-icon>
              <span>公告管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/owners">
              <el-icon><UserFilled /></el-icon>
              <span>业主管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/suggestions">
              <el-icon><ChatDotSquare /></el-icon>
              <span>建议管理</span>
            </el-menu-item>

            <el-menu-item index="/admin/logs">
              <el-icon><Document /></el-icon>
              <span>日志管理</span>
            </el-menu-item>
          </el-menu>
        </el-scrollbar>
      </el-aside>

      <!-- 主内容区 -->
      <el-container class="main-container">
        <!-- 头部 -->
        <el-header class="admin-header">
          <div class="header-left">
            <!-- 面包屑 -->
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentMenuTitle">{{ currentMenuTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <!-- 用户信息 -->
            <el-dropdown trigger="click">
              <div class="admin-user">
                <el-avatar :size="32" class="mr-sm">{{ userInitials }}</el-avatar>
                <span class="username">{{ user.username || '管理员' }}</span>
                <el-icon class="el-icon--right"><arrow-down /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="$router.push('/')">
                    <el-icon><HomeFilled /></el-icon>返回前台
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="logout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 内容区域 -->
        <el-main class="admin-main">
          <div class="page-header" v-if="currentMenuTitle">
            <div class="page-title">{{ currentMenuTitle }}</div>
            <div class="page-description" v-if="currentMenuDescription">{{ currentMenuDescription }}</div>
          </div>
          
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
        
        <!-- 页脚 -->
        <el-footer class="admin-footer" height="40px">
          <div>业主投票系统 &copy; {{ new Date().getFullYear() }} - 管理后台</div>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useUserStore } from '../store/user'
import {
  Monitor, Ticket, Bell, UserFilled, ChatDotSquare, Document,
  ArrowDown, HomeFilled, SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const userInitials = computed(() => {
  if (!user.value || !user.value.username) return '管'
  return user.value.username.substring(0, 1)
})

const activeMenu = computed(() => route.path)

const menuMap = {
  '/admin/dashboard': {
    title: '控制台',
    description: '系统概览与数据统计'
  },
  '/admin/votes': {
    title: '投票管理',
    description: '管理社区投票事项'
  },
  '/admin/announcements': {
    title: '公告管理',
    description: '发布与管理社区公告'
  },
  '/admin/owners': {
    title: '业主管理',
    description: '业主资料与认证管理'
  },
  '/admin/suggestions': {
    title: '建议管理',
    description: '业主意见与建议管理'
  },
  '/admin/logs': {
    title: '日志管理',
    description: '系统操作日志查询'
  }
}

const currentMenuTitle = computed(() => {
  return menuMap[route.path]?.title || ''
})

const currentMenuDescription = computed(() => {
  return menuMap[route.path]?.description || ''
})

const logout = () => {
  userStore.logout()
  router.replace('/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.admin-container {
  min-height: 100vh;
}

.aside-menu {
  background-color: #304156;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  position: relative;
  overflow: hidden;
  transition: width 0.3s;
  z-index: 10;
}

.logo-container {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 20px;
  border-bottom: 1px solid #1f2d3d;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: var(--app-spacing-xs);
}

.logo-title {
  color: white;
  font-size: var(--app-font-size-lg);
  margin: 0;
  white-space: nowrap;
}

.el-menu-vertical {
  border-right: none;
  background-color: transparent;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid var(--el-color-border-light);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.admin-user {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: var(--app-spacing-xs) var(--app-spacing-sm);
  border-radius: var(--app-border-radius-sm);
  transition: background-color 0.2s;
}

.admin-user:hover {
  background-color: var(--el-color-primary-light-9);
}

.username {
  margin-right: var(--app-spacing-xs);
  font-weight: var(--app-font-weight-medium);
  color: var(--el-color-text-primary);
}

.admin-main {
  padding: var(--app-spacing-lg);
  background-color: var(--app-body-background);
}

.page-header {
  margin-bottom: var(--app-spacing-lg);
}

.page-title {
  font-size: var(--app-font-size-xxl);
  font-weight: var(--app-font-weight-bold);
  color: var(--el-color-text-primary);
  margin-bottom: var(--app-spacing-sm);
  line-height: 1.2;
}

.page-description {
  color: var(--el-color-text-secondary);
}

.admin-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--el-color-text-secondary);
  font-size: var(--app-font-size-sm);
  background-color: white;
  border-top: 1px solid var(--el-color-border-light);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 覆盖Element Plus样式，适应暗色侧边栏 */
:deep(.el-menu) {
  --el-menu-bg-color: #304156 !important;
  --el-menu-text-color: #bfcbd9 !important;
  --el-menu-hover-bg-color: #263445 !important;
  --el-menu-active-color: #409eff !important;
}

:deep(.el-menu-item.is-active) {
  background-color: #263445 !important;
}

:deep(.el-menu-item:hover) {
  background-color: #263445 !important;
}

:deep(.el-scrollbar__view) {
  height: 100%;
}

@media (max-width: 768px) {
  .aside-menu {
    position: fixed;
    height: 100%;
    transform: translateX(-100%);
    transition: transform 0.3s;
  }
  
  .aside-menu.show {
    transform: translateX(0);
  }
}
</style> 