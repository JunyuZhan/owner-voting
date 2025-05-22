<script setup>
// 其他导入...
import { inject } from 'vue';
import { http } from '@/plugins/axios';

// 注入错误处理函数
const showApiError = inject('showApiError');

// 修改提交投票的方法
const submitVote = async () => {
  if (!form.optionId) {
    ElMessage.warning('请选择一个选项');
    return;
  }
  
  loading.value = true;
  try {
    const response = await http.post('/api/vote-record/submit', {
      topicId: route.params.id,
      optionId: form.optionId,
      comment: form.comment
    });
    
    if (response.code === 200) {
      ElMessage.success('投票成功');
      voted.value = true;
      getVoteResult();
    } else {
      showApiError({
        response: {
          status: response.code,
          data: response
        }
      });
    }
  } catch (error) {
    // 使用错误处理器显示错误
    showApiError(error, submitVote);
  } finally {
    loading.value = false;
  }
};

// 修改获取投票详情的方法
const getVoteDetail = async () => {
  loading.value = true;
  try {
    const response = await http.get(`/api/vote-topic/votes/${route.params.id}`);
    if (response.code === 200) {
      topic.value = response.data;
    } else {
      showApiError({
        response: {
          status: response.code,
          data: response
        }
      });
    }
  } catch (error) {
    showApiError(error, getVoteDetail);
  } finally {
    loading.value = false;
  }
};

// 修改获取投票结果的方法
const getVoteResult = async () => {
  try {
    const response = await http.get(`/api/vote-topic/votes/${route.params.id}/result`);
    if (response.code === 200) {
      voteResult.value = response.data;
    } else {
      showApiError({
        response: {
          status: response.code,
          data: response
        }
      });
    }
  } catch (error) {
    showApiError(error, getVoteResult);
  }
};
</script> 