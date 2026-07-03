<script setup lang="ts">
import { inject, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Sparkles, Zap, Shield, ChevronRight, Eye, RefreshCw, Palette } from '@lucide/vue'

const router = useRouter()

interface AuroraConfig {
  speed: number
  intensity: number
  purpleBlobColor: string
  pinkBlobColor: string
  blueBlobColor: string
  cyanBlobColor: string
}

// Inject shared aurora configuration
const auroraConfig = inject<AuroraConfig>('auroraConfig', {
  speed: 3,
  intensity: 0.7,
  purpleBlobColor: 'var(--purple)',
  pinkBlobColor: 'var(--pink)',
  blueBlobColor: 'var(--blue)',
  cyanBlobColor: 'var(--cyan)',
})

// Local options for the customizer
const selectedPreset = ref('aurora')
const presets = {
  aurora: {
    purpleBlobColor: 'var(--purple)',
    pinkBlobColor: 'var(--pink)',
    blueBlobColor: 'var(--blue)',
    cyanBlobColor: 'var(--cyan)',
  },
  neonFire: {
    purpleBlobColor: '#F59E0B', // Orange
    pinkBlobColor: '#EF4444', // Red
    blueBlobColor: '#EC4899', // Pink
    cyanBlobColor: '#8B5CF6', // Purple
  },
  cyberpunk: {
    purpleBlobColor: '#EC4899', // Pink
    pinkBlobColor: '#06B6D4', // Cyan
    blueBlobColor: '#10B981', // Green
    cyanBlobColor: '#F59E0B', // Yellow
  },
  deepSpace: {
    purpleBlobColor: '#4F46E5', // Indigo
    pinkBlobColor: '#3B82F6', // Blue
    blueBlobColor: '#09090B', // Dark
    cyanBlobColor: '#8B5CF6', // Purple
  },
}

const applyPreset = (presetName: keyof typeof presets) => {
  selectedPreset.value = presetName as string
  const config = presets[presetName]
  if (auroraConfig) {
    auroraConfig.purpleBlobColor = config.purpleBlobColor
    auroraConfig.pinkBlobColor = config.pinkBlobColor
    auroraConfig.blueBlobColor = config.blueBlobColor
    auroraConfig.cyanBlobColor = config.cyanBlobColor
  }
}

const resetConfig = () => {
  if (auroraConfig) {
    auroraConfig.speed = 3
    auroraConfig.intensity = 0.7
  }
  applyPreset('aurora')
}

// Stats counter mockup
const activeStats = ref([
  { count: '99.9%', label: 'Thời gian hoạt động', color: 'var(--cyan)' },
  { count: '10M+', label: 'Yêu cầu mỗi ngày', color: 'var(--purple)' },
  { count: '< 50ms', label: 'Tốc độ phản hồi', color: 'var(--pink)' },
])

