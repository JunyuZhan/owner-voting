import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

instance.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response && err.response.data && err.response.data.message) {
      window.$message?.error(err.response.data.message)
    }
    return Promise.reject(err)
  }
)

export default instance