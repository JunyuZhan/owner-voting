<template>
  <div class="admin-vote-list">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>投票管理</h2>
        <el-button type="primary" @click="showAddDialog = true">新增投票</el-button>
      </div>
      <el-table :data="list" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="endTime" label="结束时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showAddDialog" title="新增投票" width="500px">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="addForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="addForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="addForm.startTime" type="datetime" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="addForm.endTime" type="datetime" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="showEditDialog" title="编辑投票" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="editForm.startTime" type="datetime" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="editForm.endTime" type="datetime" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="editLoading" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getVoteList, deleteVote, addVote, editVote } from '../../api/vote'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const addForm = ref({ title: '', description: '', startTime: '', endTime: '' })
const addLoading = ref(false)
const showEditDialog = ref(false)
const editForm = ref({ id: '', title: '', description: '', startTime: '', endTime: '' })
const editLoading = ref(false)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getVoteList()
    list.value = res.list || res.data?.list || []
  } finally {
    loading.value = false
  }
}

const handleDelete = async id => {
  try {
    await deleteVote(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

const handleAdd = async () => {
  if (!addForm.value.title || !addForm.value.startTime || !addForm.value.endTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  addLoading.value = true
  try {
    await addVote(addForm.value)
    ElMessage.success('新增成功')
    showAddDialog.value = false
    addForm.value = { title: '', description: '', startTime: '', endTime: '' }
    fetchList()
  } catch {
    ElMessage.error('新增失败')
  } finally {
    addLoading.value = false
  }
}

const openEdit = row => {
  editForm.value = { ...row }
  showEditDialog.value = true
}

const handleEdit = async () => {
  if (!editForm.value.title || !editForm.value.startTime || !editForm.value.endTime) {
    ElMessage.warning('请填写完整信息')
    return
  }
  editLoading.value = true
  try {
    await editVote(editForm.value.id, editForm.value)
    ElMessage.success('编辑成功')
    showEditDialog.value = false
    fetchList()
  } catch {
    ElMessage.error('编辑失败')
  } finally {
    editLoading.value = false
  }
}

const confirmDelete = id => {
  ElMessageBox.confirm('确定要删除该投票吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    handleDelete(id)
  })
}

onMounted(fetchList)
</script>

<style scoped>
.admin-vote-list {
  max-width: 1000px;
  margin: 40px auto;
}
</style>
