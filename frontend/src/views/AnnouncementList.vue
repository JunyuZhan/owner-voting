<template>
  <el-card>
    <h2>公告列表</h2>
    <el-table :data="list" style="width: 100%" v-loading="loading">
      <template #empty>
        <div style="padding: 40px 0; color: #888;">暂无数据</div>
      </template>
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="publishedAt" label="发布时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" @click="goDetail(scope.row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAnnouncementList } from '../api/announcement'
import { ElMessage } from 'element-plus'

const list = ref([])
const router = useRouter()
const loading = ref(false)
const goDetail = (id) => {
  router.push(`/announcements/${id}`)
}
const fetchList = async () => {
  loading.value = true
  try {
    list.value = await getAnnouncementList()
  } catch (e) {
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}
onMounted(fetchList)
</script> 