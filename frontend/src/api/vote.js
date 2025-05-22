import request from './request'

export function getVoteList(params) {
  return request.get('/vote-topic/votes', { params })
}

export function getVoteDetail(id) {
  return request.get(`/vote-topic/votes/${id}`)
}

export function castVote(data) {
  return request.post('/vote-record/add', data)
}

export async function getVoteResult(id) {
  const res = await request.get(`/vote-topic/votes/${id}/result`)
  return res.data.data
}

export function getMyVotes(ownerId) {
  return request.get(`/vote-record/by-owner/${ownerId}`)
}

export function deleteVote(id) {
  return request.delete(`/vote-topic/delete/${id}`)
}

export function addVote(data) {
  return request.post('/vote-topic/add', data)
}

export function editVote(id, data) {
  return request.put(`/vote-topic/update/${id}`, data)
} 