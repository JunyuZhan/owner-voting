import request from './request'

// 房屋注册
export function registerHouse(data) {
  return request.post('/house/register', data)
}

// 获取业主的房屋列表
export function getOwnerHouses(ownerId) {
  return request.get(`/house/owner/${ownerId}`)
}

// 获取房屋详情
export function getHouseDetail(houseId) {
  return request.get(`/house/${houseId}`)
}

// 检查房屋位置冲突
export function checkHouseConflict(params) {
  return request.get('/house/check-conflict', { params })
}

// 检查投票资格
export const checkVotingEligibility = (ownerId) => {
  return request.get(`/api/house/voting-eligibility/${ownerId}`)
}

// 更新房屋信息
export const updateHouse = (houseId, data) => {
  return request.put(`/api/house/update/${houseId}`, data)
}

// 文件上传
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}