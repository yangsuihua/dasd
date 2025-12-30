<template>
  <aside class="sidebar">
    <nav>
      <ul>
        <li 
          v-for="item in navItems" 
          :key="item.label"
          class="nav-item" 
          :class="{ active: isActive(item.to) }"
          @click="$emit('navigate', item.to)"
        >
          {{ item.icon }} {{ item.label }}
        </li>
        
        <div class="divider"></div>
        
        <li 
          v-for="item in userItems" 
          :key="item.label"
          class="nav-item"
          :class="{ active: isActive(item.to) }"
          @click="$emit('navigate', item.to)"
        >
          {{ item.icon }} {{ item.label }}
        </li>
      </ul>
    </nav>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'

const emit = defineEmits(['navigate'])
const route = useRoute()

const navItems = [
  { to: '/', icon: '🏠', label: '首页推荐' },
  { to: '/explore', icon: '🔍', label: '发现' },
  { to: '/category/sports', icon: '🏅', label: '体育' },
  { to: '/category/anime', icon: '🌸', label: '动漫' },
  { to: '/category/gaming', icon: '🎮', label: '游戏' },
  { to: '/category/music', icon: '🎵', label: '音乐' },
  { to: '/category/food', icon: '🍔', label: '美食' }
]

const userItems = [
  { to: { name: 'profile', params: { userId: 'me', tab: 'following' } }, icon: '❤️', label: '我的关注' },
  { to: { name: 'profile', params: { userId: 'me', tab: 'favs' } }, icon: '⭐', label: '我的收藏' },
  { to: { name: 'profile', params: { userId: 'me', tab: 'history' } }, icon: '🕒', label: '观看历史' }
]

const isActive = (to) => {
  if (typeof to === 'string') {
    return route.path === to
  }
  if (to && to.name === 'profile') {
    return route.name === 'profile' && route.params.tab === to.params.tab
  }
  return false
}
</script>

<style scoped>
.sidebar {
  width: var(--sidebar-width);
  background: var(--bg-sidebar);
  border-right: 1px solid var(--border-color);
  overflow-y: auto;
  padding: 12px;
  flex-shrink: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  color: var(--text-secondary);
  font-weight: 600;
  margin-bottom: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-item:hover {
  background: var(--bg-hover);
  color: var(--text-main);
}

.nav-item.active {
  color: var(--primary-color);
  background: rgba(255, 71, 87, 0.1);
}

.divider {
  height: 1px;
  background: var(--border-color);
  margin: 10px 0;
}

ul {
  list-style: none;
  padding: 0;
}
</style>