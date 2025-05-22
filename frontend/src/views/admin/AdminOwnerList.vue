<template>
  <div class="admin-owner-list">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>业主管理</h2>
        <el-button type="primary">新增业主</el-button>
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
import { getOwnerList, reviewOwner, deleteOwner } from '../../api/owner'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const showReviewDialog = ref(false)
const reviewForm = ref({ ownerId: '', status: '', reviewComment: '' })
const reviewLoading = ref(false)
const showDetailDialog = ref(false)
const detailData = ref({})

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getOwnerList()
    list.value = res.list || res.data?.list || []
  } finally {
    loading.value = false
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