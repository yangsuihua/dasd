import request from './request'

/**
 * 观看历史相关API
 */

// 添加观看历史
export const addHistory = (videoId, data) => {
  return request({
    url: `/history/${videoId}`,
    method: 'post',
    data
  })
}

// 获取观看历史
export const getHistory = (params) => {
  return request({
    url: '/history/list',
    method: 'get',
    params
  })
}

// 清空观看历史
export const clearHistory = () => {
  return request({
    url: '/history/clear',
    method: 'delete'
  })
}

// 删除单条历史
export const deleteHistory = (videoId) => {
  return request({
    url: `/history/${videoId}`,
    method: 'delete'
  })
}
