import request from './request'

export function submitVerify(data) {
  return request.post('/owner/verification/submit', data)
}

export function getVerifyStatus(ownerId) {
  return request.get('/owner/verification/status', { params: { ownerId } })
}

export function changePassword(data) {
  // 假设后端接口为 POST /owner/change-password，参数：ownerId, oldPassword, newPassword
  return request.post('/owner/change-password', data)
}

export function updateProfile(data) {
  // 假设后端接口为 POST /owner/update-profile，参数：ownerId, name, phone 等
  return request.post('/owner/update-profile', data)
}

export function getOwnerList(params) {
  return request.get('/owner/list', { params })
}

export function reviewOwner(data) {
  // 业主认证审核接口
  return request.post('/owner/verification/review', data)
}

export function deleteOwner(id) {
  // 删除业主接口
  return request.delete(`/owner/delete/${id}`)
}

export function addOwner(data) {
  // 管理员新增业主接口
  return request.post('/owner/admin/add', data)
} 