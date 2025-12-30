import request from './request'

/**
 * 用户相关API
 */

// 获取用户资料
export const getUserProfile = (userId) => {
  return request({
    url: `/user/profile/${userId}`,
    method: 'get'
  })
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return request({
    url: '/user/me',
    method: 'get'
  })
}

// 更新用户信息
export const updateUser = (data) => {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 更新头像
export const updateAvatar = (avatarUrl) => {
  return request({
    url: '/user/avatar',
    method: 'post',
    params: { avatarUrl }
  })
}

// 关注用户
export const followUser = (userId) => {
  return request({
    url: `/follow/${userId}`,
    method: 'post'
  })
}

// 取消关注
export const unfollowUser = (userId) => {
  return request({
    url: `/follow/${userId}`,
    method: 'delete'
  })
}

// 检查是否关注
export const checkFollowing = (userId) => {
  return request({
    url: `/follow/check/${userId}`,
    method: 'get'
  })
}

// 获取关注列表
export const getFollowingList = () => {
  return request({
    url: '/follow/following',
    method: 'get'
  })
}

// 获取粉丝列表
export const getFollowersList = () => {
  return request({
    url: '/follow/followers',
    method: 'get'
  })
}
