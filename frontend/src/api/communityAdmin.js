import request from './request'

// ==================== 公开接口 ====================

// 提交小区管理员申请
export function submitCommunityAdminApplication(data) {
  return request({
    url: '/community-admin-application/submit',
    method: 'post',
    data
  })
}

// 查询申请状态
export function getApplicationStatus(phone) {
  return request({
    url: '/community-admin-application/status',
    method: 'get',
    params: { phone }
  })
}

// ==================== 系统管理员接口 ====================

// 获取待审核申请列表
export function getPendingApplications() {
  return request({
    url: '/community-admin-application/pending',
    method: 'get'
  })
}

// 根据状态获取申请列表
export function getApplications(status) {
  return request({
    url: '/community-admin-application/list',
    method: 'get',
    params: status ? { status } : {}
  })
}

// 获取申请详情
export function getApplicationDetail(id) {
  return request({
    url: `/community-admin-application/${id}`,
    method: 'get'
  })
}

// 审核申请
export function reviewApplication(data) {
  return request({
    url: '/community-admin-application/review',
    method: 'post',
    data
  })
}

// 获取申请统计信息
export function getApplicationStatistics() {
  return request({
    url: '/community-admin-application/statistics',
    method: 'get'
  })
} 