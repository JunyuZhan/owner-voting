import request from './request'

// 获取当前用户的小区信息
export function getCurrentCommunity() {
  return request.get('/community/current')
}

// 获取所有小区列表
export function getAllCommunities() {
  return request({
    url: '/community/public',
    method: 'get'
  })
}

// 获取业主的小区列表（包含申请状态）
export function getCommunityList(ownerId) {
  return request({
    url: `/community/owner/${ownerId}`,
    method: 'get'
  })
}

// 业主申请加入小区
export function applyCommunity(data) {
  return request({
    url: '/community/apply',
    method: 'post',
    data
  })
}

// 检查业主在小区的状态
export function checkOwnerStatus(ownerId, communityId) {
  return request({
    url: '/community/status',
    method: 'get',
    params: { ownerId, communityId }
  })
}

// ==================== 管理员接口 ====================

// 获取小区待审核申请列表
export function getPendingApplications(communityId) {
  return request({
    url: `/community/${communityId}/pending-applications`,
    method: 'get'
  })
}

// 审核业主加入小区申请
export function reviewApplication(data) {
  return request({
    url: '/community/admin/review-application',
    method: 'post',
    data
  })
}

// 获取小区基本信息和统计
export function getCommunityInfo(id) {
  return request({
    url: `/community/${id}/info`,
    method: 'get'
  })
}

// 创建新小区
export function createCommunity(data) {
  return request({
    url: '/community/admin/create',
    method: 'post',
    data
  })
}

// 删除小区（仅系统管理员）
export function deleteCommunity(id) {
  return request.delete(`/community/delete/${id}`)
}

// 更新小区信息
export function updateCommunity(id, data) {
  return request({
    url: `/community/admin/${id}`,
    method: 'put',
    data
  })
} 