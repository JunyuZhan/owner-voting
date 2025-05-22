<template>
  <div class="suggestion-detail">
    <AppCard v-if="detail" :title="detail.title" :subtitle="`状态：${detail.status} | 点赞数：${detail.likeCount}`">
      <div class="content mb-lg">{{ detail.content }}</div>
      <el-button type="success" @click="handleLike" round class="mb-lg">点赞</el-button>
      <el-divider />
      <div class="section-title mb-sm">回复</div>
      <el-skeleton v-if="repliesLoading" rows="3" animated />
      <div v-else>
        <div v-if="replies.length === 0" class="empty-state"><el-empty description="暂无回复" /></div>
        <el-timeline v-else>
          <el-timeline-item v-for="item in replies" :key="item.id" :timestamp="item.createdAt">
            <div>{{ item.content }}</div>
            <div style="font-size: 12px; color: #888;">{{ item.replierName || '匿名' }}</div>
          </el-timeline-item>
        </el-timeline>
      </div>
      <el-divider />
      <el-form :model="replyForm" label-width="60px" @submit.prevent="handleReply">
        <el-form-item label="回复">
          <el-input v-model="replyForm.content" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleReply" :loading="replyLoading" round>提交回复</el-button>
        </el-form-item>
      </el-form>
    </AppCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getSuggestionDetail, likeSuggestion, getSuggestionReplies, addSuggestionReply } from '../api/suggestion'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import AppCard from '../components/AppCard.vue'

const route = useRoute()
const detail = ref(null)
const replies = ref([])
const repliesLoading = ref(false)
const replyForm = ref({ content: '' })
const replyLoading = ref(false)

const fetchDetail = async () => {
  const res = await getSuggestionDetail(route.params.id)
  detail.value = res.data || res
}
const fetchReplies = async () => {
  repliesLoading.value = true
  try {
    const res = await getSuggestionReplies(route.params.id)
    replies.value = res.data || res
  } finally {
    repliesLoading.value = false
  }
}
onMounted(() => {
  fetchDetail()
  fetchReplies()
})

const handleLike = async () => {
  try {
    await likeSuggestion(route.params.id)
    ElMessage.success('点赞成功')
    fetchDetail()
  } catch {
    ElMessage.error('点赞失败')
  }
}

const handleReply = async () => {
  if (!replyForm.value.content) {
    ElMessage.warning('请输入回复内容')
    return
  }
  replyLoading.value = true
  try {
    await addSuggestionReply({
      content: replyForm.value.content,
      suggestion: { id: route.params.id }
    })
    ElMessage.success('回复成功')
    replyForm.value.content = ''
    fetchReplies()
  } catch {
    ElMessage.error('回复失败')
  } finally {
    replyLoading.value = false
  }
}
</script>

<style scoped>
.suggestion-detail {
  max-width: 700px;
  margin: 40px auto;
}
.content {
  color: var(--el-color-text-regular);
  font-size: var(--app-font-size-md);
  line-height: 1.8;
}
.empty-state {
  padding: 32px 0;
}
</style>
