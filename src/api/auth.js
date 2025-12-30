import request from './request'

/**
 * 用户认证相关API
 */

// 用户登录
export const login = (data) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export const register = (data) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 刷新Token
export const refreshToken = (refreshToken) => {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data: { refreshToken }
  })
}

// 退出登录
export const logout = () => {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
