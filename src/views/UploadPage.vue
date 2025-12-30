<template>
  <div class="scrollable-content">
    <div class="upload-form">
      <h2>发布新作品</h2>
      
      <!-- 视频文件 -->
      <div class="form-group">
        <label class="form-label">视频文件</label>
        <div class="file-drop-zone" @click="selectVideoFile" @drop="handleDrop" @dragover.prevent>
          <div class="upload-icon">📹</div>
          <p>点击或拖拽视频文件到此处</p>
          <div class="file-hint">支持 mp4, webm, mkv</div>
        </div>
      </div>

      <!-- 封面设置 -->
      <div class="form-group">
        <label class="form-label">封面设置</label>
        <div class="cover-upload-container">
          <div class="cover-preview">
            <img v-if="formData.coverUrl" :src="formData.coverUrl" alt="封面">
            <span v-else>封面预览</span>
          </div>
          <div class="cover-actions">
            <div class="cover-hint">
              上传引人注目的封面，吸引更多观众点击。
            </div>
            <button class="upload-btn-sm" @click="selectCoverImage">📷 上传图片</button>
            <button class="upload-btn-sm">🎞️ 截取视频画面</button>
          </div>
        </div>
      </div>

      <!-- 标题 -->
      <div class="form-group">
        <label class="form-label">标题</label>
        <input 
          type="text" 
          class="form-input" 
          placeholder="给视频起个吸引人的标题 (必填)"
          v-model="formData.title"
        >
      </div>

      <!-- 分类 -->
      <div class="form-group">
        <label class="form-label">分类 (类型)</label>
        <select class="form-select" v-model="formData.category">
          <option value="" disabled>请选择视频分类</option>
          <option value="life">生活日常</option>
          <option value="music">音乐舞蹈</option>
          <option value="game">游戏竞技</option>
          <option value="food">美食制作</option>
          <option value="tech">科技数码</option>
          <option value="anime">动漫二次元</option>
          <option value="sports">体育运动</option>
        </select>
      </div>

      <!-- 描述 -->
      <div class="form-group">
        <label class="form-label">简介描述</label>
        <textarea 
          class="form-textarea" 
          placeholder="介绍一下你的视频内容，添加 #标签 可以增加曝光哦..."
          v-model="formData.description"
        ></textarea>
      </div>

      <button class="btn-primary publish-btn" @click="handlePublish">立即发布</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { uploadVideo, uploadCover, publishVideo } from '../api/video'

const router = useRouter()

const formData = ref({
  videoFile: null,
  coverUrl: '',
  title: '',
  category: '',
  description: ''
})

const loading = ref(false)

const selectVideoFile = () => {
  // 触发文件选择
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'video/*'
  input.onchange = (e) => {
    formData.value.videoFile = e.target.files[0]
  }
  input.click()
}

const selectCoverImage = () => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = 'image/*'
  input.onchange = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        formData.value.coverUrl = e.target.result
      }
      reader.readAsDataURL(file)
    }
  }
  input.click()
}

const handleDrop = (e) => {
  e.preventDefault()
  const files = e.dataTransfer.files
  if (files.length > 0) {
    formData.value.videoFile = files[0]
  }
}

const handlePublish = async () => {
  if (!formData.value.title) {
    alert('请填写视频标题')
    return
  }
  
  if (!formData.value.videoFile) {
    alert('请选择视频文件')
    return
  }
  
  if (!formData.value.category) {
    alert('请选择视频分类')
    return
  }
  
  try {
    loading.value = true
    
    // 1. 上传视频文件
    const videoFormData = new FormData()
    videoFormData.append('file', formData.value.videoFile)
    const videoRes = await uploadVideo(videoFormData)
    const videoUrl = videoRes.data // 注意：根据后端接口，这里应该是直接返回URL字符串
    
    // 2. 上传封面（如果有）
    let coverUrl = ''
    if (formData.value.coverUrl && formData.value.coverUrl.startsWith('data:')) {
      // 如果是本地选择的图片，需要上传
      const coverFile = dataURLtoFile(formData.value.coverUrl, 'cover.jpg')
      const coverFormData = new FormData()
      coverFormData.append('file', coverFile)
      const coverRes = await uploadCover(coverFormData)
      coverUrl = coverRes.data // 注意：根据后端接口，这里应该是直接返回URL字符串
    } else if (formData.value.coverUrl) {
      // 如果已经是URL，则直接使用
      coverUrl = formData.value.coverUrl
    }
    
    // 3. 发布视频信息
    const publishData = {
      title: formData.value.title,
      description: formData.value.description,
      category: formData.value.category,
      videoUrl: videoUrl,
      coverUrl: coverUrl
    }
    
    const publishRes = await publishVideo(publishData)
    
    // 发布成功后跳转到个人主页
    if (publishRes.code === 200) {
      alert('视频发布成功！')
      router.push('/profile/me')
    } else {
      throw new Error(publishRes.message || '发布失败')
    }
  } catch (error) {
    console.error('发布失败:', error)
    alert('发布失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 将base64数据转换为File对象
const dataURLtoFile = (dataurl, filename) => {
  const arr = dataurl.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new File([u8arr], filename, { type: mime })
}
</script>

<style scoped>
.scrollable-content {
  height: 100%;
  overflow-y: auto;
  padding: 40px;
}

.upload-form {
  max-width: 700px;
  margin: 0 auto;
  background: var(--bg-card);
  padding: 30px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.upload-form h2 {
  margin-bottom: 20px;
  color: var(--text-main);
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  font-size: 14px;
  color: var(--text-main);
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 10px;
  border-radius: 6px;
  border: 1px solid var(--border-color);
  background: var(--bg-input);
  outline: none;
  color: var(--text-main);
  font-family: inherit;
}

.form-textarea {
  height: 120px;
  resize: vertical;
}

.file-drop-zone {
  border: 2px dashed var(--border-color);
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  color: var(--text-secondary);
  cursor: pointer;
  transition: 0.2s;
  background: var(--bg-input);
}

.file-drop-zone:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.upload-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.file-hint {
  font-size: 12px;
  margin-top: 5px;
  color: #666;
}

.cover-upload-container {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.cover-preview {
  width: 160px;
  height: 90px;
  background: #000;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-actions {
  flex: 1;
}

.cover-hint {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 8px;
}

.upload-btn-sm {
  display: inline-block;
  padding: 6px 12px;
  background: var(--bg-hover);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  margin-right: 8px;
  color: var(--text-main);
}

.upload-btn-sm:hover {
  background: var(--bg-input);
}

.publish-btn {
  width: 100%;
  padding: 12px;
  margin-top: 10px;
  font-size: 16px;
}
</style>