<template>
  <div class="suggestion-list">
    <el-card>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h2>业主建议</h2>
        <el-button type="primary" @click="$router.push('/suggestions/add')">提交建议</el-button>
      </div>
      <el-table :data="list" style="width: 100%" v-loading="loading">
        <template #empty>
          <div style="padding: 40px 0; color: #888;">暂无数据</div>
        </template>
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" />
        <el-table-column prop="likeCount" label="点赞数" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="goDetail(scope.row.id)">详情</el-button>
            <el-button size="small" type="success" :loading="likeLoadingId===scope.row.id" @click="handleLike(scope.row.id)">点赞</el-button>
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSuggestions, likeSuggestion } from '../api/suggestion'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

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
</script>

<style scoped>
.suggestion-list {
  max-width: 800px;
  margin: 40px auto;
}
</style> 