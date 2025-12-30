<template>
  <div :class="['app-container', { 'light-mode': !isDarkMode }]">
    <!-- 登录/注册页面，不显示导航栏 -->
    <template v-if="isAuthPage">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </template>
    
    <!-- 正常页面，显示导航栏 -->
    <template v-else>
      <HeaderNav 
        @navigate="handleNavigate" 
        @toggle-theme="isDarkMode = !isDarkMode"
        :is-dark-mode="isDarkMode"
      />
      
      <div class="main-container">
        <SideBar @navigate="handleNavigate" />
        <main class="content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </main>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import HeaderNav from './components/HeaderNav.vue'
import SideBar from './components/SideBar.vue'
import { useUserStore } from './stores/user'

const router = useRouter()
const route = useRoute()
const isDarkMode = ref(true)
const userStore = useUserStore()
const isLoadingUser = ref(false)

const isAuthPage = computed(() => route.path === '/login' || route.path === '/register')

const handleNavigate = (target) => {
  if (typeof target === 'string') {
    router.push(target)
  } else {
    router.push(target)
  }
}

const ensureCurrentUser = async () => {
  const token = localStorage.getItem('token')
  if (!token || isAuthPage.value || userStore.currentUser || isLoadingUser.value) {
    return
  }

  try {
    isLoadingUser.value = true
    await userStore.loadCurrentUser()
  } finally {
    isLoadingUser.value = false
  }
}

onMounted(() => {
  userStore.initializeUsers()
  ensureCurrentUser()
})

watch(() => route.path, () => {
  ensureCurrentUser()
})
</script>

<style>
:root {
  --primary-color: #ff4757;
  --bg-body: #121212;
  --bg-sidebar: #121212;
  --bg-header: #1e1e1e;
  --bg-card: #1e1e1e;
  --bg-input: #2c2c2c;
  --bg-hover: #2a2a2a;
  --text-main: #ffffff;
  --text-secondary: #aaaaaa;
  --border-color: #333333;
  --shadow: rgba(0,0,0,0.5);
  --header-height: 60px;
  --sidebar-width: 240px;
}

.light-mode {
  --bg-body: #f1f2f6;
  --bg-sidebar: #ffffff;
  --bg-header: #ffffff;
  --bg-card: #ffffff;
  --bg-input: #f1f2f6;
  --bg-hover: #f1f2f6;
  --text-main: #2f3542;
  --text-secondary: #57606f;
  --border-color: #dfe4ea;
  --shadow: rgba(0,0,0,0.1);
}

* { 
  box-sizing: border-box; 
  margin: 0; 
  padding: 0; 
}

body { 
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; 
}

.app-container {
  background-color: var(--bg-body);
  color: var(--text-main);
  min-height: 100vh;
  transition: background 0.3s, color 0.3s;
}

.main-container { 
  display: flex; 
  margin-top: var(--header-height); 
  height: calc(100vh - var(--header-height)); 
}

.content { 
  flex: 1; 
  position: relative; 
  background: var(--bg-body); 
  overflow: hidden; 
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.btn-primary { 
  background: var(--primary-color); 
  color: white; 
  padding: 8px 16px; 
  border-radius: 4px; 
  font-weight: 600; 
  border: none;
  cursor: pointer;
}

.btn-outline { 
  border: 1px solid var(--border-color); 
  color: var(--text-main); 
  padding: 6px 12px; 
  border-radius: 4px; 
  font-size: 13px; 
  transition: 0.2s;
  background: transparent;
  cursor: pointer;
}

.btn-outline:hover { 
  background: var(--bg-hover); 
}
</style>
