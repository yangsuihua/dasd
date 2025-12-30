<template>
  <div class="scrollable-content">
    <!-- 个人头部 -->
    <div class="profile-header">
      <img :src="profileUser.avatar" class="profile-avatar-lg">
      <div class="profile-info">
        <h1>{{ profileUser.name }}</h1>
        <p class="handle">{{ profileUser.handle }}</p>
        <p class="bio">{{ profileUser.bio }}</p>
        <div class="profile-stats">
          <span><span class="stat-num">{{ profileUser.stats.following }}</span> 关注</span>
          <span><span class="stat-num">{{ profileUser.stats.followers }}</span> 粉丝</span>
          <span><span class="stat-num">{{ profileUser.stats.likes }}</span> 获赞</span>
        </div>
      </div>
      <div class="profile-actions">
        <button v-if="isOwnProfile" class="btn-outline" @click="activeTab = 'settings'">
          编辑资料
        </button>
        <button v-else 
          :class="['btn-outline', { 'btn-followed': isFollowing }]"
          @click="handleFollow">
          {{ isFollowing ? '已关注' : '关注' }}
        </button>
      </div>
    </div>

    <!-- 标签导航 -->
    <div class="profile-tabs">
      <div 
        v-for="tab in visibleTabs" 
        :key="tab.key"
        class="tab-btn"
        :class="{ active: activeTab === tab.key }"
        @click="activeTab = tab.key">
        {{ tab.label }}
      </div>
    </div>

    <!-- 标签内容 -->
    <div class="tab-content">
      <!-- 作品/收藏/点赞/历史 -->
      <div v-if="['uploads', 'favs', 'likes', 'history'].includes(activeTab)" class="video-grid">
        <div v-for="video in userVideos" :key="video.id" class="grid-card">
          <div class="card-cover" :style="{ background: video.coverColor || '#2f3640' }">
            <img v-if="video.coverUrl" :src="video.coverUrl" alt="" class="card-cover-img">
          </div>
          <div class="card-info">
            <div class="card-title">{{ video.title }}</div>
            <div class="card-meta">{{ video.viewCount }}观看 · {{ video.createdAt }}</div>
          </div>
        </div>
      </div>

      <!-- 关注/粉丝列表 -->
      <div v-if="['following', 'followers'].includes(activeTab)" class="user-list">
        <div v-for="user in displayUsers" :key="user.id" class="user-card">
          <div class="user-info-group">
            <img :src="user.avatar" class="user-avatar">
            <div>
              <div class="user-name">{{ user.name }}</div>
              <div class="user-bio">{{ user.bio }}</div>
            </div>
          </div>
          <button 
            :class="['btn-outline', { 'btn-followed': user.isFollowing }]"
            @click="toggleUserFollow(user.id)">
            {{ user.isFollowing ? '取消关注' : (activeTab === 'followers' ? '回关' : '关注') }}
          </button>
        </div>
      </div>

      <!-- 设置 -->
      <div v-if="activeTab === 'settings'" class="settings-box">
        <!-- 隐私管理 -->
        <h3>🔒 隐私管理</h3>
        <div class="privacy-box">
          <div class="switch-group">
            <span>允许他人查看我的收藏</span>
            <label class="switch">
              <input type="checkbox" v-model="privacy.showFavs">
              <span class="slider"></span>
            </label>
          </div>
          <div class="switch-group">
            <span>允许他人查看我的点赞</span>
            <label class="switch">
              <input type="checkbox" v-model="privacy.showLikes">
              <span class="slider"></span>
            </label>
          </div>
          <div class="privacy-hint">
            关闭后，其他用户访问您的主页时将无法看到对应的内容列表。
          </div>
        </div>

        <!-- 基本资料 -->
        <h3>基本资料</h3>
        <div class="form-group">
          <label class="form-label">更换头像</label>
          <div class="avatar-upload">
            <img :src="profileUser.avatar" class="current-avatar">
            <button class="btn-outline">上传新头像</button>
          </div>
        </div>
        
        <div class="form-group">
          <label class="form-label">昵称</label>
          <input type="text" class="form-input" v-model="editForm.name">
        </div>

        <!-- 安全设置 -->
        <h3>安全设置</h3>
        <div class="form-group">
          <label class="form-label">旧密码</label>
          <input type="password" class="form-input" placeholder="请输入当前密码">
        </div>
        <div class="form-group">
          <label class="form-label">新密码</label>
          <input type="password" class="form-input" placeholder="请输入新密码">
        </div>
        <div class="form-group">
          <label class="form-label">确认新密码</label>
          <input type="password" class="form-input" placeholder="请再次输入新密码">
        </div>
        
        <button class="btn-primary">保存更改</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getUserProfile, followUser, unfollowUser } from '../api/user'
