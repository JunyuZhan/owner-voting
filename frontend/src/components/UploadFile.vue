<template>
  <el-upload
    class="upload-demo"
    action="/api/owner/verification/upload"
    :show-file-list="false"
    :on-success="handleSuccess"
    :before-upload="beforeUpload"
    :headers="headers"
    :data="{}"
    :with-credentials="true"
  >
    <el-button type="primary">选择文件</el-button>
    <span v-if="fileName" style="margin-left: 10px;">{{ fileName }}</span>
  </el-upload>
</template>

<script setup>
import { ref, watch } from 'vue'
const props = defineProps({
  fileId: Number
})
const emit = defineEmits(['update:file-id'])
const fileName = ref('')
const headers = {}
const beforeUpload = (file) => {
  fileName.value = file.name
  return true
}
const handleSuccess = (res) => {
  // 假设后端返回文件ID
  emit('update:file-id', res.data || res)
}
watch(() => props.fileId, (val) => {
  if (!val) fileName.value = ''
})
</script> 