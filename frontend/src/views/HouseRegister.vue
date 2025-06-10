<template>
  <div class="house-register">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h2>房屋注册</h2>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-alert 
        title="房屋认证说明" 
        type="info" 
        :closable="false"
        style="margin-bottom: 20px;">
        <p>1. 请准确填写房屋信息，包括专有面积（用于按面积加权投票）</p>
        <p>2. 请上传清晰的房产证照片作为认证依据</p>
        <p>3. 提交后需要管理员审核，审核通过后才能参与投票</p>
      </el-alert>

      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        label-width="120px"
        @submit.prevent="handleSubmit">
        
        <el-form-item label="所属小区" prop="communityId">
          <el-select v-model="form.communityId" placeholder="请选择小区" style="width: 100%">
            <el-option 
              v-for="community in availableCommunities" 
              :key="community.id" 
              :label="community.name" 
              :value="community.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="楼栋号" prop="building">
          <el-input 
            v-model="form.building" 
            placeholder="如：1栋、A栋" 
            @blur="checkConflict" />
        </el-form-item>

        <el-form-item label="单元号" prop="unit">
          <el-input 
            v-model="form.unit" 
            placeholder="如：1单元（可选，别墅等可不填）" 
            @blur="checkConflict" />
        </el-form-item>

        <el-form-item label="房间号" prop="room">
          <el-input 
            v-model="form.room" 
            placeholder="如：301、1001" 
            @blur="checkConflict" />
        </el-form-item>

        <el-form-item label="完整地址">
          <el-input 
            v-model="form.address" 
            type="textarea" 
            :rows="2"
            placeholder="详细地址（可选）" />
        </el-form-item>

        <el-form-item label="专有面积" prop="area">
          <el-input-number 
            v-model="form.area" 
            :precision="2" 
            :step="0.01" 
            :min="0.1" 
            :max="9999.99"
            style="width: 200px" />
          <span style="margin-left: 10px; color: #606266;">平方米</span>
        </el-form-item>

        <el-form-item label="房产证号" prop="certificateNumber">
          <el-input 
            v-model="form.certificateNumber" 
            placeholder="请输入房产证编号" />
        </el-form-item>

        <el-form-item label="房产证照片" prop="certificateFileId">
          <UploadFile 
            v-model:file-id="form.certificateFileId" 
            accept="image/*"
            :file-size-limit="5"
            tip="请上传房产证照片，支持JPG/PNG格式，文件大小不超过5MB" />
        </el-form-item>

        <el-form-item label="设为主要房屋">
          <el-switch v-model="form.isPrimary" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">
            主要房屋将用作默认投票房屋
          </span>
        </el-form-item>

        <el-form-item label="备注">
          <el-input 
            v-model="form.remark" 
            type="textarea" 
            :rows="3"
            placeholder="其他说明（可选）" />
        </el-form-item>

        <!-- 冲突提示 -->
        <el-alert 
          v-if="conflictInfo.hasConflict" 
          :title="conflictInfo.message" 
          type="warning" 
          show-icon
          style="margin-bottom: 20px;" />

        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSubmit" 
            :loading="submitting"
            :disabled="!canSubmit">
            提交注册申请
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { registerHouse, checkHouseConflict } from '../api/house'
import { getCommunityList } from '../api/community'
import UploadFile from '../components/UploadFile.vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)
const availableCommunities = ref([])

const form = ref({
  ownerId: userStore.user.id,
  communityId: null,
  building: '',
  unit: '',
  room: '',
  address: '',
  area: null,
  certificateNumber: '',
  certificateFileId: null,
  isPrimary: false,
  remark: ''
})

const conflictInfo = ref({
  hasConflict: false,
  message: ''
})

const rules = {
  communityId: [
    { required: true, message: '请选择小区', trigger: 'change' }
  ],
  building: [
    { required: true, message: '请输入楼栋号', trigger: 'blur' },
    { max: 50, message: '楼栋号不能超过50个字符', trigger: 'blur' }
  ],
  room: [
    { required: true, message: '请输入房间号', trigger: 'blur' },
    { max: 50, message: '房间号不能超过50个字符', trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入专有面积', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '面积必须大于0', trigger: 'blur' }
  ],
  certificateNumber: [
    { required: true, message: '请输入房产证号', trigger: 'blur' },
    { max: 100, message: '房产证号不能超过100个字符', trigger: 'blur' }
  ],
  certificateFileId: [
    { required: true, message: '请上传房产证照片', trigger: 'change' }
  ]
}

const canSubmit = computed(() => {
  return form.value.communityId && 
         form.value.building && 
         form.value.room && 
         form.value.area && 
         form.value.certificateNumber && 
         form.value.certificateFileId &&
         !submitting.value
})

// 检查房屋位置冲突
const checkConflict = async () => {
  if (!form.value.communityId || !form.value.building || !form.value.room) {
    conflictInfo.value = { hasConflict: false, message: '' }
    return
  }

  try {
    const params = {
      communityId: form.value.communityId,
      building: form.value.building,
      unit: form.value.unit || null,
      room: form.value.room
    }
    
    const response = await checkHouseConflict(params)
    const conflict = response.data
    
    conflictInfo.value = {
      hasConflict: conflict.hasConflict,
      message: conflict.conflictMessage || ''
    }
  } catch (error) {
    console.error('检查冲突失败:', error)
  }
}

// 监听关键字段变化，自动检查冲突
watch([
  () => form.value.communityId,
  () => form.value.building,
  () => form.value.unit,
  () => form.value.room
], () => {
  checkConflict()
}, { deep: true })

const loadCommunities = async () => {
  try {
    const response = await getCommunityList(userStore.user.id)
    // 只显示已加入的小区
    availableCommunities.value = response.data.filter(c => c.ownerStatus === 'APPROVED')
    
    if (availableCommunities.value.length === 0) {
      ElMessage.warning('您还未加入任何小区，请先申请加入小区')
      router.push('/community-select')
    }
  } catch (error) {
    console.error('加载小区列表失败:', error)
    ElMessage.error('加载小区列表失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (conflictInfo.value.hasConflict) {
      ElMessage.warning('存在房屋位置冲突，请确认后再提交')
    }
    
    submitting.value = true
    
    const submitData = { ...form.value }
    await registerHouse(submitData)
    
    ElMessage.success('房屋注册申请提交成功，等待管理员审核')
    router.push('/personal-center')
    
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  conflictInfo.value = { hasConflict: false, message: '' }
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
.house-register {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.el-form {
  max-width: 600px;
}

.el-alert {
  margin-bottom: 20px;
}

.el-alert p {
  margin: 5px 0;
  font-size: 14px;
}
</style>