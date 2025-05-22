<template>
  <el-card>
    <h2>{{ detail.title }}</h2>
    <div>状态：{{ detail.status }}</div>
    <div>开始时间：{{ detail.startTime }}</div>
    <div>结束时间：{{ detail.endTime }}</div>
    <el-divider />
    <div v-if="!voted">
      <el-radio-group v-model="selectedOption">
        <el-radio v-for="opt in detail.options" :key="opt.id" :label="opt.id">{{ opt.text }}</el-radio>
      </el-radio-group>
      <el-button type="primary" @click="submitVote">提交投票</el-button>
    </div>
    <div v-else>
      <el-alert title="您已投票" type="success" show-icon />
      <el-button @click="fetchResult">查看投票结果</el-button>
    </div>
    <el-divider />
    <div v-if="result">
      <h3>投票结果</h3>
      <el-table :data="result.options" style="width: 100%">
        <el-table-column prop="text" label="选项" />
        <el-table-column prop="count" label="票数" />
      </el-table>
    </div>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getVoteDetail, castVote, getVoteResult } from '../api/vote'
import { ElMessage } from 'element-plus'

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