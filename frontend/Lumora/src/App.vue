<script setup lang="ts">
import { reactive, provide, watch } from 'vue'
import AuroraBackground from '@/components/AuroraBackground.vue'
import ShootingStars from '@/components/ShootingStars.vue'
import CustomCursor from '@/components/CustomCursor.vue'
import Navbar from '@/components/AppNavbar.vue'
import Footer from '@/components/AppFooter.vue'

const DEFAULT_CONFIG = {
  speed: 3,
  intensity: 0.7,
  purpleBlobColor: '#8b5cf6',
  pinkBlobColor: '#ec4899',
  blueBlobColor: '#3b82f6',
  cyanBlobColor: '#06b6d4',
  preset: 'aurora'
}

// Load saved configuration or use default
const savedConfig = localStorage.getItem('aurora-customizer-settings')
const initialConfig = savedConfig ? JSON.parse(savedConfig) : { ...DEFAULT_CONFIG }

// Define the reactive configuration for the AuroraBackground
const auroraConfig = reactive(initialConfig)

// Save configuration when changed
watch(
  auroraConfig,
  (newConfig) => {
    localStorage.setItem('aurora-customizer-settings', JSON.stringify(newConfig))
  },
  { deep: true }
)

// Provide it to child components (e.g. HomeView can modify it, AuroraBackground consumes it)
provide('auroraConfig', auroraConfig)
</script>

<template>
  <div class="app-layout">
    <!-- Animated background & Stars -->
    <AuroraBackground
      :speed="auroraConfig.speed"
      :intensity="auroraConfig.intensity"
      :purpleBlobColor="auroraConfig.purpleBlobColor"
      :pinkBlobColor="auroraConfig.pinkBlobColor"
      :blueBlobColor="auroraConfig.blueBlobColor"
      :cyanBlobColor="auroraConfig.cyanBlobColor"
    />
    <ShootingStars />

    <!-- Navigation -->
    <Navbar />

    <!-- Main Content Area with Router View transitions -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <Footer />

    <!-- Custom Cursor Follower -->
    <CustomCursor />
  </div>
</template>

<style>
/* CSS imports and global styles should be loaded here or main.ts */
@import '@/assets/main.css';

.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-content {
  flex: 1;
}
</style>
