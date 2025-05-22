<template>
  <div class="suggestion-list">
    <AppCard title="业主建议" subtitle="欢迎提出宝贵建议，助力社区共治">
      <template #header>
        <div class="flex-between">
          <div class="app-card-title">业主建议</div>
          <el-button type="primary" @click="$router.push('/suggestions/add')" round>提交建议</el-button>
        </div>
      </template>
      <el-table :data="list" style="width: 100%" v-loading="loading" border stripe>
        <template #empty>
          <div class="empty-state">
            <el-empty description="暂无建议" />
          </div>
        </template>
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="status" label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" min-width="80" />
        <el-table-column label="操作" min-width="160">
          <template #default="scope">
            <el-button size="small" @click="goDetail(scope.row.id)" round>详情</el-button>
            <el-button size="small" type="success" :loading="likeLoadingId===scope.row.id" @click="handleLike(scope.row.id)" round>点赞</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchList"
        style="margin-top: 20px; text-align: right;"
      />
    </AppCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSuggestions, likeSuggestion } from '../api/suggestion'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import AppCard from '../components/AppCard.vue'

const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)
const router = useRouter()
const likeLoadingId = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getSuggestions({ page: page.value - 1, size: size.value })
    list.value = res.list || res.data?.list || []
    total.value = res.total || res.data?.total || 0
  } finally {
    loading.value = false
  }
}
onMounted(fetchList)

const goDetail = id => {
  router.push(`/suggestions/${id}`)
}

const handleLike = async id => {
  likeLoadingId.value = id
  try {
    await likeSuggestion(id)
    ElMessage.success('点赞成功')
    fetchList()
  } catch {
    ElMessage.error('点赞失败')
  } finally {
    likeLoadingId.value = null
  }
}
const statusType = (status) => {
  if (status === '待处理') return 'warning'
  if (status === '已采纳') return 'success'
  if (status === '已驳回') return 'danger'
  return ''
}
</script>

<style scoped>
.suggestion-list {
  max-width: 900px;
  margin: 40px auto;
}
.empty-state {
  padding: 48px 0;
}
</style> 