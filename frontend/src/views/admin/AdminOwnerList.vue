<template>
  <div class="admin-owner-list">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>业主管理</h2>
        <el-button type="primary" @click="openAddDialog">新增业主</el-button>
      </div>
      <el-table :data="list" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="isVerified" label="认证状态">
          <template #default="scope">
            <el-tag :type="scope.row.isVerified ? 'success' : 'info'">
              {{ scope.row.isVerified ? '已认证' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openReview(scope.row)">审核</el-button>
            <el-button size="small" @click="openDetail(scope.row)">查看</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增业主弹窗 -->
    <el-dialog v-model="showAddDialog" title="新增业主" width="500px">
      <el-form :model="addForm" :rules="addRules" ref="addFormRef" label-width="80px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="addForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="初始密码" prop="password">
          <el-input v-model="addForm.password" type="password" placeholder="不填写将使用手机号后6位" />
        </el-form-item>
        <el-form-item label="认证状态" prop="isVerified">
          <el-switch v-model="addForm.isVerified" active-text="已认证" inactive-text="未认证" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addForm.remark" type="textarea" placeholder="可选备注信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 业主认证审核弹窗 -->
    <el-dialog v-model="showReviewDialog" title="业主认证审核" width="400px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-select v-model="reviewForm.status" placeholder="请选择">
            <el-option label="通过" value="APPROVED" />
            <el-option label="驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="reviewForm.reviewComment" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReviewDialog = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="handleReview">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 业主详情弹窗 -->
    <el-dialog v-model="showDetailDialog" title="业主详情" width="400px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag :type="detailData.isVerified ? 'success' : 'info'">
            {{ detailData.isVerified ? '已认证' : '未认证' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updatedAt }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOwnerList, reviewOwner, deleteOwner, addOwner } from '../../api/owner'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const showReviewDialog = ref(false)
const reviewForm = ref({ ownerId: '', status: '', reviewComment: '' })
const reviewLoading = ref(false)
const showDetailDialog = ref(false)
const detailData = ref({})

// 新增业主相关
const showAddDialog = ref(false)
const addLoading = ref(false)
const addFormRef = ref()
const addForm = ref({
  name: '',
  phone: '',
  idCard: '',
  password: '',
  isVerified: false,
  remark: ''
})

const addRules = {
  name: [
    { required: true, message: '请输入业主姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '身份证号格式不正确', trigger: 'blur' }
  ]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getOwnerList()
    console.log('获取业主列表响应:', res)
    // 修复数据处理逻辑：后端返回的是 {code: 200, data: [...], message: "success"}
    list.value = res.data || res.list || []
    console.log('处理后的业主列表:', list.value)
  } catch (error) {
    console.error('获取业主列表失败:', error)
    ElMessage.error('获取业主列表失败')
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  addForm.value = {
    name: '',
    phone: '',
    idCard: '',
    password: '',
    isVerified: false,
    remark: ''
  }
  showAddDialog.value = true
}

const handleAdd = async () => {
  if (!addFormRef.value) return
  
  try {
    await addFormRef.value.validate()
  } catch {
    return
  }
  
  addLoading.value = true
  try {
    await addOwner(addForm.value)
    ElMessage.success('新增业主成功')
    showAddDialog.value = false
    fetchList()
  } catch (error) {
    ElMessage.error(error.message || '新增业主失败')
  } finally {
    addLoading.value = false
  }
}

const openReview = row => {
  reviewForm.value = { ownerId: row.id, status: '', reviewComment: '' }
  showReviewDialog.value = true
}

const handleReview = async () => {
  if (!reviewForm.value.status) {
    ElMessage.warning('请选择审核结果')
    return
  }
  reviewLoading.value = true
  try {
    await reviewOwner(reviewForm.value)
    ElMessage.success('审核成功')
    showReviewDialog.value = false
    fetchList()
  } catch {
    ElMessage.error('审核失败')
  } finally {
    reviewLoading.value = false
  }
}

const openDetail = row => {
  detailData.value = row
  showDetailDialog.value = true
}

const confirmDelete = id => {
  ElMessageBox.confirm('确定要删除该业主吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    handleDelete(id)
  })
}

const handleDelete = async id => {
  try {
    await deleteOwner(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.admin-owner-list {
  max-width: 1000px;
  margin: 40px auto;
}
</style> 