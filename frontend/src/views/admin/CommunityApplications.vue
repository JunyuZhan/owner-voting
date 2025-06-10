<template>
  <div class="community-applications">
    <el-card>
      <template #header>
        <div class="header">
          <h2>小区业主申请审核</h2>
          <el-button type="primary" @click="refreshData">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </template>

      <el-table 
        :data="applications" 
        v-loading="loading"
        stripe
        style="width: 100%">
        
        <el-table-column prop="id" label="申请ID" width="80" />
        
        <el-table-column label="申请人信息" width="200">
          <template #default="scope">
            <div>
              <p><strong>{{ scope.row.owner?.name || '未知' }}</strong></p>
              <p><small>手机：{{ scope.row.owner?.phone || '未提供' }}</small></p>
              <p><small>ID：{{ scope.row.owner?.id }}</small></p>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="申请理由" min-width="250">
          <template #default="scope">
            <div class="reason-cell">
              <p>{{ scope.row.applicationReason }}</p>
              <el-tag v-if="scope.row.houseInfo" size="small" type="info">
                房屋：{{ scope.row.houseInfo }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="申请时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.appliedAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                v-if="scope.row.status === 'PENDING'"
                type="success" 
                size="small"
                @click="handleReviewApplication(scope.row, 'APPROVED')">
                通过
              </el-button>
              
              <el-button 
                v-if="scope.row.status === 'PENDING'"
                type="danger" 
                size="small"
                @click="showRejectDialog(scope.row)">
                拒绝
              </el-button>
              
              <el-button 
                type="info" 
                size="small"
                @click="viewDetails(scope.row)">
                详情
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && applications.length === 0" class="empty-state">
        <el-empty description="暂无待审核申请" />
      </div>
    </el-card>

    <!-- 拒绝申请对话框 -->
    <el-dialog v-model="showRejectDialogFlag" title="拒绝申请" width="500px">
      <el-form :model="rejectForm" label-width="100px" ref="rejectFormRef" :rules="rejectRules">
        <el-form-item label="申请人">
          <el-input :value="selectedApplication.owner?.name" disabled />
        </el-form-item>
        
        <el-form-item label="拒绝原因" prop="reviewRemark">
          <el-input 
            v-model="rejectForm.reviewRemark" 
            type="textarea" 
            :rows="4" 
            placeholder="请说明拒绝原因" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showRejectDialogFlag = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog v-model="showDetailsFlag" title="申请详情" width="600px">
      <div class="application-details" v-if="selectedApplication">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请ID">{{ selectedApplication.id }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ selectedApplication.owner?.name }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedApplication.owner?.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证">{{ selectedApplication.owner?.idCard || '未提供' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间" :span="2">{{ formatTime(selectedApplication.appliedAt) }}</el-descriptions-item>
          <el-descriptions-item label="申请理由" :span="2">{{ selectedApplication.applicationReason }}</el-descriptions-item>
          <el-descriptions-item label="房屋信息" :span="2">{{ selectedApplication.houseInfo || '未提供' }}</el-descriptions-item>
          
          <el-descriptions-item v-if="selectedApplication.reviewedAt" label="审核时间" :span="2">
            {{ formatTime(selectedApplication.reviewedAt) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.reviewerName" label="审核人" :span="2">
            {{ selectedApplication.reviewerName }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.reviewRemark" label="审核备注" :span="2">
            {{ selectedApplication.reviewRemark }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store/user'
import { getPendingApplications, reviewApplication as reviewApplicationAPI } from '../../api/community'

const userStore = useUserStore()

const loading = ref(true)
const applications = ref([])
const showRejectDialogFlag = ref(false)
const showDetailsFlag = ref(false)
const submitting = ref(false)
const selectedApplication = ref({})
const rejectFormRef = ref(null)

const rejectForm = ref({
  reviewRemark: ''
})

const rejectRules = {
  reviewRemark: [
    { required: true, message: '请填写拒绝原因', trigger: 'blur' },
    { min: 5, message: '拒绝原因至少5个字符', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审核',
    'APPROVED': '已通过',
    'REJECTED': '已拒绝'
  }
  return texts[status] || '未知状态'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleString('zh-CN')
}

const loadApplications = async () => {
  try {
    loading.value = true
    
    // 获取当前管理员的小区ID
    const communityId = userStore.user.communityId
    if (!communityId) {
      ElMessage.error('无法获取小区信息')
      return
    }
    
    const response = await getPendingApplications(communityId)
    applications.value = response.data
  } catch (error) {
    console.error('加载申请列表失败:', error)
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

const handleReviewApplication = async (application, status) => {
  try {
    await ElMessageBox.confirm(
      `确认${status === 'APPROVED' ? '通过' : '拒绝'}这个申请吗？`,
      '确认操作',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: status === 'APPROVED' ? 'success' : 'warning'
      }
    )
    
    const data = {
      applicationId: application.id,
      status: status,
      reviewerName: userStore.user.username,
      reviewRemark: status === 'APPROVED' ? '申请通过' : ''
    }
    
    await reviewApplicationAPI(data)
    
    ElMessage.success(`${status === 'APPROVED' ? '通过' : '拒绝'}成功`)
    await loadApplications()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error(error.response?.data?.message || '审核失败')
    }
  }
}

const showRejectDialog = (application) => {
  selectedApplication.value = application
  rejectForm.value.reviewRemark = ''
  showRejectDialogFlag.value = true
}

const confirmReject = async () => {
  if (!rejectFormRef.value) return
  
  try {
    await rejectFormRef.value.validate()
    
    submitting.value = true
    
    const data = {
      applicationId: selectedApplication.value.id,
      status: 'REJECTED',
      reviewerName: userStore.user.username,
      reviewRemark: rejectForm.value.reviewRemark
    }
    
    await reviewApplicationAPI(data)
    
    ElMessage.success('拒绝成功')
    showRejectDialogFlag.value = false
    await loadApplications()
    
  } catch (error) {
    console.error('拒绝失败:', error)
    ElMessage.error(error.response?.data?.message || '拒绝失败')
  } finally {
    submitting.value = false
  }
}

const viewDetails = (application) => {
  selectedApplication.value = application
  showDetailsFlag.value = true
}

const refreshData = () => {
  loadApplications()
}

onMounted(() => {
  loadApplications()
})
</script>

<style scoped>
.community-applications {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  color: #303133;
}

.reason-cell p {
  margin: 5px 0;
  line-height: 1.4;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 40px 0;
}

.application-details {
  margin: 20px 0;
}

@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
  
  .action-buttons .el-button {
    width: 100%;
  }
}
</style> 