<template>
  <div class="admin-dashboard">
    <el-card>
      <h2>数据统计</h2>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-statistic title="业主总数" :value="stats.ownerCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已认证业主" :value="stats.verifiedOwnerCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="投票总数" :value="stats.voteCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="建议总数" :value="stats.suggestionCount" />
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '../../api/admin'
const stats = ref({
  ownerCount: 0,
  verifiedOwnerCount: 0,
  voteCount: 0,
  suggestionCount: 0
})
const fetchStats = async () => {
  const res = await getStatistics()
  Object.assign(stats.value, res.data || res)
}
onMounted(fetchStats)
</script>

<style scoped>
.admin-dashboard {
  max-width: 1000px;
  margin: 40px auto;
}
</style> 