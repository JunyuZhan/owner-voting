<template>
  <div v-if="error" class="error-handler">
    <el-alert
      :title="error.title"
      :description="error.message"
      type="error"
      show-icon
      :closable="true"
      @close="clearError"
    >
      <template #default>
        <div v-if="error.details" class="error-details">
          <p>{{ error.details }}</p>
          <el-button v-if="error.retry" type="primary" size="small" @click="retryAction">
            重试
          </el-button>
        </div>
      </template>
    </el-alert>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue';
import { ElAlert, ElButton } from 'element-plus';

// 错误状态
const error = ref(null);
// 重试函数
const retryAction = ref(null);

// 显示错误信息
const showError = (errorInfo) => {
  error.value = {
    title: errorInfo.title || '操作失败',
    message: errorInfo.message || '发生错误，请稍后再试',
    details: errorInfo.details || null,
    retry: false
  };
  
  // 如果有重试函数
  if (errorInfo.retry && typeof errorInfo.retry === 'function') {
    error.value.retry = true;
    retryAction.value = errorInfo.retry;
  }
  
  // 自动关闭
  if (errorInfo.autoClose !== false) {
    setTimeout(() => {
      clearError();
    }, errorInfo.duration || 5000);
  }
};

// 显示API错误
const showApiError = (error, retry = null) => {
  const errorInfo = {
    title: '请求失败',
    message: '服务器处理请求时发生错误',
    details: null,
    retry: retry
  };
  
  // 处理不同类型的错误
  if (error.response) {
    // 服务器返回错误
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        errorInfo.message = '请求参数有误';
        errorInfo.details = data.message || '请检查输入的参数是否正确';
        break;
      case 401:
        errorInfo.message = '未授权，请重新登录';
        break;
      case 403:
        errorInfo.message = '没有权限执行此操作';
        break;
      case 404:
        errorInfo.message = '请求的资源不存在';
        break;
      case 409:
        errorInfo.message = data.message || '操作冲突，请刷新后重试';
        break;
      case 429:
        errorInfo.message = '请求过于频繁，请稍后再试';
        break;
      case 500:
        errorInfo.message = '服务器内部错误';
        if (process.env.NODE_ENV === 'development' && data.message) {
          errorInfo.details = data.message;
        }
        break;
      default:
        errorInfo.message = `服务器返回错误 (${status})`;
        break;
    }
    
    // 如果响应中有详细错误信息
    if (data && data.code && data.message) {
      errorInfo.details = `错误码: ${data.code}, 错误信息: ${data.message}`;
    }
  } else if (error.request) {
    // 请求已发送但没有收到响应
    errorInfo.message = '无法连接到服务器';
    errorInfo.details = '请检查您的网络连接或稍后再试';
  } else {
    // 请求配置错误
    errorInfo.message = '请求错误';
    errorInfo.details = error.message;
  }
  
  showError(errorInfo);
};

// 清除错误
const clearError = () => {
  error.value = null;
  retryAction.value = null;
};

// 执行重试操作
const retry = () => {
  if (retryAction.value && typeof retryAction.value === 'function') {
    retryAction.value();
  }
  clearError();
};

// 提供给子组件使用
provide('showError', showError);
provide('showApiError', showApiError);
provide('clearError', clearError);

// 导出方法供父组件使用
defineExpose({
  showError,
  showApiError,
  clearError
});
</script>

<style scoped>
.error-handler {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
  min-width: 300px;
  max-width: 450px;
}

.error-details {
  margin-top: 8px;
  font-size: 0.9em;
}
</style> 