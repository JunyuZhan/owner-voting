<template>
  <AppCard :title="detail.title" :subtitle="`类型：${detail.type} | 发布时间：${detail.publishedAt}`">
    <el-divider />
    <div class="content" v-html="detail.content"></div>
    <div v-if="detail.attachments && detail.attachments.length" class="attachments">
      <div class="section-title mb-sm">附件</div>
      <el-link v-for="file in detail.attachments" :key="file.id" :href="file.url" target="_blank" type="primary" class="mr-md">
        <el-icon style="vertical-align: middle;"><document /></el-icon>
        {{ file.originalName }}
      </el-link>
    </div>
    <div class="mt-lg">
      <el-button type="success" @click="markRead" v-if="!read" round>标记为已读</el-button>
      <el-alert v-if="read" title="已读" type="success" show-icon class="mt-md" />
    </div>
  </AppCard>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAnnouncementDetail, markAnnouncementRead } from '../api/announcement'
import { ElMessage } from 'element-plus'
import AppCard from '../components/AppCard.vue'
import { Document } from '@element-plus/icons-vue'

const route = useRoute()
const detail = ref({ attachments: [] })
const read = ref(false)

const user = JSON.parse(localStorage.getItem('user') || '{}')

const fetchDetail = async () => {
  detail.value = await getAnnouncementDetail(route.params.id)
}
const markRead = async () => {
  try {
    await markAnnouncementRead(route.params.id, user.id)
    read.value = true
    ElMessage.success('已标记为已读')
  } catch (e) {
    ElMessage.error('标记失败')
  }
}
onMounted(fetchDetail)
</script>

<style scoped>
.content {
  margin-bottom: var(--app-spacing-lg);
  color: var(--el-color-text-regular);
  font-size: var(--app-font-size-md);
  line-height: 1.8;
}
.attachments {
  margin-bottom: var(--app-spacing-lg);
}
</style>
 