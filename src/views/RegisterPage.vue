<template>
  <div class="register-page">
    <!-- 背景动画 -->
    <div class="animated-bg">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>

    <div class="register-container">
      <!-- 左侧欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <h1 class="welcome-title">
            <span class="emoji">🎬</span>
            <span class="gradient-text">加入我们</span>
          </h1>
          <p class="welcome-desc">开启你的创作之旅，分享精彩视频</p>
          
          <div class="features">
            <div class="feature-item" v-for="(feature, index) in features" :key="index">
              <div class="feature-icon">{{ feature.icon }}</div>
              <div class="feature-text">
                <h3>{{ feature.title }}</h3>
                <p>{{ feature.desc }}</p>
              </div>
            </div>
          </div>

          <div class="already-member">
            已有账号？
            <router-link to="/login" class="login-link">立即登录</router-link>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="form-section">
        <div class="form-container">
          <h2 class="form-title">创建账号</h2>
          <p class="form-subtitle">填写信息完成注册</p>

          <!-- 进度指示器 -->
          <div class="progress-steps">
            <div 
              class="step" 
              :class="{ active: currentStep >= 1, completed: currentStep > 1 }"
            >
              <div class="step-number">1</div>
              <div class="step-label">基本信息</div>
            </div>
            <div class="step-line" :class="{ active: currentStep > 1 }"></div>
            <div 
              class="step" 
              :class="{ active: currentStep >= 2, completed: currentStep > 2 }"
            >
              <div class="step-number">2</div>
              <div class="step-label">账号密码</div>
            </div>
            <div class="step-line" :class="{ active: currentStep > 2 }"></div>
            <div 
              class="step" 
              :class="{ active: currentStep >= 3 }"
            >
              <div class="step-number">3</div>
              <div class="step-label">完成</div>
            </div>
          </div>

          <!-- 步骤1：基本信息 -->
          <div v-show="currentStep === 1" class="step-content">
            <div class="form-group">
              <label>用户名</label>
              <div class="input-wrapper">
                <span class="input-icon">👤</span>
                <input 
                  v-model="registerForm.username" 
                  type="text" 
                  placeholder="设置你的用户名"
                  @input="validateUsername"
                >
                <span v-if="validation.username" class="validation-icon">✓</span>
              </div>
              <div v-if="errors.username" class="error-hint">{{ errors.username }}</div>
            </div>

            <div class="form-group">
              <label>邮箱</label>
              <div class="input-wrapper">
                <span class="input-icon">📧</span>
                <input 
                  v-model="registerForm.email" 
                  type="email" 
                  placeholder="your@email.com"
                  @input="validateEmail"
                >
                <span v-if="validation.email" class="validation-icon">✓</span>
              </div>
              <div v-if="errors.email" class="error-hint">{{ errors.email }}</div>
            </div>

            <div class="form-group">
              <label>手机号（可选）</label>
              <div class="input-wrapper">
                <span class="input-icon">📱</span>
                <input 
                  v-model="registerForm.phone" 
                  type="tel" 
                  placeholder="输入手机号"
                  @input="validatePhone"
                >
                <span v-if="validation.phone" class="validation-icon">✓</span>
              </div>
            </div>

            <button class="btn-next" @click="nextStep" :disabled="!canProceedStep1">
              下一步 →
            </button>
          </div>

          <!-- 步骤2：账号密码 -->
          <div v-show="currentStep === 2" class="step-content">
            <div class="form-group">
              <label>密码</label>
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  v-model="registerForm.password" 
                  :type="showPassword ? 'text' : 'password'"
                  placeholder="设置密码（至少6位）"
                  @input="validatePassword"
                >
                <span class="toggle-password" @click="showPassword = !showPassword">
                  {{ showPassword ? '👁️' : '👁️‍🗨️' }}
                </span>
              </div>
              <div class="password-strength">
                <div class="strength-bar">
                  <div 
                    class="strength-fill" 
                    :class="passwordStrength.class"
                    :style="{ width: passwordStrength.width }"
                  ></div>
                </div>
                <span class="strength-text">{{ passwordStrength.text }}</span>
              </div>
            </div>

            <div class="form-group">
              <label>确认密码</label>
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  v-model="registerForm.confirmPassword" 
                  :type="showConfirmPassword ? 'text' : 'password'"
                  placeholder="再次输入密码"
                  @input="validateConfirmPassword"
                >
                <span class="toggle-password" @click="showConfirmPassword = !showConfirmPassword">
                  {{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}
                </span>
              </div>
              <div v-if="errors.confirmPassword" class="error-hint">{{ errors.confirmPassword }}</div>
            </div>

            <div class="form-actions">
              <button class="btn-back" @click="prevStep">← 上一步</button>
              <button class="btn-next" @click="handleRegister" :disabled="!canProceedStep2 || loading">
                {{ loading ? '注册中...' : '完成注册' }}
              </button>
            </div>
          </div>

          <!-- 步骤3：成功 -->
          <div v-show="currentStep === 3" class="step-content success-content">
            <div class="success-animation">
              <div class="success-checkmark">✓</div>
            </div>
            <h3 class="success-title">注册成功！</h3>
            <p class="success-message">欢迎加入视频平台，即将跳转...</p>
          </div>

          <div v-if="errorMsg && currentStep !== 3" class="error-msg">{{ errorMsg }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../api/auth'

const router = useRouter()

const currentStep = ref(1)
const loading = ref(false)
const errorMsg = ref('')

const registerForm = ref({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validation = ref({
  username: false,
  email: false,
  phone: false,
  password: false,
  confirmPassword: false
})

const errors = ref({
  username: '',
  email: '',
  confirmPassword: ''
})

const features = [
  { icon: '🎥', title: '上传视频', desc: '分享你的精彩瞬间' },
  { icon: '❤️', title: '互动社交', desc: '点赞评论，结识朋友' },
  { icon: '🔥', title: '热门推荐', desc: '发现更多优质内容' }
]

// 表单验证
const validateUsername = () => {
  const username = registerForm.value.username
  if (!username) {
    validation.value.username = false
    errors.value.username = ''
    return
  }
  if (username.length < 3) {
    validation.value.username = false
    errors.value.username = '用户名至少3个字符'
    return
  }
  if (username.length > 20) {
    validation.value.username = false
    errors.value.username = '用户名最多20个字符'
    return
  }
  validation.value.username = true
  errors.value.username = ''
}

const validateEmail = () => {
  const email = registerForm.value.email
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!email) {
    validation.value.email = false
    errors.value.email = ''
    return
  }
  if (!emailRegex.test(email)) {
    validation.value.email = false
    errors.value.email = '请输入有效的邮箱地址'
    return
  }
  validation.value.email = true
  errors.value.email = ''
}

const validatePhone = () => {
  const phone = registerForm.value.phone
  if (!phone) {
    validation.value.phone = false
    return
  }
  const phoneRegex = /^1[3-9]\d{9}$/
  validation.value.phone = phoneRegex.test(phone)
}

const validatePassword = () => {
  const password = registerForm.value.password
  validation.value.password = password.length >= 6
}

const validateConfirmPassword = () => {
  const { password, confirmPassword } = registerForm.value
  if (!confirmPassword) {
    validation.value.confirmPassword = false
    errors.value.confirmPassword = ''
    return
  }
  if (password !== confirmPassword) {
    validation.value.confirmPassword = false
    errors.value.confirmPassword = '两次密码不一致'
    return
  }
  validation.value.confirmPassword = true
  errors.value.confirmPassword = ''
}

// 密码强度
const passwordStrength = computed(() => {
  const password = registerForm.value.password
  if (!password) return { width: '0%', class: '', text: '' }
  
  let strength = 0
  if (password.length >= 6) strength++
  if (password.length >= 10) strength++
  if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++
  if (/\d/.test(password)) strength++
  if (/[^a-zA-Z\d]/.test(password)) strength++
  
  if (strength <= 1) return { width: '25%', class: 'weak', text: '弱' }
  if (strength <= 3) return { width: '50%', class: 'medium', text: '中等' }
  if (strength <= 4) return { width: '75%', class: 'good', text: '良好' }
  return { width: '100%', class: 'strong', text: '强' }
})

const canProceedStep1 = computed(() => {
  return validation.value.username && validation.value.email
})

const canProceedStep2 = computed(() => {
  return validation.value.password && validation.value.confirmPassword
})

const nextStep = () => {
  if (currentStep.value === 1 && canProceedStep1.value) {
    currentStep.value = 2
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const handleRegister = async () => {
  errorMsg.value = ''
  
  if (!canProceedStep2.value) return

  loading.value = true
  
  try {
    const res = await register({
      username: registerForm.value.username,
      password: registerForm.value.password,
      email: registerForm.value.email,
      phone: registerForm.value.phone
    })
    
    // 检查返回的数据是否存在
    if (!res || !res.data) {
      throw new Error('注册响应数据格式不正确')
    }
    
    // 检查必要的字段是否存在
    if (!res.data.accessToken || !res.data.userId) {
      throw new Error('注册响应缺少必要的认证信息')
    }
    
    localStorage.setItem('token', res.data.accessToken)
    localStorage.setItem('userId', res.data.userId)
    currentStep.value = 3
    setTimeout(() => {
      router.push('/')
    }, 2000)
  } catch (error) {
    console.error('注册失败:', error)
    errorMsg.value = error.message || '注册失败，请稍后重试'
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 背景动画 */
.animated-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 20s infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: -50px;
  left: 30%;
  animation-delay: 4s;
}

.circle-4 {
  width: 250px;
  height: 250px;
  bottom: 20%;
  right: 20%;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
  }
}

/* 主容器 */
.register-container {
  position: relative;
  display: flex;
  height: 100vh;
  max-width: 1200px;
  margin: 0 auto;
  z-index: 1;
}

/* 左侧欢迎区 */
.welcome-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.welcome-content {
  max-width: 500px;
}

.welcome-title {
  font-size: 48px;
  font-weight: 800;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.emoji {
  font-size: 60px;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.gradient-text {
  background: linear-gradient(45deg, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-desc {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 25px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  gap: 15px;
  align-items: flex-start;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(10px);
}

.feature-icon {
  font-size: 32px;
}

.feature-text h3 {
  font-size: 18px;
  margin-bottom: 5px;
}

.feature-text p {
  font-size: 14px;
  opacity: 0.8;
}

.already-member {
  text-align: center;
  font-size: 14px;
  opacity: 0.9;
}

.login-link {
  color: white;
  font-weight: 600;
  text-decoration: none;
  border-bottom: 2px solid white;
  margin-left: 5px;
  transition: all 0.3s;
}

.login-link:hover {
  opacity: 0.8;
}

/* 右侧表单区 */
.form-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.form-container {
  width: 100%;
  max-width: 500px;
  background: white;
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.form-subtitle {
  color: #666;
  margin-bottom: 30px;
  font-size: 14px;
}

/* 进度步骤 */
.progress-steps {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 40px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e0e0e0;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  transition: all 0.3s;
}

.step.active .step-number {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: scale(1.1);
}

.step.completed .step-number {
  background: #4caf50;
  color: white;
}

.step-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

.step.active .step-label {
  color: #667eea;
  font-weight: 600;
}

.step-line {
  flex: 1;
  height: 2px;
  background: #e0e0e0;
  margin: 0 10px;
  transition: all 0.3s;
}

.step-line.active {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

/* 表单内容 */
.step-content {
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  font-size: 18px;
}

.input-wrapper input {
  width: 100%;
  padding: 14px 45px 14px 50px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s;
}

.input-wrapper input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.validation-icon {
  position: absolute;
  right: 15px;
  color: #4caf50;
  font-size: 20px;
  animation: checkmark 0.3s;
}

@keyframes checkmark {
  from { transform: scale(0); }
  to { transform: scale(1); }
}

.toggle-password {
  position: absolute;
  right: 15px;
  cursor: pointer;
  font-size: 18px;
  user-select: none;
}

.error-hint {
  color: #f44336;
  font-size: 12px;
  margin-top: 5px;
  margin-left: 5px;
}

/* 密码强度 */
.password-strength {
  margin-top: 10px;
}

.strength-bar {
  height: 4px;
  background: #e0e0e0;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 5px;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s;
}

.strength-fill.weak { background: #f44336; }
.strength-fill.medium { background: #ff9800; }
.strength-fill.good { background: #2196f3; }
.strength-fill.strong { background: #4caf50; }

.strength-text {
  font-size: 12px;
  color: #666;
}

/* 按钮 */
.btn-next,
.btn-back {
  padding: 14px 32px;
  border: none;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-next {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-top: 10px;
}

.btn-next:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.btn-next:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-back {
  background: #f5f5f5;
  color: #666;
}

.btn-back:hover {
  background: #e0e0e0;
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.form-actions .btn-next,
.form-actions .btn-back {
  flex: 1;
  margin: 0;
}

/* 成功页面 */
.success-content {
  text-align: center;
  padding: 40px 0;
}

.success-animation {
  margin-bottom: 30px;
}

.success-checkmark {
  width: 80px;
  height: 80px;
  margin: 0 auto;
  background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
  animation: scaleIn 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes scaleIn {
  from { transform: scale(0) rotate(0deg); }
  to { transform: scale(1) rotate(360deg); }
}

.success-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.success-message {
  color: #666;
  font-size: 14px;
}

/* 错误提示 */
.error-msg {
  margin-top: 15px;
  padding: 12px;
  background: #fee;
  color: #c33;
  border-radius: 8px;
  font-size: 13px;
  text-align: center;
  border-left: 4px solid #f44336;
}

/* 响应式 */
@media (max-width: 968px) {
  .register-container {
    flex-direction: column;
  }
  
  .welcome-section {
    display: none;
  }
  
  .form-section {
    padding: 20px;
  }
}
</style>
