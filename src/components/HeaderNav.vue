<template>
  <nav class="header">
    <div class="logo" @click="$emit('navigate', '/')">
      <svg class="icon" viewBox="0 0 24 24">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 14.5v-9l6 4.5-6 4.5z" />
      </svg>
      <span>VideoPro</span>
    </div>

    <div class="search-bar">
      <div class="search-group">
        <input type="text" placeholder="Search videos, users..." v-model="searchQuery" />
        <button class="search-btn">🔍</button>
      </div>
    </div>

    <div class="header-right">
      <button class="btn-primary" @click="$emit('navigate', '/upload')">Upload</button>

      <div class="user-menu-container">
        <img
          :src="currentUser.avatar || defaultAvatar"
          class="avatar"
          @click="toggleDropdown"
        />
        <div class="dropdown-menu" :class="{ show: dropdownOpen }">
          <div class="dropdown-item" @click="navigateProfile('me')">
            <span>👤 Profile</span>
          </div>
          <div class="dropdown-item" @click="navigateProfile('me', 'settings')">
            <span>⚙️ Settings</span>
          </div>
          <div class="divider"></div>
          <div class="dropdown-item" @click="$emit('toggle-theme')">
            <span>{{ isDarkMode ? '🌙 Dark mode' : '☀️ Light mode' }}</span>
          </div>
          <div class="divider"></div>
          <div class="dropdown-item" style="color: #ff4757;" @click="handleLogout">
            <span>🚪 Logout</span>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { logout } from '../api/auth'

const router = useRouter()
const props = defineProps({
  isDarkMode: Boolean
})

const emit = defineEmits(['navigate', 'toggle-theme'])

const userStore = useUserStore()
const searchQuery = ref('')
const dropdownOpen = ref(false)
const defaultAvatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=videopro'
const currentUser = computed(() => userStore.currentUser || {})

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

const navigateProfile = (userId, tab = 'uploads') => {
  emit('navigate', { name: 'profile', params: { userId, tab } })
  dropdownOpen.value = false
}

const handleLogout = async () => {
  try {
    await logout()
  } catch (error) {
    console.error('logout failed:', error)
  }

  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  dropdownOpen.value = false

  try {
    await router.replace('/login')
  } catch (e) {
    window.location.href = '/login'
  }
}

const handleClickOutside = (e) => {
  if (!e.target.closest('.user-menu-container')) {
    dropdownOpen.value = false
  }
}

document.addEventListener('click', handleClickOutside)

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.header {
  height: var(--header-height);
  background: var(--bg-header);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.logo {
  font-size: 22px;
  font-weight: 800;
  color: var(--primary-color);
  display: flex;
  gap: 8px;
  align-items: center;
  cursor: pointer;
}

.icon {
  width: 24px;
  height: 24px;
  fill: currentColor;
}

.search-bar {
  flex: 0 1 400px;
}

.search-group {
  display: flex;
  background: var(--bg-input);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid transparent;
}

.search-group:focus-within {
  border-color: var(--primary-color);
}

.search-group input {
  flex: 1;
  padding: 8px 16px;
  border: none;
  background: transparent;
  outline: none;
  color: var(--text-main);
}

.search-btn {
  padding: 0 16px;
  background: var(--bg-hover);
  color: var(--text-secondary);
  border: none;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-menu-container {
  position: relative;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  cursor: pointer;
  object-fit: cover;
}

.dropdown-menu {
  position: absolute;
  top: 50px;
  right: 0;
  width: 200px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 4px 12px var(--shadow);
  display: none;
  flex-direction: column;
  padding: 8px 0;
  z-index: 1001;
}

.dropdown-menu.show {
  display: flex;
}

.dropdown-item {
  padding: 10px 16px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
}

.dropdown-item:hover {
  background: var(--bg-hover);
}

.divider {
  height: 1px;
  background: var(--border-color);
  margin: 4px 0;
}
</style>
