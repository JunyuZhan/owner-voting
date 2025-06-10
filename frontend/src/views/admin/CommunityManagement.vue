<template>
  <div class="community-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>小区管理</h3>
          <el-button 
            v-if="userStore.isSystemAdmin" 
            type="primary" 
            @click="showAddDialog = true"
          >
            新增小区
          </el-button>
        </div>
      </template>

      <!-- 当前小区信息 -->
      <div v-if="!userStore.isSystemAdmin && currentCommunity" class="current-community">
        <el-alert 
          :title="`当前管理小区：${currentCommunity.name}`" 
          type="info" 
          :closable="false"
          class="mb-4"
        >
          <template #default>
            <p><strong>地址：</strong>{{ currentCommunity.address }}</p>
            <p><strong>描述：</strong>{{ currentCommunity.description }}</p>
          </template>
        </el-alert>
      </div>

      <!-- 小区列表 -->
      <el-table :data="communities" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="小区名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column 
          v-if="userStore.isSystemAdmin" 
          label="操作" 
          width="200"
        >
          <template #default="scope">
            <el-button type="primary" size="small" @click="editCommunity(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteCommunityConfirm(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑小区对话框 -->
    <el-dialog 
      :title="editingCommunity.id ? '编辑小区' : '新增小区'"
      v-model="showAddDialog"
      width="500px"
    >
      <el-form 
        :model="editingCommunity" 
        :rules="communityRules" 
        ref="communityFormRef"
        label-width="100px"
      >
        <el-form-item label="小区名称" prop="name">
          <el-input v-model="editingCommunity.name" placeholder="请输入小区名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="editingCommunity.address" placeholder="请输入小区地址" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="editingCommunity.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入小区描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveCommunity">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '../../store/user'
import { 
  getAllCommunities, 
  getCurrentCommunity, 
  createCommunity, 
  deleteCommunity,
  updateCommunity 
} from '../../api/community'

const userStore = useUserStore()

// 数据
const communities = ref([])
const currentCommunity = ref(null)
const loading = ref(false)
const showAddDialog = ref(false)
const communityFormRef = ref()

// 编辑中的小区
const editingCommunity = reactive({
  id: null,
  name: '',
  address: '',
  description: ''
})

// 表单验证规则
const communityRules = {
  name: [
    { required: true, message: '请输入小区名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入小区地址', trigger: 'blur' }
  ]
}

// 方法
const loadCommunities = async () => {
  loading.value = true
  try {
    if (userStore.isSystemAdmin) {
      const response = await getAllCommunities()
      communities.value = response.data || []
    } else {
      const response = await getCurrentCommunity()
      if (response.data && response.data.community) {
        currentCommunity.value = response.data.community
        communities.value = [response.data.community]
      }
    }
  } catch (error) {
    ElMessage.error('加载小区信息失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const editCommunity = (community) => {
  Object.assign(editingCommunity, community)
  showAddDialog.value = true
}

const resetForm = () => {
  Object.assign(editingCommunity, {
    id: null,
    name: '',
    address: '',
    description: ''
  })
  if (communityFormRef.value) {
    communityFormRef.value.resetFields()
  }
}

const saveCommunity = async () => {
  if (!communityFormRef.value) return
  
  try {
    await communityFormRef.value.validate()
    
    if (editingCommunity.id) {
      // 更新小区
      await updateCommunity(editingCommunity.id, editingCommunity)
      ElMessage.success('小区信息更新成功')
    } else {
      // 创建新小区
      await createCommunity(editingCommunity)
      ElMessage.success('小区创建成功')
    }
    
    showAddDialog.value = false
    resetForm()
    loadCommunities()
  } catch (error) {
    if (error.errors) {
      return // 表单验证失败
    }
    ElMessage.error('操作失败，请重试')
    console.error(error)
  }
}

const deleteCommunityConfirm = async (community) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除小区"${community.name}"吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteCommunity(community.id)
    ElMessage.success('小区删除成功')
    loadCommunities()
  } catch (error) {
    if (error === 'cancel') return
    ElMessage.error('删除失败，请重试')
    console.error(error)
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

// 监听对话框关闭，重置表单
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  loadCommunities()
})
</script>

<style scoped>
.community-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-community {
  margin-bottom: 20px;
}

.mb-4 {
  margin-bottom: 16px;
}
</style> 