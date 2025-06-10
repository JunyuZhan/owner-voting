import { ElMessage } from 'element-plus'

// 创建一个基础的请求函数
const request = async (url, options = {}) => {
  const token = localStorage.getItem('token')
  
  const defaultOptions = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    }
  }
  
  const finalOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers
    }
  }
  
  try {
    const response = await fetch(url, finalOptions)
    const data = await response.json()
    
    // 统一处理响应
    if (data.code === 200) {
      return data
    } else {
      ElMessage.error(data.message || '请求失败')
      throw new Error(data.message || '请求失败')
    }
  } catch (error) {
    console.error('请求错误:', error)
    ElMessage.error('网络错误')
    throw error
  }
}

// 封装常用的HTTP方法
request.get = (url, params = {}) => {
  const queryString = new URLSearchParams(params).toString()
  const fullUrl = queryString ? `${url}?${queryString}` : url
  return request(fullUrl)
}

request.post = (url, data = {}) => {
  return request(url, {
    method: 'POST',
    body: JSON.stringify(data)
  })
}

request.put = (url, data = {}) => {
  return request(url, {
    method: 'PUT',
    body: JSON.stringify(data)
  })
}

request.delete = (url) => {
  return request(url, {
    method: 'DELETE'
  })
}

export default request
