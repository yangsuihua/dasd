<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-box">
        <h1 class="app-title">🎬 视频平台</h1>
        <p class="subtitle">欢迎回来！</p>
        
        <!-- 登录表单 -->
        <div class="form">
          <div class="form-group">
            <label>用户名</label>
            <input 
              v-model="loginForm.username" 
              type="text" 
              placeholder="请输入用户名"
              @keyup.enter="handleLogin"
            >
          </div>
          <div class="form-group">
            <label>密码</label>
            <input 
              v-model="loginForm.password" 
              type="password" 
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
            >
          </div>
          <button class="btn-primary btn-block" @click="handleLogin" :disabled="loading">
            {{ loading ? '登录中...' : '登录' }}
          </button>
          
          <div class="quick-login">
            <p>快速登录（演示）</p>
            <button class="btn-outline btn-sm" @click="quickLogin">使用演示账号</button>
          </div>
          
          <div class="register-link">
            还没有账号？
            <router-link to="/register" class="link">立即注册</router-link>
          </div>
        </div>

        <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'

const router = useRouter()

const loading = ref(false)
const errorMsg = ref('')

const loginForm = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  errorMsg.value = ''
  
  if (!loginForm.value.username || !loginForm.value.password) {
    errorMsg.value = '请填写用户名和密码'
    return
  }

  loading.value = true
  
  try {
    const res = await login(loginForm.value)
    localStorage.setItem('token', res.data.accessToken)
    localStorage.setItem('userId', res.data.userId)
    router.push('/')
  } catch (error) {
    errorMsg.value = error.message || '登录失败，请检查用户名和密码'
    loading.value = false
  }
}

const quickLogin = () => {
  loginForm.value.username = 'test'
  loginForm.value.password = 'qqqqqq1'
  handleLogin()
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-container {
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.app-title {
  text-align: center;
  font-size: 32px;
  margin-bottom: 30px;
  color: #667eea;
}

.tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 12px;
  font-weight: 600;
  color: #999;
  cursor: pointer;
  position: relative;
  transition: color 0.3s;
}

.tab:hover {
  color: #667eea;
}

.tab.active {
  color: #667eea;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #667eea;
}

.subtitle {
  text-align: center;
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #666;
}

.link {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
}

.link:hover {
  text-decoration: underline;
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

.form-group input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-block {
  width: 100%;
  margin-top: 10px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 14px;
  border-radius: 8px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: transform 0.2s;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.quick-login {
  margin-top: 20px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.quick-login p {
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
}

.btn-outline {
  background: white;
  border: 1px solid #667eea;
  color: #667eea;
  padding: 8px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.btn-outline:hover {
  background: #667eea;
  color: white;
}

.btn-sm {
  font-size: 12px;
  padding: 6px 16px;
}

.error-msg {
  margin-top: 15px;
  padding: 12px;
  background: #fee;
  color: #c33;
  border-radius: 8px;
  font-size: 13px;
  text-align: center;
}
</style>