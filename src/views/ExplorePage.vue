<template>
  <div class="view-explore">
    <!-- 搜索栏 -->
    <div class="search-header">
      <div class="search-box">
        <input 
          type="text" 
          class="search-input" 
          placeholder="搜索视频、用户..."
          v-model="searchKeyword"
          @keyup.enter="handleSearch"
          @input="handleSearchInput"
        >
        <button class="search-btn" @click="handleSearch">🔍</button>
      </div>
      
      <!-- 搜索建议 -->
      <div v-if="searchSuggestions.length > 0" class="search-suggestions">
        <div 
          v-for="suggestion in searchSuggestions" 
          :key="suggestion"
          class="suggestion-item"
          @click="selectSearchSuggestion(suggestion)"
        >
          {{ suggestion }}
        </div>
      </div>
      
      <!-- 热搜榜 -->
      <div v-if="!searchKeyword && hotSearches.length > 0" class="hot-searches">
        <div class="hot-searches-title">热搜榜</div>
        <div class="hot-searches-list">
          <div 
            v-for="(hotSearch, index) in hotSearches.slice(0, 10)" 
            :key="hotSearch"
            class="hot-search-item"
            @click="selectHotSearch(hotSearch)"
          >
            <span class="hot-rank" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</span>
            {{ hotSearch }}
          </div>
        </div>
      </div>
      
      <!-- 标签过滤 -->
      <div class="tags-filter">
        <div 
          v-for="tag in tags" 
          :key="tag.id"
          class="tag-item"
          :class="{ active: activeTag === tag.id }"
          @click="selectTag(tag.id)"
        >
          {{ tag.name }}
        </div>
      </div>
    </div>

    <!-- 视频网格 -->
    <div class="video-grid">
      <div 
        v-for="video in videos" 
        :key="video.id"
        class="video-card"
        @click="goToVideoDetail(video.id)"
      >
        <div class="video-cover">
          <img v-if="video.coverUrl" :src="video.coverUrl" alt="">
          <div v-else class="video-placeholder"></div>
          <div class="video-duration">{{ formatDuration(video.duration) }}</div>
          <div class="video-play-count">▶ {{ video.viewCount }}</div>
        </div>
        <div class="video-info">
          <h4>{{ video.title }}</h4>
          <div class="video-meta">
            <span>{{ video.username }}</span>
            <span>❤️ {{ video.likeCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore">
      <button class="btn-outline" @click="loadMore">加载更多</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getVideoList } from '../api/video'
import { searchVideos, searchUsers, getHotSearch, getSearchSuggestions } from '../api/search'

const router = useRouter()

const searchKeyword = ref('')
const activeTag = ref('all')
const videos = ref([])
const page = ref(1)
const hasMore = ref(true)
const loading = ref(false)
const hotSearches = ref([]) // 热搜榜数据
const searchSuggestions = ref([]) // 搜索建议数据

const tags = [
  { id: 'all', name: '全部' },
  { id: 'life', name: '生活' },
  { id: 'music', name: '音乐' },
  { id: 'game', name: '游戏' },
  { id: 'food', name: '美食' },
  { id: 'tech', name: '科技' },
  { id: 'anime', name: '动漫' },
  { id: 'sports', name: '体育' }
]

onMounted(() => {
  loadVideos()
  loadHotSearches() // 加载热搜榜
})

const loadVideos = async () => {
  try {
    loading.value = true
    // 如果有搜索关键词，则执行搜索而不是普通视频列表
    if (searchKeyword.value.trim()) {
      const res = await searchVideos(searchKeyword.value, { page: page.value, size: 12 })
      if (page.value === 1) {
        videos.value = res.data || []
      } else {
        videos.value = [...videos.value, ...(res.data || [])]
      }
      hasMore.value = res.data && res.data.length === 12
    } else {
      // 普通视频列表
      const res = await getVideoList({ page: page.value, size: 12, category: activeTag.value === 'all' ? null : activeTag.value })
      if (page.value === 1) {
        videos.value = res.data || []
      } else {
        videos.value = [...videos.value, ...(res.data || [])]
      }
      hasMore.value = res.data && res.data.length === 12
    }
  } catch (error) {
    console.error('加载视频失败:', error)
    videos.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 加载热搜榜
const loadHotSearches = async () => {
  try {
    const res = await getHotSearch()
    hotSearches.value = res.data || []
  } catch (error) {
    console.error('加载热搜榜失败:', error)
    hotSearches.value = []
  }
}

// 获取搜索建议
const fetchSearchSuggestions = async (keyword) => {
  if (!keyword.trim()) {
    searchSuggestions.value = []
    return
  }
  
  try {
    const res = await getSearchSuggestions(keyword)
    searchSuggestions.value = res.data || []
  } catch (error) {
    console.error('获取搜索建议失败:', error)
    searchSuggestions.value = []
  }
}

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    // 如果搜索关键词为空，重置为普通视频列表
    page.value = 1
    await loadVideos()
    return
  }
  
  try {
    loading.value = true
    page.value = 1
    const res = await searchVideos(searchKeyword.value, { page: 1, size: 12 })
    videos.value = res.data || []
    hasMore.value = res.data && res.data.length === 12
  } catch (error) {
    console.error('搜索失败:', error)
    videos.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

// 搜索框输入事件，用于获取搜索建议
const handleSearchInput = () => {
  fetchSearchSuggestions(searchKeyword.value)
}

// 选择热搜词进行搜索
const selectHotSearch = (keyword) => {
  searchKeyword.value = keyword
  handleSearch()
}

// 选择搜索建议进行搜索
const selectSearchSuggestion = (keyword) => {
  searchKeyword.value = keyword
  searchSuggestions.value = [] // 清空建议列表
  handleSearch()
}

const selectTag = (tagId) => {
  activeTag.value = tagId
  page.value = 1
  searchKeyword.value = '' // 清空搜索关键词
  searchSuggestions.value = [] // 清空搜索建议
  loadVideos()
}

const loadMore = () => {
  page.value++
  loadVideos()
}

const goToVideoDetail = (videoId) => {
  router.push(`/video/${videoId}`)
}

const formatDuration = (seconds) => {
  if (!seconds || seconds <= 0) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}
</script>

<style scoped>
.view-explore {
  height: 100%;
  overflow-y: auto;
  background: var(--bg-body);
}

.search-header {
  position: sticky;
  top: 0;
  background: var(--bg-body);
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  z-index: 10;
}

.search-box {
  display: flex;
  gap: 10px;
  max-width: 600px;
  margin: 0 auto 15px;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: var(--bg-input);
  color: var(--text-main);
  font-size: 14px;
}

.search-btn {
  padding: 10px 20px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
}

.search-suggestions {
  max-width: 600px;
  margin: 0 auto 15px;
}

.suggestion-item {
  padding: 8px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 15px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 13px;
  transition: 0.2s;
}

.suggestion-item:hover {
  background: var(--bg-hover);
}

.hot-searches {
  max-width: 600px;
  margin: 0 auto 15px;
}

.hot-searches-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
}

.hot-searches-list {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.hot-search-item {
  padding: 8px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 15px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 13px;
  transition: 0.2s;
}

.hot-search-item:hover {
  background: var(--bg-hover);
}

.hot-rank {
  margin-right: 8px;
  font-weight: bold;
}

.hot-rank.top-three {
  color: var(--primary-color);
}

.tags-filter {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  padding: 5px 0;
}

.tags-filter::-webkit-scrollbar {
  height: 4px;
}

.tag-item {
  padding: 6px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 15px;
  cursor: pointer;
  white-space: nowrap;
  font-size: 13px;
  transition: 0.2s;
}

.tag-item:hover {
  background: var(--bg-hover);
}

.tag-item.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 15px;
  padding: 20px;
}

.video-card {
  background: var(--bg-card);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: 0.2s;
}

.video-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px var(--shadow);
}

.video-cover {
  position: relative;
  width: 100%;
  padding-top: 133.33%; /* 3:4 的宽高比 */
  background: #000;
  overflow: hidden;
}

.video-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.3);
  font-size: 24px;
}

.video-duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-play-count {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
}

.video-info {
  padding: 12px;
}

.video-info h4 {
  font-size: 14px;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.video-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-secondary);
}

.load-more {
  text-align: center;
  padding: 20px;
}
</style>