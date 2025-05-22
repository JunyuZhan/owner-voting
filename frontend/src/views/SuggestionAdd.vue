<template>
  <div class="suggestion-add">
    <el-card>
      <h2>提交建议</h2>
      <el-form :model="form" label-width="80px" @submit.prevent="handleSubmit">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" />
        </el-form-item>
        <el-form-item label="匿名">
          <el-switch v-model="form.isAnonymous" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { addSuggestion } from '../api/suggestion'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const form = ref({
  title: '',
  content: '',
  isAnonymous: false
})
const loading = ref(false)
const router = useRouter()

const handleSubmit = async () => {
  loading.value = true
  try {
    await addSuggestion(form.value)
    ElMessage.success('提交成功')
    router.push('/suggestions')
  } catch {
    ElMessage.error('提交失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.suggestion-add {
  max-width: 500px;
  margin: 40px auto;
}
</style> 