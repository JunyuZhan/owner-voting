<template>
  <div class="admin-vote-list">
    <AppCard title="投票管理" subtitle="管理社区所有投票议题">
      <template #header>
        <div class="flex-between">
          <div class="app-card-title">投票管理</div>
          <el-button type="primary" @click="showAddDialog = true" round>新增投票</el-button>
        </div>
      </template>
      <el-table :data="list" style="width: 100%" v-loading="loading" border stripe>
        <template #empty>
          <div class="empty-state"><el-empty description="暂无投票" /></div>
        </template>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" min-width="140" />
        <el-table-column prop="endTime" label="结束时间" min-width="140" />
        <el-table-column label="操作" min-width="160">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)" round>编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row.id)" round>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </AppCard>

    <!-- 新增投票对话框 -->
    <el-dialog v-model="showAddDialog" title="新增投票" width="600px">
      <el-form :model="addForm" label-width="100px" :rules="formRules" ref="addFormRef">
        <el-form-item label="投票标题" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入投票标题" />
        </el-form-item>
        
        <el-form-item label="投票描述" prop="description">
          <el-input v-model="addForm.description" type="textarea" :rows="3" placeholder="请输入投票描述" />
        </el-form-item>
        
        <el-form-item label="所属小区">
          <el-select v-model="addForm.communityId" placeholder="请选择小区" style="width: 100%">
            <el-option label="示范小区" :value="1" />
            <el-option label="阳光花园" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="addForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="addForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>

        <!-- 投票选项 -->
        <el-form-item label="投票选项" prop="options">
          <div class="vote-options">
            <div v-for="(option, index) in addForm.options" :key="index" class="option-item">
              <el-input 
                v-model="option.optionText" 
                placeholder="请输入选项内容"
                class="option-input"
              />
              <el-button 
                v-if="addForm.options.length > 2" 
                type="danger" 
                size="small" 
                icon="Delete" 
                @click="removeOption(index, 'add')"
                circle
              />
            </div>
            <el-button type="primary" size="small" icon="Plus" @click="addOption('add')" plain>
              添加选项
            </el-button>
          </div>
        </el-form-item>

        <!-- 高级设置 -->
        <el-form-item label="高级设置">
          <div class="advanced-settings">
            <el-checkbox v-model="addForm.isAreaWeighted">按面积加权投票</el-checkbox>
            <el-checkbox v-model="addForm.isRealName">实名投票</el-checkbox>
            <el-checkbox v-model="addForm.isResultPublic">结果公开</el-checkbox>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelAdd">取消</el-button>
        <el-button type="primary" :loading="addLoading" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑投票对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑投票" width="600px">
      <el-form :model="editForm" label-width="100px" :rules="formRules" ref="editFormRef">
        <el-form-item label="投票标题" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入投票标题" />
        </el-form-item>
        
        <el-form-item label="投票描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入投票描述" />
        </el-form-item>
        
        <el-form-item label="所属小区">
          <el-select v-model="editForm.communityId" placeholder="请选择小区" style="width: 100%">
            <el-option label="示范小区" :value="1" />
            <el-option label="阳光花园" :value="2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="editForm.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="editForm.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>

        <!-- 投票选项 -->
        <el-form-item label="投票选项" prop="options">
          <div class="vote-options">
            <div v-for="(option, index) in editForm.options" :key="index" class="option-item">
              <el-input 
                v-model="option.optionText" 
                placeholder="请输入选项内容"
                class="option-input"
              />
              <el-button 
                v-if="editForm.options.length > 2" 
                type="danger" 
                size="small" 
                icon="Delete" 
                @click="removeOption(index, 'edit')"
                circle
              />
            </div>
            <el-button type="primary" size="small" icon="Plus" @click="addOption('edit')" plain>
              添加选项
            </el-button>
          </div>
        </el-form-item>

        <!-- 高级设置 -->
        <el-form-item label="高级设置">
          <div class="advanced-settings">
            <el-checkbox v-model="editForm.isAreaWeighted">按面积加权投票</el-checkbox>
            <el-checkbox v-model="editForm.isRealName">实名投票</el-checkbox>
            <el-checkbox v-model="editForm.isResultPublic">结果公开</el-checkbox>
          </div>
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
import AppCard from '../../components/AppCard.vue'

