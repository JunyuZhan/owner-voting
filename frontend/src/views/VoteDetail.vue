<template>
  <AppCard :title="detail.title" :subtitle="`状态：${detail.status} | 开始：${detail.startTime} | 结束：${detail.endTime}`">
    <el-divider />
    <div v-if="!voted" class="vote-section">
      <el-radio-group v-model="selectedOption">
        <el-radio v-for="opt in detail.options" :key="opt.id" :label="opt.id">{{ opt.text }}</el-radio>
      </el-radio-group>
      <el-button type="primary" @click="submitVote" :disabled="!selectedOption" class="mt-md" round>提交投票</el-button>
    </div>
    <div v-else class="vote-section">
      <el-alert title="您已投票" type="success" show-icon class="mb-md" />
      <el-button @click="fetchResult" round>查看投票结果</el-button>
    </div>
    <el-divider />
    <div v-if="result">
      <div class="section-title mb-sm">投票结果</div>
      <el-table :data="result.options" style="width: 100%" border stripe>
        <el-table-column prop="text" label="选项" min-width="120" />
        <el-table-column prop="count" label="票数" min-width="80" />
      </el-table>
    </div>
  </AppCard>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getVoteDetail, castVote, getVoteResult } from '../api/vote'
import { ElMessage } from 'element-plus'
import AppCard from '../components/AppCard.vue'

const route = useRoute()
const detail = ref({ options: [] })
const selectedOption = ref(null)
const voted = ref(false)
const result = ref(null)

const user = JSON.parse(localStorage.getItem('user') || '{}')

const fetchDetail = async () => {
  detail.value = await getVoteDetail(route.params.id)
}
const submitVote = async () => {
  try {
    await castVote(route.params.id, { optionId: selectedOption.value, ownerId: user.id })
    voted.value = true
    ElMessage.success('投票成功')
  } catch (e) {
    ElMessage.error('投票失败')
  }
}
const fetchResult = async () => {
  result.value = await getVoteResult(route.params.id)
}
onMounted(fetchDetail)
</script>

<style scoped>
.vote-section {
  margin-bottom: var(--app-spacing-lg);
}
</style> 