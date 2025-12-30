<template>
  <div class="view-video-detail">
    <!-- 视频播放区 -->
    <div class="video-player">
      <div class="video-placeholder" :style="{ background: '#2c3e50' }">
        VIDEO PLAYER
      </div>
    </div>

    <!-- 视频信息 -->
    <div class="video-content">
      <div class="video-header">
        <h2>{{ video.title }}</h2>
        <div class="video-stats">
          <span>{{ video.viewCount }} 观看</span>
          <span>{{ video.createdAt }}</span>
        </div>
      </div>

      <!-- 视频操作 -->
      <div class="video-actions">
        <button 
          class="action-btn"
          :class="{ liked: isLiked }"
          @click="handleLike"
        >
          ❤️ {{ video.likeCount }}
        </button>
        <button class="action-btn">💬 {{ video.commentCount }}</button>
        <button 
          class="action-btn"
          :class="{ favorited: isFavorited }"
          @click="handleFavorite"
        >
          ⭐ {{ video.favoriteCount }}
        </button>
        <button class="action-btn">↗️ 分享</button>
      </div>

      <!-- 作者信息 -->
      <div class="author-card">
        <img :src="video.avatar" class="author-avatar">
        <div class="author-info">
          <h4>{{ video.username }}</h4>
          <p>{{ video.followerCount }} 粉丝</p>
        </div>
        <button 
          class="btn-follow"
          :class="{ following: isFollowing }"
          @click="handleFollow"
        >
          {{ isFollowing ? '已关注' : '+ 关注' }}
        </button>
      </div>

      <div class="video-description">
        <p>{{ video.description }}</p>
      </div>

      <!-- 评论区 -->
      <div class="comments-section">
        <h3>评论 {{ comments.length }}</h3>
        
        <!-- 评论输入框 -->
        <div class="comment-input-box">
          <textarea 
            v-model="commentText"
            class="comment-input"
            placeholder="说点什么..."
            rows="3"
          ></textarea>
          <button class="btn-primary" @click="handlePostComment">发表</button>
        </div>

        <!-- 评论列表 -->
        <div class="comments-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <img :src="comment.avatar" class="comment-avatar">
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.username }}</span>
                <span class="comment-time">{{ comment.createdAt }}</span>
              </div>
              <p class="comment-text">{{ comment.content }}</p>
              <div class="comment-actions">
                <button class="comment-action-btn" @click="handleLikeComment(comment.id)">
                  ❤️ {{ comment.likeCount }}
                </button>
                <button class="comment-action-btn" @click="handleReply(comment)">
                  💬 回复
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getVideoDetail } from '../api/video'
import { getComments, postComment, likeComment } from '../api/interaction'
import { likeVideo, favoriteVideo, unlikeVideo, unfavoriteVideo } from '../api/interaction'
import { followUser, unfollowUser } from '../api/user'

const route = useRoute()
const videoId = route.params.id

const video = ref({
  id: null,
  title: '',
  username: '',
  avatar: '',
  followerCount: 0,
  viewCount: 0,
  likeCount: 0,
  commentCount: 0,
  favoriteCount: 0,
  createdAt: '',
  description: '',
  userId: null
})

const comments = ref([])
const commentText = ref('')
const isLiked = ref(false)
const isFavorited = ref(false)
const isFollowing = ref(false)
const loading = ref(false)

onMounted(() => {
  loadVideoDetail()
  loadComments()
})

