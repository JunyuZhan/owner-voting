<template>
  <div class="owner-verify">
    <el-card>
      <h2>业主实名认证</h2>
      <el-form :model="form" label-width="100px" @submit.prevent="handleSubmit">
        <el-form-item label="身份证正面">
          <UploadFile v-model:file-id="form.idCardFrontFileId" />
        </el-form-item>
        <el-form-item label="身份证反面">
          <UploadFile v-model:file-id="form.idCardBackFileId" />
        </el-form-item>
        <el-form-item label="房产证">
          <UploadFile v-model:file-id="form.houseCertFileId" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交认证</el-button>
        </el-form-item>
      </el-form>
      <el-divider />
      <el-skeleton v-if="loading" rows="4" animated style="margin: 20px 0;" />
      <div v-else-if="status">
        <el-alert :title="statusText" :type="statusType" show-icon />
        <div v-if="status.reviewComment">审核意见：{{ status.reviewComment }}</div>
        <div v-if="status.reviewerName">审核人：{{ status.reviewerName }}</div>
        <div v-if="status.reviewedAt">审核时间：{{ status.reviewedAt }}</div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import UploadFile from '../components/UploadFile.vue'
import { submitVerify, getVerifyStatus } from '../api/owner'

const user = JSON.parse(localStorage.getItem('user') || '{}')
const form = ref({
  ownerId: user.id || '',
  idCardFrontFileId: null,
  idCardBackFileId: null,
  houseCertFileId: null,
  remark: ''
})

const status = ref(null)
const statusText = computed(() => {
  if (!status.value) return ''
  if (status.value.status === 'APPROVED') return '认证已通过'
  if (status.value.status === 'REJECTED') return '认证被驳回'
  if (status.value.status === 'PENDING') return '待审核'
  return '未认证'
})
const statusType = computed(() => {
  if (!status.value) return 'info'
  if (status.value.status === 'APPROVED') return 'success'
  if (status.value.status === 'REJECTED') return 'error'
  if (status.value.status === 'PENDING') return 'warning'
  return 'info'
})

const loading = ref(false)

const fetchStatus = async () => {
  loading.value = true
  try {
    status.value = await getVerifyStatus(form.value.ownerId)
  } finally {
    loading.value = false
  }
}
onMounted(fetchStatus)

const handleSubmit = async () => {
  loading.value = true
  try {
    await submitVerify(form.value)
    ElMessage.success('认证申请已提交！')
    fetchStatus()
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.owner-verify {
  max-width: 500px;
  margin: 40px auto;
}
</style> 