<template>
  <AppCard title="投票列表" subtitle="参与社区治理，查看并参与当前开放的投票议题">
    <el-table :data="voteList" style="width: 100%" v-loading="loading" border stripe>
      <template #empty>
        <div class="empty-state">
          <el-empty description="暂无投票数据" />
        </div>
      </template>
      <el-table-column prop="title" label="议题" min-width="180" />
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" min-width="140" />
      <el-table-column prop="endTime" label="结束时间" min-width="140" />
      <el-table-column label="操作" min-width="100">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="goDetail(row.id)" round>详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </AppCard>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVoteList } from '../api/vote'
import { ElMessage } from 'element-plus'
import AppCard from '../components/AppCard.vue'

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
const statusType = (status) => {
  if (status === '进行中') return 'success'
  if (status === '已结束') return 'info'
  if (status === '未开始') return 'warning'
  return ''
}
onMounted(fetchList)
</script>

<style scoped>
.empty-state {
  padding: 48px 0;
}
</style> 