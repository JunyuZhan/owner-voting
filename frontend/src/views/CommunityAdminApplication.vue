<template>
  <div class="community-admin-application">
    <el-card>
      <template #header>
        <div class="header">
          <h2>申请成为小区管理员</h2>
          <p>如果您是小区业委会成员或物业管理方，可以申请成为小区管理员</p>
        </div>
      </template>

      <el-steps :active="currentStep" finish-status="success" align-center class="steps">
        <el-step title="填写申请信息" />
        <el-step title="提交申请" />
        <el-step title="等待审核" />
        <el-step title="完成" />
      </el-steps>

      <!-- 申请表单 -->
      <div v-if="currentStep === 0" class="form-section">
        <el-form 
          :model="applicationForm" 
          :rules="applicationRules" 
          ref="applicationFormRef" 
          label-width="120px"
          class="application-form"
        >
          <el-divider content-position="left">申请人信息</el-divider>
          
          <el-form-item label="姓名" prop="applicantName">
            <el-input v-model="applicationForm.applicantName" placeholder="请输入您的真实姓名" />
          </el-form-item>
          
          <el-form-item label="手机号" prop="applicantPhone">
            <el-input v-model="applicationForm.applicantPhone" placeholder="请输入手机号" />
          </el-form-item>
          
          <el-form-item label="邮箱" prop="applicantEmail">
            <el-input v-model="applicationForm.applicantEmail" placeholder="请输入邮箱地址（可选）" />
          </el-form-item>
          
          <el-form-item label="身份证号" prop="applicantIdCard">
            <el-input v-model="applicationForm.applicantIdCard" placeholder="请输入身份证号（可选）" />
          </el-form-item>

          <el-divider content-position="left">小区信息</el-divider>
          
          <el-form-item label="小区名称" prop="communityName">
            <el-input v-model="applicationForm.communityName" placeholder="请输入小区名称" />
          </el-form-item>
          
          <el-form-item label="小区地址" prop="communityAddress">
            <el-input v-model="applicationForm.communityAddress" placeholder="请输入小区详细地址" />
          </el-form-item>
          
          <el-form-item label="小区描述">
            <el-input 
              v-model="applicationForm.communityDescription" 
              type="textarea" 
              :rows="3"
              placeholder="请简单描述小区情况（可选）" 
            />
          </el-form-item>

          <el-divider content-position="left">申请说明</el-divider>
          
          <el-form-item label="申请理由" prop="applicationReason">
            <el-input 
              v-model="applicationForm.applicationReason" 
              type="textarea" 
              :rows="4"
              placeholder="请详细说明您申请成为该小区管理员的理由，如：您在该小区的身份、管理经验等" 
            />
          </el-form-item>
          
          <el-form-item label="资质证明">
            <el-input 
              v-model="applicationForm.qualificationProof" 
              type="textarea" 
              :rows="3"
              placeholder="请说明您的相关资质或证明材料（如：业委会任命书、物业资质等）" 
            />
          </el-form-item>
          
          <el-form-item label="证件信息">
            <el-input 
              v-model="applicationForm.businessLicense" 
              placeholder="如有营业执照或其他相关证件，请填写证件号码" 
            />
          </el-form-item>
        </el-form>

        <div class="form-actions">
          <el-button @click="checkApplicationStatus">查询申请状态</el-button>
          <el-button type="primary" @click="nextStep">下一步</el-button>
        </div>
      </div>

      <!-- 确认信息 -->
      <div v-if="currentStep === 1" class="confirm-section">
        <el-descriptions title="请确认申请信息" :column="2" border>
          <el-descriptions-item label="申请人姓名">{{ applicationForm.applicantName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ applicationForm.applicantPhone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ applicationForm.applicantEmail || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ applicationForm.applicantIdCard || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="小区名称">{{ applicationForm.communityName }}</el-descriptions-item>
          <el-descriptions-item label="小区地址">{{ applicationForm.communityAddress }}</el-descriptions-item>
          <el-descriptions-item label="小区描述" :span="2">{{ applicationForm.communityDescription || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="申请理由" :span="2">{{ applicationForm.applicationReason }}</el-descriptions-item>
          <el-descriptions-item label="资质证明" :span="2">{{ applicationForm.qualificationProof || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="证件信息" :span="2">{{ applicationForm.businessLicense || '未填写' }}</el-descriptions-item>
        </el-descriptions>

        <div class="form-actions">
          <el-button @click="prevStep">上一步</el-button>
          <el-button type="primary" :loading="submitting" @click="submitApplication">提交申请</el-button>
        </div>
      </div>

      <!-- 提交成功 -->
      <div v-if="currentStep === 2" class="success-section">
        <el-result icon="success" title="申请提交成功" sub-title="您的申请已提交，请耐心等待系统管理员审核">
          <template #extra>
            <el-button type="primary" @click="checkApplicationStatus">查询申请状态</el-button>
            <el-button @click="resetForm">提交新申请</el-button>
          </template>
        </el-result>
      </div>
    </el-card>

    <!-- 申请状态查询对话框 -->
    <el-dialog v-model="showStatusDialog" title="申请状态查询" width="600px">
      <el-form :model="statusQuery" label-width="100px">
        <el-form-item label="手机号">
          <el-input v-model="statusQuery.phone" placeholder="请输入申请时填写的手机号" />
        </el-form-item>
      </el-form>
      
      <div v-if="applicationHistory.length > 0" class="status-list">
        <el-timeline>
          <el-timeline-item 
            v-for="app in applicationHistory" 
            :key="app.id"
            :type="getStatusType(app.status)"
            :timestamp="formatTime(app.createdAt)"
          >
            <el-card>
              <h4>{{ app.communityName }}</h4>
              <p><strong>状态：</strong>
                <el-tag :type="getStatusType(app.status)">{{ getStatusText(app.status) }}</el-tag>
              </p>
              <p><strong>申请时间：</strong>{{ formatTime(app.createdAt) }}</p>
              <p v-if="app.reviewedAt"><strong>审核时间：</strong>{{ formatTime(app.reviewedAt) }}</p>
              <p v-if="app.reviewRemark"><strong>审核备注：</strong>{{ app.reviewRemark }}</p>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
      
      <div v-else-if="statusQueried" class="no-applications">
        <el-empty description="未找到相关申请记录" />
      </div>
      
      <template #footer>
        <el-button @click="showStatusDialog = false">关闭</el-button>
        <el-button type="primary" @click="queryStatus">查询</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { submitCommunityAdminApplication, getApplicationStatus } from '@/api/communityAdmin'

const currentStep = ref(0)
const submitting = ref(false)
const showStatusDialog = ref(false)
const statusQueried = ref(false)
const applicationFormRef = ref(null)

const applicationForm = reactive({
  applicantName: '',
  applicantPhone: '',
  applicantEmail: '',
  applicantIdCard: '',
  communityName: '',
  communityAddress: '',
  communityDescription: '',
  applicationReason: '',
  qualificationProof: '',
  businessLicense: ''
})

const statusQuery = reactive({
  phone: ''
})

const applicationHistory = ref([])

const applicationRules = {
  applicantName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  applicantPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  applicantEmail: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  applicantIdCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, 
      message: '身份证号格式不正确', trigger: 'blur' }
  ],
  communityName: [
    { required: true, message: '请输入小区名称', trigger: 'blur' }
  ],
  communityAddress: [
    { required: true, message: '请输入小区地址', trigger: 'blur' }
  ],
  applicationReason: [
    { required: true, message: '请填写申请理由', trigger: 'blur' },
    { min: 20, message: '申请理由至少20个字符', trigger: 'blur' }
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

const nextStep = async () => {
  if (!applicationFormRef.value) return
  
  try {
    await applicationFormRef.value.validate()
    currentStep.value = 1
  } catch (error) {
    // 表单验证失败
  }
}

const prevStep = () => {
  currentStep.value = 0
}

const submitApplication = async () => {
  try {
    submitting.value = true
    
    await submitCommunityAdminApplication(applicationForm)
    
    ElMessage.success('申请提交成功！')
    currentStep.value = 2
    
  } catch (error) {
    console.error('提交申请失败:', error)
    ElMessage.error(error.response?.data?.message || '提交申请失败')
  } finally {
    submitting.value = false
  }
}

const checkApplicationStatus = () => {
  statusQuery.phone = applicationForm.applicantPhone || ''
  statusQueried.value = false
  applicationHistory.value = []
  showStatusDialog.value = true
}

const queryStatus = async () => {
  if (!statusQuery.phone) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  try {
    const response = await getApplicationStatus(statusQuery.phone)
    applicationHistory.value = response.data || []
    statusQueried.value = true
    
    if (applicationHistory.value.length === 0) {
      ElMessage.info('未找到相关申请记录')
    }
  } catch (error) {
    console.error('查询申请状态失败:', error)
    ElMessage.error('查询失败，请重试')
  }
}

const resetForm = () => {
  currentStep.value = 0
  Object.keys(applicationForm).forEach(key => {
    applicationForm[key] = ''
  })
  if (applicationFormRef.value) {
    applicationFormRef.value.resetFields()
  }
}
</script>

<style scoped>
.community-admin-application {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  text-align: center;
  margin-bottom: 20px;
}

.header h2 {
  color: #409EFF;
  margin-bottom: 10px;
}

.header p {
  color: #666;
  font-size: 14px;
}

.steps {
  margin: 30px 0;
}

.form-section {
  margin-top: 30px;
}

.application-form {
  max-width: 600px;
  margin: 0 auto;
}

.form-actions {
  text-align: center;
  margin-top: 30px;
}

.confirm-section {
  margin-top: 30px;
}

.success-section {
  margin-top: 30px;
}

.status-list {
  margin-top: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.no-applications {
  text-align: center;
  padding: 20px;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #999;
}
</style> 