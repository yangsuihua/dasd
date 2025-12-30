import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import UploadPage from '../views/UploadPage.vue'
import ProfilePage from '../views/ProfilePage.vue'
import ExplorePage from '../views/ExplorePage.vue'
import VideoDetailPage from '../views/VideoDetailPage.vue'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginPage,
    meta: { noAuth: true }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterPage,
    meta: { noAuth: true }
  },
  {
    path: '/',
    name: 'home',
    component: HomePage
  },
  {
    path: '/explore',
    name: 'explore',
    component: ExplorePage
  },
  {
    path: '/upload',
    name: 'upload',
    component: UploadPage
  },
  {
    path: '/video/:id',
    name: 'video-detail',
    component: VideoDetailPage
  },
  {
    path: '/profile/:userId?/:tab?',
    name: 'profile',
    component: ProfilePage,
    props: route => ({
      userId: route.params.userId || 'me',
      tab: route.params.tab || 'uploads'
    })
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 如果是登录页面，直接放行
  if (to.meta.noAuth) {
    next()
    return
  }
  
  // 如果没有token，跳转到登录页
  if (!token) {
    next('/login')
    return
  }
  
  next()
})

export default router
