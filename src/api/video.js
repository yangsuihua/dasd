import request from './request'

/**
 * 视频相关API
 */

// 获取视频列表(推荐流)
export const getVideoList = (params) => {
  return request({
    url: '/video/feed',
    method: 'get',
    params
  })
}

// 获取视频详情
export const getVideoDetail = (videoId) => {
  return request({
    url: `/video/${videoId}`,
    method: 'get'
  })
}

// 上传视频
export const uploadVideo = (data) => {
  return request({
    url: '/video/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 发布视频
export const publishVideo = (data) => {
  return request({
    url: '/video/publish',
    method: 'post',
    data
  })
}

// 获取用户视频列表
export const getUserVideos = (userId, params) => {
  return request({
    url: `/video/user/${userId}`,
    method: 'get',
    params
  })
}

// 删除视频
export const deleteVideo = (videoId) => {
  return request({
    url: `/video/${videoId}`,
    method: 'delete'
  })
}

// 获取视频分类
export const getCategories = () => {
  return request({
    url: '/video/categories',
    method: 'get'
  })
}

// 上传封面
export const uploadCover = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/video/upload/cover',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