const scrollToSection = (id: string) => {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<template>
  <div class="home-container">
    <!-- Hero Section -->
    <header class="hero-section">
      <div class="hero-content">
        <!-- Badge -->
        <div class="hero-badge animate-float">
          <Sparkles :size="14" class="badge-icon" />
          <span>Phiên bản v2.0 cực quang mới</span>
        </div>

        <h1 class="hero-title">
          Thiết Kế Web <br />
          <span class="text-gradient-aurora">Cực Quang Kỳ Ảo</span>
        </h1>
        <p class="hero-subtitle">
          Trải nghiệm trang web với các hiệu ứng chuyển động mượt mà, chiều sâu 3D glassmorphism và
          phối màu Aurora hiện đại bậc nhất.
        </p>

        <div class="hero-actions">
          <button class="btn-aurora" @click="scrollToSection('features')">
            <span>Bắt Đầu Khám Phá</span>
            <ChevronRight :size="18" />
          </button>
          <button class="btn-glass" @click="router.push('/login')">
            <Eye :size="18" />
            <span>Trải Nghiệm Thử</span>
          </button>
        </div>
      </div>

      <!-- Floating Tech Cards Visual -->
      <div class="hero-visual">
        <div class="visual-wrapper">
          <div class="glow-orb"></div>

          <!-- Card 1 -->
          <div class="floating-card card-purple glow-border-purple">
            <div class="card-header">
              <Zap class="card-icon text-purple" />
              <span class="card-tag">Tốc Độ</span>
            </div>
            <h3>Hiệu Năng Vượt Trội</h3>
            <p>Trải nghiệm tải trang tức thì dưới 0.1s nhờ thuật toán tối ưu hóa CSS.</p>
          </div>

          <!-- Card 2 -->
          <div class="floating-card card-pink glow-border-pink">
            <div class="card-header">
              <Sparkles class="card-icon text-pink" />
              <span class="card-tag">Đẹp mắt</span>
            </div>
            <h3>Thiết Kế Aurora</h3>
            <p>Sự kết hợp hoàn hảo giữa màu hồng thạch lựu, tím huyền ảo và xanh lam ngọc.</p>
          </div>
        </div>
      </div>
    </header>

    <!-- Interactive Aurora Customizer -->
    <section class="customizer-section glass-card">
      <div class="customizer-header">
        <div class="icon-circle">
          <Palette class="text-gradient-aurora" />
        </div>
        <div>
          <h2>Bảng Điều Khiển Cực Quang</h2>
          <p>Tự tay điều chỉnh tốc độ, cường độ và bảng màu của nền Aurora ở phía sau.</p>
        </div>
      </div>

      <div class="customizer-grid">
        <!-- Color Presets -->
        <div class="control-group">
          <label class="control-label">Bảng Phối Màu Nền</label>
          <div class="presets-buttons">
            <button
              v-for="(config, name) in presets"
              :key="name"
              class="preset-btn"
              :class="{ active: selectedPreset === name }"
              @click="applyPreset(name as keyof typeof presets)"
            >
              <span class="preset-name">{{
                name === 'aurora'
                  ? 'Cực Quang'
                  : name === 'neonFire'
                    ? 'Lửa Hồng'
                    : name === 'cyberpunk'
                      ? 'Cyberpunk'
                      : 'Vũ Trụ'
              }}</span>
              <div class="preset-dots">
                <span class="dot" :style="{ backgroundColor: config.purpleBlobColor }"></span>
                <span class="dot" :style="{ backgroundColor: config.pinkBlobColor }"></span>
                <span class="dot" :style="{ backgroundColor: config.blueBlobColor }"></span>
              </div>
            </button>
          </div>
        </div>

        <!-- Controls -->
        <div class="control-group">
          <div class="slider-wrapper">
            <div class="slider-header">
              <span class="control-label">Tốc Độ Di Chuyển</span>
              <span class="slider-value">{{ auroraConfig.speed }}x</span>
            </div>
            <input
              type="range"
              min="1"
              max="5"
              step="0.5"
              v-model.number="auroraConfig!.speed"
              class="range-input"
            />
          </div>

          <div class="slider-wrapper">
            <div class="slider-header">
              <span class="control-label">Độ Sáng / Cường Độ</span>
              <span class="slider-value">{{ Math.round(auroraConfig.intensity * 100) }}%</span>
            </div>
            <input
              type="range"
              min="0.1"
              max="1"
              step="0.05"
              v-model.number="auroraConfig!.intensity"
              class="range-input"
            />
          </div>
        </div>
      </div>

      <div class="customizer-footer">
        <button class="reset-btn" @click="resetConfig">
          <RefreshCw :size="14" />
          <span>Khôi phục mặc định</span>
        </button>
      </div>
    </section>

    <!-- Stats Showcase -->
    <section class="stats-section">
      <div v-for="stat in activeStats" :key="stat.label" class="stat-card glass-card">
        <h2 :style="{ color: stat.color }">{{ stat.count }}</h2>
        <p>{{ stat.label }}</p>
      </div>
    </section>

    <!-- Core Features Grid -->
    <section id="features" class="features-section">
      <div class="section-header">
        <h2>Công Nghệ Tích Hợp</h2>
        <p>Hệ sinh thái Lumora mang đến những công cụ tốt nhất cho sự phát triển dự án của bạn.</p>
      </div>

      <div class="features-grid">
        <div class="feature-card glass-card glow-border-purple">
          <div class="feature-icon-box bg-purple">
            <Zap />
          </div>
          <h3>Tối Ưu CSS Siêu Nhẹ</h3>
          <p>
            Không dùng các thư viện cồng kềnh, mọi hiệu ứng đều viết bằng CSS thuần túy giúp ứng
            dụng phản hồi mượt mà.
          </p>
        </div>

        <div class="feature-card glass-card glow-border-pink">
          <div class="feature-icon-box bg-pink">
            <Sparkles />
          </div>
          <h3>Ánh Sáng Động (Dynamic Glow)</h3>
          <p>
            Các nút bấm và ô viền tự động phát sáng dựa theo tọa độ chuột và trạng thái tương tác
            của người dùng.
          </p>
        </div>

        <div class="feature-card glass-card glow-border-cyan">
          <div class="feature-icon-box bg-cyan">
            <Shield />
          </div>
          <h3>Kính Mờ (Glassmorphism)</h3>
          <p>
            Sử dụng thuộc tính backdrop-filter cao cấp mang lại chiều sâu không gian đa tầng cuốn
            hút mắt nhìn.
          </p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 24px 80px 24px;
  display: flex;
  flex-direction: column;
  gap: 100px;
}

/* Hero Section */
.hero-section {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 40px;
  align-items: center;
  min-height: 70vh;
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.25);
  border-radius: 99px;
  color: var(--purple);
  font-weight: 600;
  font-size: 0.85rem;
  width: fit-content;
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.1);
}

