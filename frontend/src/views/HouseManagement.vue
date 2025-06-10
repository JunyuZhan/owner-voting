<template>
  <div class="house-management">
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <h2>我的房屋</h2>
          <el-button type="primary" @click="goToRegister">注册新房屋</el-button>
        </div>
      </template>

      <el-alert 
        v-if="eligibilityInfo.message"
        :title="eligibilityInfo.message" 
        :type="eligibilityInfo.canVote ? 'success' : 'warning'"
        show-icon
        style="margin-bottom: 20px;" />

      <el-table :data="houseList" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">
            <p>您还没有注册任何房屋</p>
            <el-button type="primary" @click="goToRegister">立即注册</el-button>
          </div>
        </template>
        
        <el-table-column prop="building" label="楼栋" width="100" />
        <el-table-column prop="unit" label="单元" width="100">
          <template #default="scope">
            {{ scope.row.unit || '无' }}
          </template>
        </el-table-column>
        <el-table-column prop="room" label="房号" width="100" />
        <el-table-column prop="area" label="专有面积" width="120">
          <template #default="scope">
            {{ scope.row.area }}㎡
          </template>
        </el-table-column>
        <el-table-column prop="certificateNumber" label="房产证号" width="150" />
        <el-table-column prop="verificationStatus" label="认证状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.verificationStatus)">
              {{ getStatusText(scope.row.verificationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isPrimary" label="主要房屋" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.isPrimary" type="success" size="small">是</el-tag>
            <span v-else>否</span>
          </template>
        </el-table-column>
        <el-table-column prop="hasDispute" label="争议状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.hasDispute" type="danger" size="small">有争议</el-tag>
            <span v-else>正常</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间" width="150">
          <template #default="scope">
            {{ formatTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="160">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="showDetail(scope.row)"
              type="primary" 
              link>
              详情
            </el-button>
            <el-button 
              v-if="canEdit(scope.row)"
              size="small" 
              @click="editHouse(scope.row)"
              type="warning" 
              link
              style="margin-left: 8px;">
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 房屋详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="房屋详情" width="500px">
      <el-descriptions v-if="selectedHouse" :column="1" border>
        <el-descriptions-item label="楼栋">{{ selectedHouse.building }}</el-descriptions-item>
        <el-descriptions-item label="单元">{{ selectedHouse.unit || '无' }}</el-descriptions-item>
        <el-descriptions-item label="房号">{{ selectedHouse.room }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ selectedHouse.address || '无' }}</el-descriptions-item>
        <el-descriptions-item label="专有面积">{{ selectedHouse.area }}㎡</el-descriptions-item>
        <el-descriptions-item label="房产证号">{{ selectedHouse.certificateNumber }}</el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag :type="getStatusType(selectedHouse.verificationStatus)">
            {{ getStatusText(selectedHouse.verificationStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="主要房屋">
          {{ selectedHouse.isPrimary ? '是' : '否' }}
        </el-descriptions-item>
        <el-descriptions-item label="争议状态">
          <el-tag v-if="selectedHouse.hasDispute" type="danger" size="small">有争议</el-tag>
          <span v-else>正常</span>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">
          {{ formatTime(selectedHouse.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="认证时间">
          {{ selectedHouse.verifiedAt ? formatTime(selectedHouse.verifiedAt) : '未认证' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" v-if="selectedHouse.verificationRemark">
          {{ selectedHouse.verificationRemark }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 编辑房屋信息对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑房屋信息" width="500px">
      <el-alert 
        title="注意：只能修改待审核或被拒绝的房屋信息。已认证的房屋信息不能修改。" 
        type="warning" 
        :closable="false"
        style="margin-bottom: 20px;" />
      
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="专有面积" prop="area">
          <el-input-number 
            v-model="editForm.area" 
            :min="0.1" 
            :precision="2" 
            :step="0.1"
            style="width: 100%;" />
          <div style="color: #909399; font-size: 12px; margin-top: 4px;">
            单位：平方米，最小0.1㎡
          </div>
        </el-form-item>
        
        <el-form-item label="房产证号" prop="certificateNumber">
          <el-input v-model="editForm.certificateNumber" placeholder="请输入房产证号码" />
        </el-form-item>
        
        <el-form-item label="详细地址" prop="address">
          <el-input 
            v-model="editForm.address" 
            type="textarea" 
            :rows="3"
            placeholder="请输入房屋详细地址（可选）" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleEditSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { getOwnerHouses, checkVotingEligibility, updateHouse } from '../api/house'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(true)
const houseList = ref([])
const showDetailDialog = ref(false)
const showEditDialog = ref(false)
const selectedHouse = ref(null)
const editLoading = ref(false)
const editFormRef = ref(null)

const eligibilityInfo = ref({
  canVote: false,
  votingWeight: 0,
  message: ''
})

const editForm = ref({
  area: 0,
  certificateNumber: '',
  address: ''
})

const editRules = {
  area: [
    { required: true, message: '请输入专有面积', trigger: 'blur' },
    { type: 'number', min: 0.1, message: '面积不能小于0.1平方米', trigger: 'blur' }
  ],
  certificateNumber: [
    { required: true, message: '请输入房产证号', trigger: 'blur' }
  ]
}

const getStatusType = (status) => {
  const types = {
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'DISPUTED': 'danger'
  }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待审核',
    'APPROVED': '已认证',
    'REJECTED': '已拒绝',
    'DISPUTED': '有争议'
  }
  return texts[status] || '未知'
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleString('zh-CN')
}

// 判断房屋是否可以编辑（只有待审核或被拒绝的房屋可以编辑）
const canEdit = (house) => {
  return house.verificationStatus === 'PENDING' || house.verificationStatus === 'REJECTED'
}

const loadHouseList = async () => {
  try {
    loading.value = true
    const response = await getOwnerHouses(userStore.user.id)
    houseList.value = response.data || []
  } catch (error) {
    console.error('加载房屋列表失败:', error)
    ElMessage.error('加载房屋列表失败')
  } finally {
    loading.value = false
  }
}

const loadEligibilityInfo = async () => {
  try {
    const response = await checkVotingEligibility(userStore.user.id)
    eligibilityInfo.value = response.data
  } catch (error) {
    console.error('加载投票资格失败:', error)
  }
}

const showDetail = (house) => {
  selectedHouse.value = house
  showDetailDialog.value = true
}

const editHouse = (house) => {
  if (!canEdit(house)) {
    ElMessage.warning('该房屋当前状态不允许编辑')
    return
  }
  
  selectedHouse.value = house
  editForm.value = {
    area: house.area,
    certificateNumber: house.certificateNumber,
    address: house.address || ''
  }
  showEditDialog.value = true
}

const handleEditSubmit = async () => {
  try {
    await editFormRef.value.validate()
    editLoading.value = true
    
    const updateData = {
      ownerId: userStore.user.id,
      communityId: selectedHouse.value.community?.id || selectedHouse.value.communityId,
      building: selectedHouse.value.building,
      unit: selectedHouse.value.unit,
      room: selectedHouse.value.room,
      area: editForm.value.area,
      certificateNumber: editForm.value.certificateNumber,
      address: editForm.value.address
    }
    
    await updateHouse(selectedHouse.value.id, updateData)
    
    ElMessage.success('房屋信息修改成功')
    showEditDialog.value = false
    loadHouseList() // 重新加载列表
    
  } catch (error) {
    console.error('修改房屋信息失败:', error)
    ElMessage.error(error.response?.data?.message || '修改房屋信息失败')
  } finally {
    editLoading.value = false
  }
}

const goToRegister = () => {
  router.push('/house-register')
}

onMounted(() => {
  if (!userStore.user.id) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  loadHouseList()
  loadEligibilityInfo()
})
</script>

<style scoped>
.house-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.el-table {
  margin-top: 20px;
}
</style>