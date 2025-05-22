<template>
  <AppCard title="公告列表" subtitle="社区最新公告，及时了解重要信息">
    <el-table :data="list" style="width: 100%" v-loading="loading" border stripe>
      <template #empty>
        <div class="empty-state">
          <el-empty description="暂无公告" />
        </div>
      </template>
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column prop="type" label="类型" min-width="100">
        <template #default="{ row }">
          <el-tag :type="typeTag(row.type)">{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishedAt" label="发布时间" min-width="140" />
      <el-table-column label="操作" min-width="100">
        <template #default="scope">
          <el-button size="small" @click="goDetail(scope.row.id)" round>详情</el-button>
        </template>
      </el-table-column>
    </el-table>
  </AppCard>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAnnouncementList } from '../api/announcement'
import { ElMessage } from 'element-plus'
import AppCard from '../components/AppCard.vue'

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
const typeTag = (type) => {
  if (type === 'NOTICE') return 'info'
  if (type === 'FINANCIAL') return 'success'
  if (type === 'VOTE_RESULT') return 'primary'
  if (type === 'OTHER') return 'warning'
  return ''
}
onMounted(fetchList)
</script>

<style scoped>
.empty-state {
  padding: 48px 0;
}
</style> 