.badge-icon {
  animation: pulse-slow 2s infinite;
}

.hero-title {
  font-size: 3.8rem;
  line-height: 1.1;
  font-weight: 800;
  letter-spacing: -0.04em;
}

.hero-subtitle {
  font-size: 1.15rem;
  color: var(--text-secondary);
  max-width: 540px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-top: 10px;
}

/* Floating visual */
.hero-visual {
  position: relative;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.visual-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
}

.glow-orb {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, rgba(236, 72, 153, 0.25) 0%, transparent 70%);
  filter: blur(20px);
  pointer-events: none;
}

.floating-card {
  position: absolute;
  width: 260px;
  padding: 24px;
  background: rgba(15, 15, 20, 0.7);
  backdrop-filter: blur(16px);
  border: 1px solid var(--glass-border);
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  transition: var(--transition-smooth);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  padding: 6px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
}

.text-purple {
  color: var(--purple);
  background: rgba(139, 92, 246, 0.1);
}

.text-pink {
  color: var(--pink);
  background: rgba(236, 72, 153, 0.1);
}

.card-tag {
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--text-muted);
}

.floating-card h3 {
  font-size: 1.1rem;
  margin-bottom: 8px;
}

.floating-card p {
  font-size: 0.88rem;
}

.card-purple {
  top: 15%;
  left: 10%;
  transform: rotate(-4deg);
  animation: float 6s ease-in-out infinite;
}

.card-pink {
  bottom: 10%;
  right: 10%;
  transform: rotate(3deg);
  animation: float 6s ease-in-out infinite 2s;
}

.floating-card:hover {
  transform: translateY(-8px) scale(1.02);
  z-index: 5;
}

/* Customizer Section */
.customizer-section {
  padding: 40px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.customizer-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.icon-circle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
}

.customizer-header h2 {
  font-size: 1.6rem;
  margin-bottom: 4px;
}

.customizer-grid {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 40px;
}

.control-group {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.control-label {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--white);
  letter-spacing: -0.01em;
}

.presets-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.preset-btn {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  cursor: pointer;
  color: var(--text-secondary);
  text-align: left;
  transition: var(--transition-smooth);
}

.preset-btn:hover {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.15);
}

.preset-btn.active {
  background: rgba(139, 92, 246, 0.08);
  border-color: var(--purple);
  color: var(--white);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.1);
}

.preset-name {
  font-weight: 600;
  font-size: 0.88rem;
}

.preset-dots {
  display: flex;
  gap: 4px;
}

.preset-dots .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.slider-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.slider-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.slider-value {
  font-size: 0.85rem;
  font-weight: 700;
  color: var(--cyan);
}

.range-input {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 6px;
  border-radius: 3px;
  background: rgba(255, 255, 255, 0.1);
  outline: none;
}

.range-input::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--purple);
  cursor: pointer;
  box-shadow: 0 0 10px rgba(139, 92, 246, 0.6);
  transition: var(--transition-fast);
}

.range-input::-webkit-slider-thumb:hover {
  transform: scale(1.2);
}

.customizer-footer {
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 20px;
}

.reset-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.85rem;
  font-weight: 500;
  transition: var(--transition-fast);
}

.reset-btn:hover {
  color: var(--pink);
}

/* Stats Section */
.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-card {
  padding: 30px;
  text-align: center;
}

.stat-card h2 {
  font-size: 2.5rem;
  font-weight: 800;
  margin-bottom: 8px;
}

.stat-card p {
  font-size: 0.95rem;
  font-weight: 500;
}

/* Features Section */
.features-section {
  display: flex;
  flex-direction: column;
  gap: 48px;
}

.section-header {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.section-header h2 {
  font-size: 2.2rem;
  margin-bottom: 12px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.feature-card {
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-icon-box {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  color: var(--white);
}

.bg-purple {
  background: linear-gradient(135deg, var(--purple), #7c3aed);
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}

.bg-pink {
  background: linear-gradient(135deg, var(--pink), #db2777);
  box-shadow: 0 4px 15px rgba(236, 72, 153, 0.3);
}

.bg-cyan {
  background: linear-gradient(135deg, var(--cyan), #0891b2);
  box-shadow: 0 4px 15px rgba(6, 182, 212, 0.3);
}

.feature-card h3 {
  font-size: 1.25rem;
  color: var(--white);
}

@media (max-width: 968px) {
  .hero-section {
    grid-template-columns: 1fr;
    text-align: center;
    gap: 60px;
  }
  .hero-badge {
    margin: 0 auto;
  }
  .hero-subtitle {
    margin: 0 auto;
  }
  .hero-actions {
    justify-content: center;
  }
  .hero-visual {
    height: 350px;
  }
  .customizer-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  .stats-section {
    grid-template-columns: 1fr;
  }
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>
