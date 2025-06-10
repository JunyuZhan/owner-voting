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
        <el-table-column prop="type" label="类型">
          <template #default="scope">
            {{ getTypeText(scope.row.type) }}
          </template>
        </el-table-column>
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
          <el-select v-model="addForm.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="通知" value="NOTICE" />
            <el-option label="投票结果" value="VOTE_RESULT" />
            <el-option label="财务公告" value="FINANCIAL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="小区">
          <el-select v-model="addForm.communityId" placeholder="请选择小区" style="width: 100%">
            <el-option label="全部小区" :value="null" />
            <el-option label="示范小区" :value="1" />
            <el-option label="阳光花园" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="addForm.isPinned" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="addForm.content" type="textarea" rows="4" />
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
          <el-select v-model="editForm.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option label="通知" value="NOTICE" />
            <el-option label="投票结果" value="VOTE_RESULT" />
            <el-option label="财务公告" value="FINANCIAL" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="小区">
          <el-select v-model="editForm.communityId" placeholder="请选择小区" style="width: 100%">
            <el-option label="全部小区" :value="null" />
            <el-option label="示范小区" :value="1" />
            <el-option label="阳光花园" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否置顶">
          <el-switch v-model="editForm.isPinned" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editForm.content" type="textarea" rows="4" />
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
const addForm = ref({ title: '', type: '', content: '', publishedAt: '', communityId: 1, isPinned: false })
const addLoading = ref(false)
const showEditDialog = ref(false)
const editForm = ref({ id: '', title: '', type: '', content: '', publishedAt: '', communityId: null, isPinned: false })
const editLoading = ref(false)

// 获取类型显示文本
const getTypeText = (type) => {
  const typeMap = {
    'NOTICE': '通知',
    'VOTE_RESULT': '投票结果',
    'FINANCIAL': '财务公告',
    'OTHER': '其他'
  }
  return typeMap[type] || type
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementList()
    console.log('获取公告列表响应:', res)
    list.value = res.data || res.list || []
    console.log('处理后的公告列表:', list.value)
  } catch (error) {
    console.error('获取公告列表失败:', error)
    ElMessage.error('获取公告列表失败')
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
    addForm.value = { title: '', type: '', content: '', publishedAt: '', communityId: 1, isPinned: false }
    fetchList()
  } catch {
    ElMessage.error('新增失败')
  } finally {
    addLoading.value = false
  }
}

const openEdit = row => {
  console.log('编辑公告数据:', row)
  editForm.value = { 
    ...row,
    // 确保communityId不为null，如果为null则设为1
    communityId: row.communityId || 1
  }
  console.log('编辑表单数据:', editForm.value)
  showEditDialog.value = true
}

const handleEdit = async () => {
  if (!editForm.value.title || !editForm.value.type || !editForm.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  // 确保communityId不为null
  const submitData = {
    ...editForm.value,
    communityId: editForm.value.communityId || 1
  }
  console.log('提交编辑数据:', submitData)
  
  editLoading.value = true
  try {
    await editAnnouncement(editForm.value.id, submitData)
    ElMessage.success('编辑成功')
    showEditDialog.value = false
    fetchList()
  } catch (error) {
    console.error('编辑失败:', error)
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