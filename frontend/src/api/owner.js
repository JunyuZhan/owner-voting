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
  return request.get('/admin-user/all', { params })
}

export function reviewOwner(data) {
  // 假设后端接口为 POST /admin-user/owner-review
  return request.post('/admin-user/owner-review', data)
}

export function deleteOwner(id) {
  // 假设后端接口为 DELETE /admin-user/delete/{id}
  return request.delete(`/admin-user/delete/${id}`)
} 