const loadVideoDetail = async () => {
  try {
    loading.value = true
    const res = await getVideoDetail(videoId)
    if (res.data) {
      video.value = {
        ...video.value,
        ...res.data,
        likeCount: res.data.likeCount || 0,
        commentCount: res.data.commentCount || 0,
        favoriteCount: res.data.favoriteCount || 0,
        viewCount: res.data.viewCount || 0,
        followerCount: res.data.followerCount || 0
      }
    }
  } catch (error) {
    console.error('加载视频失败:', error)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    loading.value = true
    const res = await getComments(videoId, { page: 1, size: 20 })
    comments.value = res.data || []
  } catch (error) {
    console.error('加载评论失败:', error)
    comments.value = []
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  try {
    if (isLiked.value) {
      await unlikeVideo(videoId)
      video.value.likeCount = Math.max(0, (video.value.likeCount || 0) - 1)
    } else {
      await likeVideo(videoId)
      video.value.likeCount = (video.value.likeCount || 0) + 1
    }
    isLiked.value = !isLiked.value
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleFavorite = async () => {
  try {
    if (isFavorited.value) {
      await unfavoriteVideo(videoId)
      video.value.favoriteCount = Math.max(0, (video.value.favoriteCount || 0) - 1)
    } else {
      await favoriteVideo(videoId)
      video.value.favoriteCount = (video.value.favoriteCount || 0) + 1
    }
    isFavorited.value = !isFavorited.value
  } catch (error) {
    console.error('收藏失败:', error)
  }
}

const handleFollow = async () => {
  try {
    if (isFollowing.value) {
      await unfollowUser(video.value.userId)
    } else {
      await followUser(video.value.userId)
    }
    isFollowing.value = !isFollowing.value
  } catch (error) {
    console.error('关注失败:', error)
  }
}

const handlePostComment = async () => {
  if (!commentText.value.trim()) return
  
  try {
    const res = await postComment({
      videoId: videoId,
      content: commentText.value
    })
    
    // 添加到评论列表顶部
    if (res.data) {
      comments.value.unshift(res.data)
      commentText.value = ''
      
      // 更新评论数
      video.value.commentCount = (video.value.commentCount || 0) + 1
    }
  } catch (error) {
    console.error('发表评论失败:', error)
  }
}

const handleLikeComment = async (commentId) => {
  try {
    await likeComment(commentId)
    
    // 更新评论点赞数
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) {
      comment.likeCount = (comment.likeCount || 0) + 1
    }
  } catch (error) {
    console.error('点赞评论失败:', error)
  }
}

const handleReply = (comment) => {
  // 回复评论逻辑
  commentText.value = `@${comment.username} `
}
</script>

<style scoped>
.view-video-detail {
  height: 100%;
  overflow-y: auto;
  background: var(--bg-body);
}

.video-player {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  background: #000;
}

.video-placeholder {
  width: 100%;
  padding-top: 56.25%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.3);
  font-size: 24px;
}

.video-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.video-header h2 {
  font-size: 20px;
  margin-bottom: 10px;
}

.video-stats {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: var(--text-secondary);
}

.video-actions {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}

.action-btn {
  flex: 1;
  padding: 10px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s;
}

.action-btn:hover {
  background: var(--bg-hover);
}

.action-btn.liked,
.action-btn.favorited {
  background: #ffe6e6;
  border-color: #ff4757;
  color: #ff4757;
}

.author-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: var(--bg-card);
  border-radius: 8px;
  margin-bottom: 15px;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
}

.author-info {
  flex: 1;
}

.author-info h4 {
  margin-bottom: 4px;
}

.author-info p {
  font-size: 13px;
  color: var(--text-secondary);
}

.btn-follow {
  padding: 8px 20px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.btn-follow.following {
  background: var(--bg-hover);
  color: var(--text-main);
  border: 1px solid var(--border-color);
}

.video-description {
  padding: 15px;
  background: var(--bg-card);
  border-radius: 8px;
  margin-bottom: 20px;
  line-height: 1.6;
}

.comments-section h3 {
  margin-bottom: 15px;
}

.comment-input-box {
  margin-bottom: 20px;
}

.comment-input {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background: var(--bg-input);
  color: var(--text-main);
  font-family: inherit;
  resize: vertical;
  margin-bottom: 10px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 15px 0;
  border-bottom: 1px solid var(--border-color);
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.comment-text {
  margin-bottom: 8px;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  gap: 15px;
}

.comment-action-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 13px;
}

.comment-action-btn:hover {
  color: var(--primary-color);
}
</style>
