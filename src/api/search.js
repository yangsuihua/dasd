import request from './request'

/**
 * 搜索相关API
 */

// 搜索视频
export const searchVideos = (keyword, params) => {
  return request({
    url: '/search/video',
    method: 'get',
    params: {
      keyword,
      ...params
    }
  })
}

// 搜索用户
export const searchUsers = (keyword, params) => {
  return request({
    url: '/search/user',
    method: 'get',
    params: {
      keyword,
      ...params
    }
  })
}

// 热搜榜
export const getHotSearch = () => {
  return request({
    url: '/search/hot',
    method: 'get'
  })
}

// 搜索建议
export const getSearchSuggestions = (keyword) => {
  return request({
    url: '/search/suggest',
    method: 'get',
    params: { keyword }
  })
}
