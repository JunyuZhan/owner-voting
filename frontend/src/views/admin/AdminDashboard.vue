<template>
  <div class="admin-dashboard">
    <!-- 当前管理小区信息 -->
    <el-card v-if="!userStore.isSystemAdmin && currentCommunity" class="community-info">
      <template #header>
        <h3>当前管理小区</h3>
      </template>
      <div class="community-details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="小区名称">{{ currentCommunity.name }}</el-descriptions-item>
          <el-descriptions-item label="小区地址">{{ currentCommunity.address }}</el-descriptions-item>
          <el-descriptions-item label="小区描述" :span="2">
            {{ currentCommunity.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>

    <!-- 权限提示 -->
    <el-alert 
      v-if="userStore.isSystemAdmin"
      title="系统管理员权限" 
      type="success" 
      :closable="false"
      class="permission-alert"
    >
      您拥有系统管理员权限，可以管理所有小区的数据
    </el-alert>

    <el-alert 
      v-else
      :title="`小区管理员权限 - ${userStore.currentCommunityName}`" 
      type="info" 
      :closable="false"
      class="permission-alert"
    >
      您只能管理当前绑定小区的数据
    </el-alert>

    <!-- 数据统计 -->
    <el-card>
      <template #header>
        <h3>数据统计</h3>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="业主总数" :value="stats.ownerCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已认证业主" :value="stats.verifiedOwnerCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="投票总数" :value="stats.voteCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="建议总数" :value="stats.suggestionCount" />
        </el-col>
      </el-row>
      
      <el-divider />
      
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="公告总数" :value="stats.announcementCount" />
        </el-col>
        <el-col :span="6" v-if="userStore.isSystemAdmin">
          <el-statistic title="小区总数" :value="stats.communityCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="活跃投票" :value="stats.activeVoteCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="今日新增业主" :value="stats.todayNewOwners" />
        </el-col>
      </el-row>
    </el-card>

    <!-- 快捷操作 -->
    <el-card>
      <template #header>
        <h3>快捷操作</h3>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-button type="primary" size="large" @click="$router.push('/admin/announcements')">
            <el-icon><Bell /></el-icon>
            管理公告
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="success" size="large" @click="$router.push('/admin/votes')">
            <el-icon><Select /></el-icon>
            管理投票
          </el-button>
        </el-col>
        <el-col :span="6" v-if="userStore.isSystemAdmin">
          <el-button type="warning" size="large" @click="$router.push('/admin/communities')">
            <el-icon><OfficeBuilding /></el-icon>
            小区管理
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-button type="info" size="large" @click="$router.push('/admin/owners')">
            <el-icon><User /></el-icon>
            业主管理
          </el-button>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bell, Select, OfficeBuilding, User } from '@element-plus/icons-vue'
import { useUserStore } from '../../store/user'
import { getStatistics } from '../../api/admin'
import { getCurrentCommunity } from '../../api/community'

const userStore = useUserStore()
const currentCommunity = ref(null)

const stats = ref({
  ownerCount: 0,
  verifiedOwnerCount: 0,
  voteCount: 0,
  suggestionCount: 0,
  announcementCount: 0,
  communityCount: 0,
  activeVoteCount: 0,
  todayNewOwners: 0
})

const fetchStats = async () => {
  try {
    const res = await getStatistics()
    Object.assign(stats.value, res.data || res)
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const fetchCurrentCommunity = async () => {
  if (!userStore.isSystemAdmin) {
    try {
      const response = await getCurrentCommunity()
      if (response.data && response.data.community) {
        currentCommunity.value = response.data.community
        userStore.setCurrentCommunity(response.data.community)
      }
    } catch (error) {
      console.error('获取当前小区信息失败:', error)
    }
  }
}

onMounted(() => {
  fetchStats()
  fetchCurrentCommunity()
})
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 20px auto;
  padding: 20px;
}

.community-info {
  margin-bottom: 20px;
}

.community-details {
  margin-top: 10px;
}

.permission-alert {
  margin-bottom: 20px;
}

.el-card {
  margin-bottom: 20px;
}

.el-button {
  width: 100%;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.el-icon {
  font-size: 20px;
}
</style> 