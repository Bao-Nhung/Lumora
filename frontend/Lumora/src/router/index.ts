import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('@/views/DashboardView.vue'),
    },
  ],
})

// Authentication navigation guard
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem('user')
  const user = userStr ? JSON.parse(userStr) : null

  if (to.path === '/dashboard') {
    if (!user) {
      next('/login')
    } else if (user.role !== 'ADMIN') {
      next('/')
    } else {
      next()
    }
  } else if (to.path === '/login' && user) {
    if (user.role === 'ADMIN') {
      next('/dashboard')
    } else {
      next('/')
    }
  } else {
    next()
  }
})

export default router
