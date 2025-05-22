import request from './request'

export function getAnnouncementList(params) {
  return request.get('/announcement/all', { params })
}

export function getAnnouncementDetail(id) {
  return request.get(`/announcement/${id}`)
}

export async function markAnnouncementRead(id, ownerId) {
  return request.post(`/announcement-read/announcements/${id}/read`, null, { params: { ownerId } })
}

export function addAnnouncement(data) {
  return request.post('/announcement/add', data)
}

export function editAnnouncement(id, data) {
  return request.put(`/announcement/update/${id}`, data)
}

export function deleteAnnouncement(id) {
  return request.delete(`/announcement/delete/${id}`)
} 