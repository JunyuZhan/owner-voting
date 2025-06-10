<template>
  <div class="community-select">
    <el-card>
      <template #header>
        <div class="header">
          <h2>选择小区</h2>
          <p>请选择您所在的小区，申请通过后即可参与投票</p>
        </div>
      </template>

      <el-row :gutter="16">
        <el-col :span="24" v-if="loading">
          <el-skeleton :rows="6" animated />
        </el-col>
        
        <el-col :span="24" v-else>
          <div class="community-grid">
            <el-card 
              v-for="community in communities" 
              :key="community.id"
              class="community-card"
              :class="{ 'approved': community.ownerStatus === 'APPROVED' }"
              shadow="hover">
              
              <div class="community-info">
                <h3>{{ community.name }}</h3>
                <p class="address">{{ community.address }}</p>
                <p class="description">{{ community.description }}</p>
                
                <div class="stats">
                  <span><i class="el-icon-user"></i> {{ community.totalOwners }} 位业主</span>
                  <span><i class="el-icon-house"></i> {{ community.totalHouses }} 套房屋</span>
                </div>
              </div>

              <div class="status-section">
                <el-tag 
                  :type="getStatusType(community.ownerStatus)" 
                  size="large">
                  {{ getStatusText(community.ownerStatus) }}
                </el-tag>

                <div class="action-buttons">
                  <el-button 
                    v-if="community.ownerStatus === 'NOT_APPLIED'"
                    type="primary" 
                    @click="showApplyDialog(community)">
                    申请加入
                  </el-button>
                  
                  <el-button 
                    v-if="community.ownerStatus === 'APPROVED'"
                    type="success" 
                    @click="enterCommunity(community)">
                    进入小区
                  </el-button>
                  
                  <el-button 
                    v-if="community.ownerStatus === 'REJECTED'"
                    type="warning" 
                    @click="showApplyDialog(community)">
                    重新申请
                  </el-button>
                  
                  <el-button 
                    v-if="community.ownerStatus === 'PENDING'"
                    disabled>
                    等待审核
                  </el-button>
                </div>
              </div>

              <div v-if="community.appliedAt" class="time-info">
                <p><small>申请时间：{{ formatTime(community.appliedAt) }}</small></p>
                <p v-if="community.reviewedAt"><small>审核时间：{{ formatTime(community.reviewedAt) }}</small></p>
                <p v-if="community.reviewRemark" class="remark"><small>审核备注：{{ community.reviewRemark }}</small></p>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 申请对话框 -->
    <el-dialog v-model="showDialog" title="申请加入小区" width="500px">
      <el-form :model="applicationForm" label-width="100px" ref="applicationFormRef" :rules="applicationRules">
        <el-form-item label="小区名称">
          <el-input v-model="selectedCommunity.name" disabled />
        </el-form-item>
        
        <el-form-item label="申请理由" prop="applicationReason">
          <el-input 
            v-model="applicationForm.applicationReason" 
            type="textarea" 
            :rows="4" 
            placeholder="请说明您申请加入该小区的原因，如：在该小区拥有房产等" />
        </el-form-item>
        
        <el-form-item label="房屋信息">
          <el-input 
            v-model="applicationForm.houseInfo" 
            placeholder="如：1栋2单元301室（可选）" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitApplication">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getCommunityList, applyCommunity } from '../api/community'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const communities = ref([])
const showDialog = ref(false)
const submitting = ref(false)
const selectedCommunity = ref({})
const applicationFormRef = ref(null)

const applicationForm = ref({
  applicationReason: '',
  houseInfo: ''
})

const applicationRules = {
  applicationReason: [
    { required: true, message: '请填写申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由至少10个字符', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const types = {
    'NOT_APPLIED': '',
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    'NOT_APPLIED': '未申请',
    'PENDING': '审核中',
    'APPROVED': '已认证',
    'REJECTED': '已拒绝'
  }
  return texts[status] || '未知状态'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleString('zh-CN')
}

const showApplyDialog = (community) => {
  selectedCommunity.value = community
  applicationForm.value = {
    applicationReason: '',
    houseInfo: ''
  }
  showDialog.value = true
}

const submitApplication = async () => {
  if (!applicationFormRef.value) return
  
  try {
    await applicationFormRef.value.validate()
    
    submitting.value = true
    
    const data = {
      ownerId: userStore.user.id,
      communityId: selectedCommunity.value.id,
      applicationReason: applicationForm.value.applicationReason,
      houseInfo: applicationForm.value.houseInfo
    }
    
    await applyCommunity(data)
    
    ElMessage.success('申请提交成功，等待管理员审核')
    showDialog.value = false
    
    // 刷新列表
    await loadCommunities()
    
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error(error.response?.data?.message || '提交申请失败')
  } finally {
    submitting.value = false
  }
}

const enterCommunity = (community) => {
  // 保存当前选中的小区到本地存储
  localStorage.setItem('currentCommunity', JSON.stringify({
    id: community.id,
    name: community.name
  }))
  
  ElMessage.success(`已进入${community.name}`)
  router.push('/home')
}

const loadCommunities = async () => {
  try {
    loading.value = true
    const response = await getCommunityList(userStore.user.id)
    communities.value = response.data
  } catch (error) {
    console.error('加载小区列表失败:', error)
    ElMessage.error('加载小区列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (!userStore.user.id) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  loadCommunities()
})
</script>

<style scoped>
.community-select {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
}

.header h2 {
  color: #303133;
  margin-bottom: 8px;
}

.header p {
  color: #606266;
  margin: 0;
}

.community-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.community-card {
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.community-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.community-card.approved {
  border-color: #67C23A;
  background: linear-gradient(135deg, #f0f9ff 0%, #e6f7f1 100%);
}

.community-info h3 {
  color: #303133;
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: 600;
}

.community-info .address {
  color: #606266;
  margin: 5px 0;
  font-size: 14px;
}

.community-info .description {
  color: #909399;
  margin: 10px 0;
  font-size: 13px;
  line-height: 1.4;
}

.stats {
  display: flex;
  gap: 15px;
  margin: 15px 0;
  font-size: 13px;
  color: #606266;
}

.stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0;
  padding-top: 15px;
  border-top: 1px solid #EBEEF5;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.time-info {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #EBEEF5;
}

.time-info p {
  margin: 2px 0;
  color: #909399;
}

.time-info .remark {
  color: #E6A23C;
}

@media (max-width: 768px) {
  .community-grid {
    grid-template-columns: 1fr;
  }
  
  .status-section {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  
  .action-buttons {
    justify-content: center;
  }
}
</style> 