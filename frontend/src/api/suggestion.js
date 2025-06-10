import request from './request' // 假设有统一的request封装

export function getSuggestions(params) {
  return request.get('/suggestion/page', { params })
}

// 添加别名导出，兼容不同的调用方式
export function getSuggestionList(params) {
  return request.get('/suggestion/page', { params })
}

// 添加获取我的建议列表的函数
export function getMySuggestionList(ownerId) {
  return request.get(`/suggestion/by-owner/${ownerId}`)
}

export function getSuggestionDetail(id) {
  return request.get(`/suggestion/${id}`)
}

export function addSuggestion(data) {
  return request.post('/suggestion/add', data)
}

export function likeSuggestion(id) {
  return request.post(`/suggestion/suggestions/${id}/like`)
}

export function getSuggestionReplies(suggestionId) {
  return request.get(`/suggestion-reply/by-suggestion/${suggestionId}`)
}

export function addSuggestionReply(data) {
  return request.post('/suggestion-reply/add', data)
}

export function deleteSuggestion(id) {
  return request.delete(`/suggestion/delete/${id}`)
}

export function adminReplySuggestion(id, data) {
  // 假设后端接口为 POST /suggestion-reply/admin/suggestions/{id}/reply
  return request.post(`/suggestion-reply/admin/suggestions/${id}/reply`, data)
} 