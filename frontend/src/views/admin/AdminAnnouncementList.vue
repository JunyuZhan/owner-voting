<template>
  <div class="admin-announcement-list">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>公告管理</h2>
        <el-button type="primary" @click="showAddDialog = true">新增公告</el-button>
      </div>
      <el-table :data="list" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="publishedAt" label="发布时间" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <el-dialog v-model="showAddDialog" title="新增公告" width="500px">
      <el-form :model="addForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="addForm.title" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="addForm.type" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="addForm.content" type="textarea" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="addForm.publishedAt" type="datetime" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
    <el-dialog v-model="showEditDialog" title="编辑公告" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="editForm.type" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.content" type="textarea" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="editForm.publishedAt" type="datetime" />
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
import { getAnnouncementList, addAnnouncement, editAnnouncement, deleteAnnouncement } from '../../api/announcement'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const addForm = ref({ title: '', type: '', content: '', publishedAt: '' })
const addLoading = ref(false)
const showEditDialog = ref(false)
const editForm = ref({ id: '', title: '', type: '', content: '', publishedAt: '' })
const editLoading = ref(false)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementList()
    list.value = res.list || res.data?.list || []
  } finally {
    loading.value = false
  }
}

const handleAdd = async () => {
  if (!addForm.value.title || !addForm.value.type || !addForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  addLoading.value = true
  try {
    await addAnnouncement(addForm.value)
    ElMessage.success('新增成功')
    showAddDialog.value = false
    addForm.value = { title: '', type: '', content: '', publishedAt: '' }
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
  if (!editForm.value.title || !editForm.value.type || !editForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  editLoading.value = true
  try {
    await editAnnouncement(editForm.value.id, editForm.value)
    ElMessage.success('编辑成功')
    showEditDialog.value = false
    fetchList()
  } catch {
    ElMessage.error('编辑失败')
  } finally {
    editLoading.value = false
  }
}

const handleDelete = async id => {
  try {
    await deleteAnnouncement(id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    ElMessage.error('删除失败')
  }
}

const confirmDelete = id => {
  ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
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
.admin-announcement-list {
  max-width: 1000px;
  margin: 40px auto;
}
</style> 