const list = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const addFormRef = ref()
const editFormRef = ref()

// 初始化表单数据
const initFormData = () => ({
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  communityId: 1,
  isAreaWeighted: false,
  isRealName: false,
  isResultPublic: true,
  options: [
    { optionText: '同意', sortOrder: 1 },
    { optionText: '不同意', sortOrder: 2 },
    { optionText: '弃权', sortOrder: 3 }
  ]
})

const addForm = ref(initFormData())
const addLoading = ref(false)
const showEditDialog = ref(false)
const editForm = ref(initFormData())
const editLoading = ref(false)

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入投票标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入投票描述', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getVoteList()
    console.log('获取投票列表响应:', res)
    // 修复数据处理逻辑：后端返回的是 {code: 200, data: [...], message: "success"}
    list.value = res.data || res.list || []
    console.log('处理后的投票列表:', list.value)
  } catch (error) {
    console.error('获取投票列表失败:', error)
    ElMessage.error('获取投票列表失败')
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

// 添加选项
const addOption = (type) => {
  const form = type === 'add' ? addForm.value : editForm.value
  const sortOrder = form.options.length + 1
  form.options.push({
    optionText: '',
    sortOrder: sortOrder
  })
}

// 移除选项
const removeOption = (index, type) => {
  const form = type === 'add' ? addForm.value : editForm.value
  form.options.splice(index, 1)
  // 重新排序
  form.options.forEach((option, idx) => {
    option.sortOrder = idx + 1
  })
}

// 验证投票选项
const validateOptions = (options) => {
  if (options.length < 2) {
    ElMessage.warning('至少需要2个投票选项')
    return false
  }
  
  for (let i = 0; i < options.length; i++) {
    if (!options[i].optionText.trim()) {
      ElMessage.warning(`请填写第${i + 1}个选项的内容`)
      return false
    }
  }
  
  return true
}

const handleAdd = async () => {
  try {
    await addFormRef.value.validate()
  } catch {
    return
  }

  if (!validateOptions(addForm.value.options)) {
    return
  }

  // 确保communityId不为空
  const submitData = {
    ...addForm.value,
    communityId: addForm.value.communityId || 1
  }
  console.log('提交新增投票数据:', submitData)

  addLoading.value = true
  try {
    await addVote(submitData)
    ElMessage.success('新增成功')
    showAddDialog.value = false
    addForm.value = initFormData()
    fetchList()
  } catch (error) {
    console.error('新增失败:', error)
    ElMessage.error('新增失败：' + (error.message || '未知错误'))
  } finally {
    addLoading.value = false
  }
}

const cancelAdd = () => {
  showAddDialog.value = false
  addForm.value = initFormData()
  addFormRef.value?.resetFields()
}

const openEdit = row => {
  console.log('编辑投票数据:', row)
  editForm.value = {
    ...row,
    communityId: row.communityId || 1, // 确保communityId不为空
    options: row.options || [
      { optionText: '同意', sortOrder: 1 },
      { optionText: '不同意', sortOrder: 2 },
      { optionText: '弃权', sortOrder: 3 }
    ]
  }
  console.log('编辑表单数据:', editForm.value)
  showEditDialog.value = true
}

const handleEdit = async () => {
  try {
    await editFormRef.value.validate()
  } catch {
    return
  }

  if (!validateOptions(editForm.value.options)) {
    return
  }

  editLoading.value = true
  try {
    await editVote(editForm.value.id, editForm.value)
    ElMessage.success('编辑成功')
    showEditDialog.value = false
    fetchList()
  } catch (error) {
    ElMessage.error('编辑失败：' + (error.message || '未知错误'))
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

const statusType = (status) => {
  if (status === '进行中') return 'success'
  if (status === '已结束') return 'info'  
  if (status === '未开始') return 'warning'
  return ''
}

onMounted(fetchList)
</script>

<style scoped>
.admin-vote-list {
  max-width: 1000px;
  margin: 40px auto;
}

.empty-state {
  padding: 48px 0;
}

.vote-options {
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 8px;
}

.option-input {
  flex: 1;
}

.advanced-settings {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.advanced-settings .el-checkbox {
  margin-right: 0;
}
</style>