import { getFollowingList, getFollowersList } from '../api/user'
import { getUserVideos } from '../api/video'

const route = useRoute()
const userStore = useUserStore()

const props = defineProps({
  userId: String,
  tab: String
})

// 响应式数据
const activeTab = ref(props.tab || 'uploads')
const privacy = ref({
  showFavs: true,
  showLikes: false
})

const editForm = ref({
  name: ''
})

const profileData = ref(null)
const followingList = ref([])
const followersList = ref([])
const userVideos = ref([])
const loading = ref(false)

// 计算属性
const isOwnProfile = computed(() => {
  const currentUserId = userStore.currentUser?.id?.toString()
  return !props.userId || props.userId === 'me' || props.userId === currentUserId
})

const profileUser = computed(() => {
  if (isOwnProfile.value) {
    return userStore.currentUser || {}
  }
  return profileData.value || {}
})

const isFollowing = computed(() => {
  if (isOwnProfile.value) return false
  return userStore.isFollowing(props.userId)
})

const allTabs = [
  { key: 'uploads', label: '作品' },
  { key: 'favs', label: '收藏' },
  { key: 'likes', label: '点赞' },
  { key: 'history', label: '历史' },
  { key: 'following', label: '关注' },
  { key: 'followers', label: '粉丝' },
  { key: 'settings', label: '更新信息' }
]

const visibleTabs = computed(() => {
  if (isOwnProfile.value) {
    return allTabs
  }
  // 其他人的主页不显示历史和设置
  return allTabs.filter(tab => !['history', 'settings'].includes(tab.key))
})

// 根据当前标签显示对应的用户列表
const displayUsers = computed(() => {
  if (activeTab.value === 'following') {
    return followingList.value
  } else if (activeTab.value === 'followers') {
    return followersList.value
  }
  return []
})

// 监听路由变化
watch(() => props.tab, (newTab) => {
  if (newTab) {
    activeTab.value = newTab
  }
})

// 方法
const handleFollow = async () => {
  if (isOwnProfile.value) return
  
  try {
    if (isFollowing.value) {
      const res = await unfollowUser(props.userId)
      if (res.code === 200) {
        userStore.toggleFollow(props.userId)
      }
    } else {
      const res = await followUser(props.userId)
      if (res.code === 200) {
        userStore.toggleFollow(props.userId)
      }
    }
  } catch (error) {
    console.error('关注操作失败:', error)
  }
}

// 切换用户关注状态
const toggleUserFollow = async (userId) => {
  try {
    // 在关注列表中查找
    const followingUser = followingList.value.find(u => u.id === userId)
    if (followingUser) {
      if (followingUser.isFollowing) {
        const res = await unfollowUser(userId)
        if (res.code === 200) {
          followingUser.isFollowing = false
        }
      } else {
        const res = await followUser(userId)
        if (res.code === 200) {
          followingUser.isFollowing = true
        }
      }
    }
    
    // 在粉丝列表中查找
    const followerUser = followersList.value.find(u => u.id === userId)
    if (followerUser) {
      if (followerUser.isFollowing) {
        const res = await unfollowUser(userId)
        if (res.code === 200) {
          followerUser.isFollowing = false
        }
      } else {
        const res = await followUser(userId)
        if (res.code === 200) {
          followerUser.isFollowing = true
        }
      }
    }
  } catch (error) {
    console.error('关注操作失败:', error)
  }
}

