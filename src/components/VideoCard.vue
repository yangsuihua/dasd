<template>
  <div class="video-card" @click="handleClick">
    <div class="thumbnail-container">
      <img :src="video.thumbnail" :alt="video.title" class="thumbnail">
      <span class="duration">{{ formatDuration(video.duration) }}</span>
      <div class="hover-preview">
        <svg class="play-icon" viewBox="0 0 24 24" fill="currentColor">
          <path d="M8 5v14l11-7z"/>
        </svg>
      </div>
    </div>
    <div class="video-info">
      <img :src="video.avatar" alt="作者" class="author-avatar">
      <div class="video-details">
        <h3 class="video-title">{{ video.title }}</h3>
        <p class="video-author">{{ video.author }}</p>
        <p class="video-stats">
          {{ formatViews(video.views) }} 次观看 · {{ video.time }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  video: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.video)
}

const formatDuration = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const formatViews = (views) => {
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return views.toString()
}
</script>

<style scoped>
.video-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.video-card:hover {
  transform: translateY(-4px);
}

.thumbnail-container {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 比例 */
  background: var(--card-bg);
  border-radius: 12px;
  overflow: hidden;
}

.thumbnail {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.duration {
  position: absolute;
  bottom: 8px;
  right: 8px;
  padding: 2px 6px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  font-size: 12px;
  border-radius: 4px;
}

.hover-preview {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.video-card:hover .hover-preview {
  opacity: 1;
}

.play-icon {
  width: 48px;
  height: 48px;
  color: white;
}

.video-info {
  display: flex;
  gap: 12px;
  margin-top: 12px;
}

.author-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.video-details {
  flex: 1;
  min-width: 0;
}

.video-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin: 0 0 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.video-author {
  font-size: 13px;
  color: var(--text-secondary);
  margin: 0 0 2px 0;
}

.video-stats {
  font-size: 12px;
  color: var(--text-tertiary);
  margin: 0;
}
</style>