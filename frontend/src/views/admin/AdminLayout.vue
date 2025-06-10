<template>
  <div class="admin-layout">
    <el-container style="height: 100vh">
      <el-aside width="220px">
        <!-- 用户信息 -->
        <div class="user-info">
          <el-avatar :size="50" :src="userStore.user.avatar">
            {{ userStore.user.name?.charAt(0) || 'A' }}
          </el-avatar>
          <div class="user-details">
            <div class="user-name">{{ userStore.user.name || '管理员' }}</div>
            <div class="user-role">
              <el-tag 
                :type="userStore.isSystemAdmin ? 'success' : 'info'" 
                size="small"
              >
                {{ getRoleText(userStore.userRole) }}
              </el-tag>
            </div>
            <div v-if="!userStore.isSystemAdmin && userStore.currentCommunityName" class="community-name">
              {{ userStore.currentCommunityName }}
            </div>
          </div>
        </div>
        
        <el-divider />
        
        <!-- 菜单 -->
        <el-menu 
          :default-active="activeMenu" 
          router 
          background-color="#f5f5f5"
          text-color="#333"
          active-text-color="#409EFF"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>仪表盘</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/announcements">
            <el-icon><Bell /></el-icon>
            <span>公告管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/votes">
            <el-icon><Select /></el-icon>
            <span>投票管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/owners">
            <el-icon><User /></el-icon>
            <span>业主管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/suggestions">
            <el-icon><ChatLineRound /></el-icon>
            <span>建议管理</span>
          </el-menu-item>
          
          <!-- 系统管理员专用菜单 -->
          <el-menu-item 
            v-if="userStore.isSystemAdmin" 
            index="/admin/communities"
          >
            <el-icon><OfficeBuilding /></el-icon>
            <span>小区管理</span>
          </el-menu-item>
          
          <el-menu-item 
            v-if="userStore.isSystemAdmin"
            index="/admin/ads"
          >
            <el-icon><Promotion /></el-icon>
            <span>广告管理</span>
          </el-menu-item>
          
          <el-menu-item index="/admin/logs">
            <el-icon><Document /></el-icon>
            <span>操作日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header style="background:#fff; text-align:right; padding:0 20px; border-bottom: 1px solid #e6e6e6;">
          <div class="header-content">
            <span class="header-title">业主投票管理系统</span>
            <div class="header-actions">
              <el-dropdown @command="handleCommand">
                <span class="el-dropdown-link">
                  {{ userStore.user.name || '管理员' }}
                  <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="profile">个人设置</el-dropdown-item>
                    <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  HomeFilled, 
  Bell, 
  Select, 
  User, 
  ChatLineRound, 
  OfficeBuilding, 
  Document,
  Promotion,
  ArrowDown 
} from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

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

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/admin/profile')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确认退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录')
      } catch (error) {
        // 用户取消退出
      }
      break
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.user-info {
  padding: 20px;
  text-align: center;
  background: #f9f9f9;
}

.user-details {
  margin-top: 10px;
}

.user-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 5px;
}

.user-role {
  margin-bottom: 5px;
}

.community-name {
  font-size: 12px;
  color: #666;
  margin-top: 5px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.header-actions {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  display: flex;
  align-items: center;
}

.el-aside {
  background-color: #f5f5f5;
}

.el-menu {
  border-right: none;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
}

.el-menu-item.is-active {
  background-color: #ecf5ff;
}
</style> 