// 加载用户资料
const loadUserProfile = async () => {
  if (isOwnProfile.value) return
  
  try {
    loading.value = true
    const res = await getUserProfile(props.userId)
    if (res.code === 200 && res.data) {
      profileData.value = res.data
    }
  } catch (error) {
    console.error('加载用户资料失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载用户视频
const loadUserVideos = async () => {
  try {
    loading.value = true
    const userId = isOwnProfile.value ? userStore.currentUser?.id : props.userId
    if (!userId) return
    
    const res = await getUserVideos(userId, { page: 1, size: 12 })
    if (res.code === 200) {
      userVideos.value = res.data || []
    }
  } catch (error) {
    console.error('加载用户视频失败:', error)
    userVideos.value = []
  } finally {
    loading.value = false
  }
}

// 加载关注列表
const loadFollowingList = async () => {
  try {
    const res = await getFollowingList()
    if (res.code === 200) {
      followingList.value = (res.data || []).map(user => ({
        ...user,
        isFollowing: true // 关注列表中的用户默认已关注
      }))
    }
  } catch (error) {
    console.error('加载关注列表失败:', error)
    followingList.value = []
  }
}

// 加载粉丝列表
const loadFollowersList = async () => {
  try {
    const res = await getFollowersList()
    if (res.code === 200) {
      followersList.value = (res.data || []).map(user => ({
        ...user,
        isFollowing: userStore.isFollowing(user.id) // 检查当前用户是否关注了这个粉丝
      }))
    }
  } catch (error) {
    console.error('加载粉丝列表失败:', error)
    followersList.value = []
  }
}

// 监听标签变化，加载对应数据
watch(activeTab, (newTab) => {
  if (newTab === 'following') {
    loadFollowingList()
  } else if (newTab === 'followers') {
    loadFollowersList()
  } else if (['uploads', 'favs', 'likes', 'history'].includes(newTab)) {
    loadUserVideos()
  }
})

// 初始化
onMounted(() => {
  if (!isOwnProfile.value) {
    loadUserProfile()
  }
  editForm.value.name = profileUser.value?.name || ''
  
  // 加载默认标签的视频
  loadUserVideos()
})
</script>

<style scoped>
.scrollable-content {
  height: 100%;
  overflow-y: auto;
  padding: 40px;
}

.profile-header {
  display: flex;
  gap: 24px;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 30px;
  border-bottom: 1px solid var(--border-color);
}

.profile-avatar-lg {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid var(--bg-card);
}

.profile-info {
  flex: 1;
}

.profile-info h1 {
  font-size: 24px;
  margin-bottom: 5px;
}

.handle {
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.bio {
  font-size: 14px;
  max-width: 600px;
  margin-bottom: 15px;
}

.profile-stats {
  display: flex;
  gap: 20px;
  color: var(--text-secondary);
  font-size: 14px;
}

.stat-num {
  color: var(--text-main);
  font-weight: 700;
  font-size: 16px;
}

.profile-actions {
  margin-left: auto;
}

.btn-followed {
  background: var(--bg-input);
  color: var(--text-secondary);
}

/* Tabs */
.profile-tabs {
  display: flex;
  gap: 30px;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: 20px;
}

.tab-btn {
  padding: 10px 0;
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.tab-btn:hover {
  color: var(--text-main);
}

.tab-btn.active {
  color: var(--primary-color);
}

.tab-btn.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background: var(--primary-color);
}

/* Content */
.tab-content {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* Video Grid */
.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.grid-card {
  background: var(--bg-card);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s;
}

.grid-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px var(--shadow);
}

.card-cover {
  width: 100%;
  aspect-ratio: 16/9;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-info {
  padding: 10px;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-meta {
  font-size: 12px;
  color: var(--text-secondary);
}

/* User List */
.user-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: var(--bg-card);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.user-info-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.user-name {
  font-weight: 600;
}

.user-bio {
  font-size: 12px;
  color: var(--text-secondary);
}

/* Settings */
.settings-box {
  max-width: 500px;
}

.settings-box h3 {
  margin: 30px 0 20px;
  color: var(--primary-color);
}

.settings-box h3:first-child {
  margin-top: 0;
}

.privacy-box {
  background: var(--bg-input);
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.switch-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #4a4a4a;
  transition: 0.4s;
  border-radius: 34px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: 0.4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: var(--primary-color);
}

input:checked + .slider:before {
  transform: translateX(20px);
}

.privacy-hint {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 10px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 14px;
}

.form-input {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid var(--border-color);
  background: var(--bg-input);
  outline: none;
  color: var(--text-main);
}

.avatar-upload {
  display: flex;
  gap: 10px;
  align-items: center;
}

.current-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

/* Responsive */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }
  
  .profile-tabs {
    overflow-x: auto;
    gap: 20px;
  }
  
  .video-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>