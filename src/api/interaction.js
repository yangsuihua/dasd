import request from './request'

/**
 * 互动相关API (点赞、收藏、评论)
 */

// 点赞视频
export const likeVideo = (videoId) => {
  return request({
    url: `/interaction/like/${videoId}`,
    method: 'post'
  })
}

// 取消点赞
export const unlikeVideo = (videoId) => {
  return request({
    url: `/interaction/like/${videoId}`,
    method: 'delete'
  })
}

// 检查是否点赞
export const checkLiked = (videoId) => {
  return request({
    url: `/interaction/like/check/${videoId}`,
    method: 'get'
  })
}

// 获取点赞列表
export const getLikedVideos = (params) => {
  return request({
    url: '/interaction/like/list',
    method: 'get',
    params
  })
}

// 收藏视频
export const favoriteVideo = (videoId) => {
  return request({
    url: `/interaction/favorite/${videoId}`,
    method: 'post'
  })
}

// 取消收藏
export const unfavoriteVideo = (videoId) => {
  return request({
    url: `/interaction/favorite/${videoId}`,
    method: 'delete'
  })
}

// 检查是否收藏
export const checkFavorited = (videoId) => {
  return request({
    url: `/interaction/favorite/check/${videoId}`,
    method: 'get'
  })
}

// 获取收藏列表
export const getFavoriteVideos = (params) => {
  return request({
    url: '/interaction/favorite/list',
    method: 'get',
    params
  })
}

// 获取评论列表
export const getComments = (videoId, params) => {
  return request({
    url: `/interaction/comment/${videoId}`,
    method: 'get',
    params
  })
}

// 发表评论
export const postComment = (data) => {
  return request({
    url: '/interaction/comment',
    method: 'post',
    data
  })
}

// 删除评论
export const deleteComment = (commentId) => {
  return request({
    url: `/interaction/comment/${commentId}`,
    method: 'delete'
  })
}

// 点赞评论
export const likeComment = (commentId) => {
  return request({
    url: `/interaction/comment/like/${commentId}`,
    method: 'post'
  })
}
