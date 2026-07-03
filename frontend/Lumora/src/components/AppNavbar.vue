<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { Sparkles, LogIn } from '@lucide/vue'

const router = useRouter()
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const navigateTo = (path: string) => {
  router.push(path)
}
</script>

<template>
  <nav class="navbar" :class="{ 'navbar-scrolled': isScrolled }">
    <div class="navbar-container">
      <!-- Logo -->
      <div class="logo" @click="navigateTo('/')">
        <div class="logo-icon-wrapper">
          <Sparkles class="logo-icon animate-pulse-slow" />
          <div class="logo-glow"></div>
        </div>
        <span class="logo-text">Lumora</span>
      </div>

      <!-- Action Button -->
      <div class="nav-actions">
        <router-link to="/login" class="login-btn">
          <LogIn :size="16" />
          <span>Sign In</span>
        </router-link>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 80px;
  z-index: 100;
  display: flex;
  align-items: center;
  border-bottom: 1px solid transparent;
  transition: var(--transition-smooth);
  background: rgba(9, 9, 11, 0.05);
}

.navbar-scrolled {
  height: 70px;
  background: var(--glass-bg);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--glass-border);
}

.navbar-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* Logo */
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.logo-icon-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  color: var(--pink);
  filter: drop-shadow(0 0 8px var(--pink));
  z-index: 2;
}

.logo-glow {
  position: absolute;
  width: 24px;
  height: 24px;
  background: var(--purple);
  filter: blur(10px);
  border-radius: 50%;
  opacity: 0.8;
  z-index: 1;
}

.logo-text {
  font-size: 1.4rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  background: linear-gradient(135deg, var(--white), rgba(255, 255, 255, 0.7));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  transition: var(--transition-fast);
}

.logo:hover .logo-text {
  background: linear-gradient(135deg, var(--purple), var(--pink));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Action Button */
.login-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9rem;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--glass-border);
  color: var(--white);
  transition: var(--transition-smooth);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.login-btn:hover {
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.15), rgba(6, 182, 212, 0.15));
  border-color: rgba(139, 92, 246, 0.35);
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.2);
  transform: translateY(-1px);
}
</style>
