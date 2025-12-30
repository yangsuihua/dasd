import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCurrentUser } from '../api/user'

export const useUserStore = defineStore('user', () => {
  // 当前用户
  const currentUser = ref(null)
  
  // 用户数据示例
  const usersDB = ref({})
  
  // 关注状态
  const followingStatus = ref({})
  
  // 加载当前用户信息
  const loadCurrentUser = async () => {
    try {
      const res = await getCurrentUser()
      currentUser.value = res.data
      return res.data
    } catch (error) {
      console.error('加载当前用户信息失败:', error)
      return null
    }
  }
  
  // 获取用户信息
  const getUserById = (userId) => {
    if (userId === 'me' && currentUser.value) {
      return currentUser.value
    }
    return usersDB.value[userId] || null
  }
  
  // 检查是否关注
  const isFollowing = (userId) => {
    return followingStatus.value[userId] || false
  }
  
  // 切换关注状态
  const toggleFollow = (userId) => {
    followingStatus.value[userId] = !followingStatus.value[userId]
  }
  
  // 初始化用户数据
  const initializeUsers = () => {
    // 初始化一些示例用户数据
    usersDB.value = {
      'u1': { 
        id: 'u1', 
        name: 'NBA Highlights', 
        handle: '@nba_official', 
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=NBA', 
        bio: '篮球精彩集锦',
        stats: { following: 23, followers: '10.2w', likes: '89.3k' },
        privacy: { showFavs: true, showLikes: false } 
      },
      'u2': { 
        id: 'u2', 
        name: 'AnimeFan', 
        handle: '@anime_lover', 
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Anime', 
        bio: '二次元聚集地',
        stats: { following: 456, followers: '5.8w', likes: '234k' },
        privacy: { showFavs: false, showLikes: true } 
      },
      'u3': { 
        id: 'u3', 
        name: 'LinkGaming', 
        handle: '@zelda_pro', 
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Link', 
        bio: '任天堂游戏实况',
        stats: { following: 89, followers: '2.3w', likes: '45.6k' },
        privacy: { showFavs: true, showLikes: true } 
      },
      'u4': { 
        id: 'u4', 
        name: 'Foodie', 
        handle: '@yummy_food', 
        avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Food', 
        bio: '深夜食堂',
        stats: { following: 234, followers: '8.9k', likes: '56.7k' },
        privacy: { showFavs: false, showLikes: false } 
      }
    }
    
    // 初始化关注状态
    followingStatus.value = {
      'u1': false,
      'u2': true,
      'u3': false,
      'u4': false
    }
  }
  
  return {
    currentUser,
    usersDB,
    getUserById,
    isFollowing,
    toggleFollow,
    loadCurrentUser,
    initializeUsers
  }
})
