<template>
  <div class="personal-center">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>个人中心</h2>
        <div>
          <el-button type="primary" @click="showEditDialog = true" style="margin-right: 10px;">编辑信息</el-button>
          <el-button type="primary" @click="showPwdDialog = true" style="margin-right: 10px;">修改密码</el-button>
          <el-button type="danger" @click="logout">退出登录</el-button>
        </div>
      </div>
      <el-descriptions title="基本信息" :column="1" border>
        <el-descriptions-item label="手机号">{{ user.phone }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ user.name }}</el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag :type="verifyStatusType">{{ verifyStatusText }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-divider />
      <h3>我的建议</h3>
      <el-table :data="mySuggestions" style="width: 100%" v-loading="loadingSuggestions">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="likeCount" label="点赞数" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="goSuggestionDetail(scope.row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-divider />
      <h3>我的投票</h3>
      <el-table :data="myVotes" style="width: 100%" v-loading="loadingVotes">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="topicTitle" label="投票主题" />
        <el-table-column prop="optionText" label="我的选择" />
        <el-table-column prop="voteTime" label="投票时间" />
      </el-table>
      <el-dialog v-model="showPwdDialog" title="修改密码" width="400px">
        <el-form :model="pwdForm" label-width="80px">
          <el-form-item label="原密码">
            <el-input v-model="pwdForm.oldPassword" type="password" />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="pwdForm.newPassword" type="password" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showPwdDialog = false">取消</el-button>
          <el-button type="primary" :loading="pwdLoading" @click="handleChangePwd">确定</el-button>
        </template>
      </el-dialog>
      <el-dialog v-model="showEditDialog" title="编辑信息" width="400px">
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="姓名">
            <el-input v-model="editForm.name" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="editForm.phone" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" :loading="editLoading" @click="handleEditProfile">保存</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getVerifyStatus } from '../api/owner'
import { getSuggestions } from '../api/suggestion'
import { useRouter } from 'vue-router'
import { getMyVotes } from '../api/vote'
import { changePassword, updateProfile } from '../api/owner'
import { ElMessage } from 'element-plus'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const verifyStatus = ref(null)
const mySuggestions = ref([])
const loadingSuggestions = ref(false)
const myVotes = ref([])
const loadingVotes = ref(false)
const router = useRouter()
const showPwdDialog = ref(false)
const showEditDialog = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '' })
const pwdLoading = ref(false)
const editForm = ref({ name: user.name, phone: user.phone })
const editLoading = ref(false)

const verifyStatusText = computed(() => {
  if (!verifyStatus.value) return '未认证'
  if (verifyStatus.value.status === 'APPROVED') return '已通过'
  if (verifyStatus.value.status === 'REJECTED') return '被驳回'
  if (verifyStatus.value.status === 'PENDING') return '待审核'
  return '未认证'
})
const verifyStatusType = computed(() => {
  if (!verifyStatus.value) return 'info'
  if (verifyStatus.value.status === 'APPROVED') return 'success'
  if (verifyStatus.value.status === 'REJECTED') return 'danger'
  if (verifyStatus.value.status === 'PENDING') return 'warning'
  return 'info'
})

const fetchVerifyStatus = async () => {
  if (!user.id) return
  verifyStatus.value = await getVerifyStatus(user.id)
}
const fetchMySuggestions = async () => {
  loadingSuggestions.value = true
  try {
    const res = await getSuggestions({ ownerId: user.id })
    mySuggestions.value = res.list || res.data?.list || []
  } finally {
    loadingSuggestions.value = false
  }
}
const fetchMyVotes = async () => {
  loadingVotes.value = true
  try {
    const res = await getMyVotes(user.id)
    myVotes.value = (res.data || res).map(r => ({
      topicTitle: r.topicTitle || r.voteTopic?.title || '',
      optionText: r.optionText || r.voteOption?.optionText || '',
      voteTime: r.voteTime || r.createdAt || ''
    }))
  } finally {
    loadingVotes.value = false
  }
}
onMounted(() => {
  fetchVerifyStatus()
  fetchMySuggestions()
  fetchMyVotes()
})
const goSuggestionDetail = id => {
  router.push(`/suggestions/${id}`)
}
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.replace('/login')
}
const handleChangePwd = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请输入完整信息')
    return
  }
  pwdLoading.value = true
  try {
    await changePassword({
      ownerId: user.id,
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    ElMessage.success('修改成功，请重新登录')
    showPwdDialog.value = false
    logout()
  } catch {
    ElMessage.error('修改失败')
  } finally {
    pwdLoading.value = false
    pwdForm.value.oldPassword = ''
    pwdForm.value.newPassword = ''
  }
}
const handleEditProfile = async () => {
  if (!editForm.value.name || !editForm.value.phone) {
    ElMessage.warning('请输入完整信息')
    return
  }
  editLoading.value = true
  try {
    await updateProfile({
      ownerId: user.id,
      name: editForm.value.name,
      phone: editForm.value.phone
    })
    ElMessage.success('修改成功')
    showEditDialog.value = false
    user.name = editForm.value.name
    user.phone = editForm.value.phone
    localStorage.setItem('user', JSON.stringify(user))
  } catch {
    ElMessage.error('修改失败')
  } finally {
    editLoading.value = false
  }
}
</script>

<style scoped>
.personal-center {
  max-width: 900px;
  margin: 40px auto;
}
</style> 