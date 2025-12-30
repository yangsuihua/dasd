<template>
  <div class="view-home" @wheel="handleScroll">
    <div 
      v-for="video in feedVideos" 
      :key="video.id"
      class="video-slide"
    >
      <div class="video-slide-inner">
        <!-- 视频内容占位 -->
        <div class="video-placeholder" :style="{ background: video.color }">
          VIDEO CONTENT
        </div>
        
        <!-- 右侧操作按钮 -->
        <div class="slide-actions">
          <!-- 作者头像 -->
          <div class="action-avatar-box" @click="navigateToProfile(video.userId)">
            <img :src="video.authorAvatar" class="action-avatar-img">
            <div 
              v-if="!isFollowing(video.userId)" 
              class="action-follow-plus"
              @click.stop="toggleFollow(video.userId)"
            >+</div>
          </div>

          <div class="action-btn" @click="toggleLike(video.id)">
            <div class="action-circle">??</div>
            <span>{{ video.likeCount }}</span>
          </div>
          
          <div class="action-btn">
            <div class="action-circle">??</div>
            <span>{{ video.commentCount }}</span>
          </div>
          
          <div class="action-btn">
            <div class="action-circle">↗?</div>
            <span>分享</span>
          </div>
        </div>
        
        <!-- 底部信息 -->
        <div class="slide-info">
          <h3 @click="goToVideoDetail(video.id)" style="cursor: pointer;">{{ video.authorName }}</h3>
          <p @click="goToVideoDetail(video.id)" style="cursor: pointer;">{{ video.title }} #推荐 #热门</p>
          <div class="music-info">?? 原始声音 - {{ video.authorName }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getVideoList } from '../api/video'
import { followUser, unfollowUser } from '../api/user'
import { likeVideo, unlikeVideo } from '../api/interaction'

const router = useRouter()
const userStore = useUserStore()

const currentIndex = ref(0)
const feedVideos = ref([])
const loading = ref(false)
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=videopro'

const loadFeedVideos = async () => {
  try {
    loading.value = true
    const res = await getVideoList({ page: 1, size: 10 })
    feedVideos.value = (res.data || []).map(video => ({
      ...video,
      authorName: video.username || '匿名用户',
      authorAvatar: video.avatar || defaultAvatar,
      likeCount: video.likeCount || 0,
      commentCount: video.commentCount || 0,
      isLiked: Boolean(video.isLiked)
    }))
  } catch (error) {
    console.error('加载推荐视频失败:', error)
  } finally {
    loading.value = false
  }
}

const isFollowing = (userId) => {
  return userStore.isFollowing(userId)
}

const toggleFollow = async (userId) => {
  try {
    if (isFollowing(userId)) {
      await unfollowUser(userId)
    } else {
      await followUser(userId)
    }
    userStore.toggleFollow(userId)
  } catch (error) {
    console.error('关注操作失败:', error)
  }
}

const toggleLike = async (videoId) => {
  try {
    const video = feedVideos.value.find(v => v.id === videoId)
    if (!video) return
    
    if (video.isLiked) {
      await unlikeVideo(videoId)
      video.isLiked = false
      video.likeCount = Math.max(0, video.likeCount - 1)
    } else {
      await likeVideo(videoId)
      video.isLiked = true
      video.likeCount += 1
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
  }
}

const navigateToProfile = (userId) => {
  router.push(`/profile/${userId}`)
}

const goToVideoDetail = (videoId) => {
  router.push(`/video/${videoId}`)
}

const handleScroll = (event) => {
  if (event.deltaY > 0 && currentIndex.value < feedVideos.value.length - 1) {
    currentIndex.value++
  } else if (event.deltaY < 0 && currentIndex.value > 0) {
    currentIndex.value--
  }
  
  const container = document.querySelector('.view-home')
  if (container) {
    container.scrollTo({
      top: currentIndex.value * window.innerHeight,
      behavior: 'smooth'
    })
  }
}

onMounted(() => {
  loadFeedVideos()
})
</script>

<style scoped>
.view-home {
  height: 100%;
  overflow-y: scroll;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
}

.view-home::-webkit-scrollbar {
  width: 0;
}

.video-slide {
  width: 100%;
  height: 100vh;
  scroll-snap-align: start;
  position: relative;
  display: flex;
  justify-content: center;
  background: black;
}

.video-slide-inner {
  width: 100%;
  max-width: 500px;
  height: 100%;
  position: relative;
  background: #000;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.2);
  font-size: 30px;
}

.slide-actions {
  position: absolute;
  right: 10px;
  bottom: 100px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
  color: white;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  cursor: pointer;
}

.action-circle {
  width: 45px;
  height: 45px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: 0.2s;
}

.action-circle:hover {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.3);
}

.action-avatar-box {
  position: relative;
  margin-bottom: 10px;
  cursor: pointer;
}

.action-avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 2px solid white;
  transition: transform 0.2s;
  object-fit: cover;
}

.action-avatar-box:hover .action-avatar-img {
  transform: scale(1.1);
}

.action-follow-plus {
  position: absolute;
  bottom: -5px;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 20px;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
}

.slide-info {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 20px 70px 40px 20px;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.9), transparent);
  color: white;
}

.slide-info h3 {
  margin-bottom: 8px;
}

.music-info {
  margin-top: 10px;
  font-size: 12px;
}
</style>
