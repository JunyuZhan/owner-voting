<template>
  <el-card>
    <h2>{{ detail.title }}</h2>
    <div>类型：{{ detail.type }}</div>
    <div>发布时间：{{ detail.publishedAt }}</div>
    <el-divider />
    <div v-html="detail.content" style="margin-bottom: 16px;"></div>
    <div v-if="detail.attachments && detail.attachments.length">
      <h4>附件：</h4>
      <ul>
        <li v-for="file in detail.attachments" :key="file.id">
          <a :href="file.url" target="_blank">{{ file.originalName }}</a>
        </li>
      </ul>
    </div>
    <el-button type="success" @click="markRead" v-if="!read">标记为已读</el-button>
    <el-alert v-if="read" title="已读" type="success" show-icon style="margin-top: 16px;" />
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getAnnouncementDetail, markAnnouncementRead } from '../api/announcement'
import { ElMessage } from 'element-plus'

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
 