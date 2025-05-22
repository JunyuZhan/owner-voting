<template>
  <div class="admin-log-list">
    <el-card>
      <h2>操作日志</h2>
      <el-table :data="list" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="userId" label="用户ID" />
        <el-table-column prop="userType" label="用户类型" />
        <el-table-column prop="operation" label="操作" />
        <el-table-column prop="ipAddress" label="IP地址" />
        <el-table-column prop="detail" label="详情" />
        <el-table-column prop="createdAt" label="时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
// 假设有 getLogList API
// import { getLogList } from '../../api/log'
const list = ref([])
const loading = ref(false)
const fetchList = async () => {
  loading.value = true
  try {
    // const res = await getLogList()
    // list.value = res.list || res.data?.list || []
    list.value = [] // 占位
  } finally {
    loading.value = false
  }
}
onMounted(fetchList)
</script>

<style scoped>
.admin-log-list {
  max-width: 1000px;
  margin: 40px auto;
}
</style> 