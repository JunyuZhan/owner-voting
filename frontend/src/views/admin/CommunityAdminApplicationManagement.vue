<template>
  <div class="community-admin-application-management">
    <el-card>
      <template #header>
        <div class="header">
          <h2>小区管理员申请审核</h2>
          <div class="header-actions">
            <el-button type="primary" @click="refreshData">
              <i class="el-icon-refresh"></i> 刷新
            </el-button>
          </div>
        </div>
      </template>

      <!-- 统计信息 -->
      <el-row :gutter="16" class="statistics">
        <el-col :span="6">
          <el-statistic title="待审核" :value="statistics.PENDING || 0" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已通过" :value="statistics.APPROVED || 0" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已拒绝" :value="statistics.REJECTED || 0" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="总计" :value="statistics.TOTAL || 0" />
        </el-col>
      </el-row>

      <!-- 筛选条件 -->
      <el-form :model="filterForm" inline class="filter-form">
        <el-form-item label="状态筛选">
          <el-select v-model="filterForm.status" placeholder="全部状态" @change="loadApplications">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 申请列表 -->
      <el-table 
        :data="applications" 
        v-loading="loading"
        stripe
        style="width: 100%">
        
        <el-table-column prop="id" label="申请ID" width="80" />
        
        <el-table-column label="申请人信息" width="200">
          <template #default="scope">
            <div>
              <p><strong>{{ scope.row.applicantName }}</strong></p>
              <p><small>{{ scope.row.applicantPhone }}</small></p>
              <p v-if="scope.row.applicantEmail"><small>{{ scope.row.applicantEmail }}</small></p>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="小区信息" min-width="250">
          <template #default="scope">
            <div>
              <p><strong>{{ scope.row.communityName }}</strong></p>
              <p><small>{{ scope.row.communityAddress }}</small></p>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="申请时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
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
                type="info" 
                size="small"
                @click="viewDetails(scope.row)">
                详情
              </el-button>
              
              <el-button 
                v-if="scope.row.status === 'PENDING'"
                type="success" 
                size="small"
                @click="showApproveDialog(scope.row)">
                通过
              </el-button>
              
              <el-button 
                v-if="scope.row.status === 'PENDING'"
                type="danger" 
                size="small"
                @click="showRejectDialog(scope.row)">
                拒绝
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!loading && applications.length === 0" class="empty-state">
        <el-empty description="暂无申请记录" />
      </div>
    </el-card>

    <!-- 审核通过对话框 -->
    <el-dialog v-model="showApproveFlag" title="审核通过" width="500px">
      <el-form :model="approveForm" label-width="120px" ref="approveFormRef" :rules="approveRules">
        <el-form-item label="申请人">
          <el-input :value="selectedApplication.applicantName" disabled />
        </el-form-item>
        
        <el-form-item label="小区名称">
          <el-input :value="selectedApplication.communityName" disabled />
        </el-form-item>
        
        <el-form-item label="管理员用户名" prop="adminUsername">
          <el-input 
            v-model="approveForm.adminUsername" 
            placeholder="默认使用手机号作为用户名" />
          <div class="form-tip">留空将使用申请人手机号作为用户名</div>
        </el-form-item>
        
        <el-form-item label="初始密码" prop="adminPassword">
          <el-input 
            v-model="approveForm.adminPassword" 
            type="password"
            placeholder="默认密码为123456" />
          <div class="form-tip">留空将使用默认密码123456</div>
        </el-form-item>
        
        <el-form-item label="审核备注">
          <el-input 
            v-model="approveForm.reviewRemark" 
            type="textarea" 
            :rows="3"
            placeholder="审核通过备注（可选）" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showApproveFlag = false">取消</el-button>
        <el-button type="success" :loading="submitting" @click="confirmApprove">确认通过</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝申请对话框 -->
    <el-dialog v-model="showRejectFlag" title="拒绝申请" width="500px">
      <el-form :model="rejectForm" label-width="100px" ref="rejectFormRef" :rules="rejectRules">
        <el-form-item label="申请人">
          <el-input :value="selectedApplication.applicantName" disabled />
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
        <el-button @click="showRejectFlag = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog v-model="showDetailsFlag" title="申请详情" width="700px">
      <div class="application-details" v-if="selectedApplication">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="申请ID">{{ selectedApplication.id }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(selectedApplication.status)">
              {{ getStatusText(selectedApplication.status) }}
            </el-tag>
          </el-descriptions-item>
          
          <el-descriptions-item label="申请人姓名">{{ selectedApplication.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ selectedApplication.applicantPhone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ selectedApplication.applicantEmail || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ selectedApplication.applicantIdCard || '未填写' }}</el-descriptions-item>
          
          <el-descriptions-item label="小区名称">{{ selectedApplication.communityName }}</el-descriptions-item>
          <el-descriptions-item label="小区地址">{{ selectedApplication.communityAddress }}</el-descriptions-item>
          <el-descriptions-item label="小区描述" :span="2">{{ selectedApplication.communityDescription || '未填写' }}</el-descriptions-item>
          
          <el-descriptions-item label="申请理由" :span="2">{{ selectedApplication.applicationReason }}</el-descriptions-item>
          <el-descriptions-item label="资质证明" :span="2">{{ selectedApplication.qualificationProof || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="证件信息" :span="2">{{ selectedApplication.businessLicense || '未填写' }}</el-descriptions-item>
          
          <el-descriptions-item label="申请时间" :span="2">{{ formatTime(selectedApplication.createdAt) }}</el-descriptions-item>
          
          <el-descriptions-item v-if="selectedApplication.reviewedAt" label="审核时间" :span="2">
            {{ formatTime(selectedApplication.reviewedAt) }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.reviewerName" label="审核人" :span="2">
            {{ selectedApplication.reviewerName }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.reviewRemark" label="审核备注" :span="2">
            {{ selectedApplication.reviewRemark }}
          </el-descriptions-item>
          
          <el-descriptions-item v-if="selectedApplication.createdCommunityId" label="创建小区ID" :span="2">
            {{ selectedApplication.createdCommunityId }}
          </el-descriptions-item>
          <el-descriptions-item v-if="selectedApplication.createdAdminUserId" label="管理员账号ID" :span="2">
            {{ selectedApplication.createdAdminUserId }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { 
  getApplications, 
  getApplicationStatistics, 
  reviewApplication 
} from '@/api/communityAdmin'

const userStore = useUserStore()

const loading = ref(true)
const submitting = ref(false)
const applications = ref([])
const statistics = ref({})
const showApproveFlag = ref(false)
const showRejectFlag = ref(false)
const showDetailsFlag = ref(false)
const selectedApplication = ref({})

const approveFormRef = ref(null)
const rejectFormRef = ref(null)

const filterForm = reactive({
  status: ''
})

const approveForm = reactive({
  adminUsername: '',
  adminPassword: '',
  reviewRemark: ''
})

const rejectForm = reactive({
  reviewRemark: ''
})

const approveRules = {
  adminUsername: [
    { pattern: /^[a-zA-Z0-9_]{4,20}$/, message: '用户名只能包含字母、数字、下划线，长度4-20位', trigger: 'blur' }
  ],
  adminPassword: [
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

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
    
    const response = await getApplications(filterForm.status)
    applications.value = response.data || []
  } catch (error) {
    console.error('加载申请列表失败:', error)
    ElMessage.error('加载申请列表失败')
  } finally {
    loading.value = false
  }
}

const loadStatistics = async () => {
  try {
    const response = await getApplicationStatistics()
    statistics.value = response.data || {}
  } catch (error) {
    console.error('加载统计信息失败:', error)
  }
}

const viewDetails = (application) => {
  selectedApplication.value = application
  showDetailsFlag.value = true
}

const showApproveDialog = (application) => {
  selectedApplication.value = application
  approveForm.adminUsername = ''
  approveForm.adminPassword = ''
  approveForm.reviewRemark = ''
  showApproveFlag.value = true
}

const showRejectDialog = (application) => {
  selectedApplication.value = application
  rejectForm.reviewRemark = ''
  showRejectFlag.value = true
}

const confirmApprove = async () => {
  try {
    submitting.value = true
    
    const data = {
      applicationId: selectedApplication.value.id,
      status: 'APPROVED',
      reviewerName: userStore.user.username,
      reviewRemark: approveForm.reviewRemark,
      adminUsername: approveForm.adminUsername,
      adminPassword: approveForm.adminPassword
    }
    
    await reviewApplication(data)
    
    ElMessage.success('审核通过，已创建小区和管理员账号')
    showApproveFlag.value = false
    await refreshData()
    
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    submitting.value = false
  }
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
      reviewRemark: rejectForm.reviewRemark
    }
    
    await reviewApplication(data)
    
    ElMessage.success('已拒绝申请')
    showRejectFlag.value = false
    await refreshData()
    
  } catch (error) {
    if (error.errors) {
      return // 表单验证失败
    }
    console.error('审核失败:', error)
    ElMessage.error(error.response?.data?.message || '审核失败')
  } finally {
    submitting.value = false
  }
}

const refreshData = async () => {
  await Promise.all([loadApplications(), loadStatistics()])
}

onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.community-admin-application-management {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h2 {
  margin: 0;
  color: #409EFF;
}

.statistics {
  margin: 20px 0;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.filter-form {
  margin: 20px 0;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.application-details {
  max-height: 500px;
  overflow-y: auto;
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

:deep(.el-statistic__number) {
  font-size: 24px;
  font-weight: bold;
}

:deep(.el-statistic__title) {
  font-size: 14px;
  color: #666;
}
</style> 