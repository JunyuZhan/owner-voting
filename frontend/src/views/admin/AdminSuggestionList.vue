<template>
  <div class="admin-suggestion-list">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>建议管理</h2>
      </div>
      <el-table :data="list" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="likeCount" label="点赞数" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openReply(scope.row)">回复</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showReplyDialog" title="回复建议" width="400px">
      <el-form :model="replyForm" label-width="60px">
        <el-form-item label="回复">
          <el-input v-model="replyForm.content" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReplyDialog = false">取消</el-button>
        <el-button type="primary" :loading="replyLoading" @click="handleReply">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSuggestions, deleteSuggestion, adminReplySuggestion } from '../../api/suggestion'
import { ElMessage, ElMessageBox } from 'element-plus'
const list = ref([])
const loading = ref(false)
const showReplyDialog = ref(false)
const replyForm = ref({ content: '' })
const replyLoading = ref(false)
const currentId = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getSuggestions()
    list.value = res.list || res.data?.list || []
  } finally {
    loading.value = false
  }
}

const openReply = row => {
  currentId.value = row.id
  replyForm.value = { content: '' }
  showReplyDialog.value = true
}

const handleReply = async () => {
  if (!replyForm.value.content) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    await adminReplySuggestion(currentId.value, replyForm.value)
    ElMessage.success('回复成功')
    showReplyDialog.value = false
    fetchList()
  } catch {
    ElMessage.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}

const confirmDelete = id => {
  ElMessageBox.confirm('确定要删除该建议吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    handleDelete(id)
  })
}

const handleDelete = async id => {
  try {
    await deleteSuggestion(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.admin-suggestion-list {
  max-width: 1000px;
  margin: 40px auto;
}
</style> 