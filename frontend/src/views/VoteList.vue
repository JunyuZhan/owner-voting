<template>
  <el-card>
    <h2>投票列表</h2>
    <el-table :data="voteList" style="width: 100%" v-loading="loading">
      <template #empty>
        <div style="padding: 40px 0; color: #888;">暂无数据</div>
      </template>
      <el-table-column prop="title" label="议题" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="goDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVoteList } from '../api/vote'
import { ElMessage } from 'element-plus'

const voteList = ref([])
const router = useRouter()
const loading = ref(false)
const goDetail = (id) => {
  router.push(`/votes/${id}`)
}
const fetchList = async () => {
  loading.value = true
  try {
    voteList.value = await getVoteList()
  } catch (e) {
    ElMessage.error('获取投票列表失败')
  } finally {
    loading.value = false
  }
}
onMounted(fetchList)
</script> 