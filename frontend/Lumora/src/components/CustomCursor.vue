<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const dotX = ref(0)
const dotY = ref(0)
const ringX = ref(0)
const ringY = ref(0)

const isHovered = ref(false)
const isClicked = ref(false)
const isVisible = ref(false)
const isDesktop = ref(false)

const mouseX = ref(0)
const mouseY = ref(0)

let canvas: HTMLCanvasElement | null = null
let ctx: CanvasRenderingContext2D | null = null
let animationId = 0

interface Particle {
  x: number
  y: number
  vx: number
  vy: number
  alpha: number
  size: number
  color: string
  decay: number
}

const particles: Particle[] = []

const colors = [
  '#8B5CF6', // Purple
  '#EC4899', // Pink
  '#3B82F6', // Blue
  '#06B6D4', // Cyan
]

const handleMouseMove = (e: MouseEvent) => {
  mouseX.value = e.clientX
  mouseY.value = e.clientY

  if (!isVisible.value) {
    isVisible.value = true
  }

  // Spawn particles on movement
  if (canvas && ctx && isDesktop.value) {
    // Only spawn sometimes to avoid clogging
    if (Math.random() < 0.4) {
      spawnParticle(e.clientX, e.clientY)
    }
  }
}

const spawnParticle = (x: number, y: number) => {
  const color = colors[Math.floor(Math.random() * colors.length)] || '#8B5CF6'
  particles.push({
    x,
    y,
    vx: (Math.random() - 0.5) * 1.5,
    vy: (Math.random() - 0.5) * 1.5 - 0.5, // drift slightly up
    alpha: 1.0,
    size: Math.random() * 4 + 1.5,
    color,
    decay: Math.random() * 0.02 + 0.015,
  })
}

const handleMouseOver = (e: MouseEvent) => {
  const target = e.target as HTMLElement
  if (
    target &&
    (target.tagName === 'A' ||
      target.tagName === 'BUTTON' ||
      target.tagName === 'INPUT' ||
      target.tagName === 'SELECT' ||
      target.tagName === 'TEXTAREA' ||
      target.closest('a') ||
      target.closest('button') ||
      target.closest('.preset-btn') ||
      target.closest('.filter-btn') ||
      target.getAttribute('role') === 'button')
  ) {
    isHovered.value = true
  } else {
    isHovered.value = false
  }
}

const handleMouseDown = () => {
  isClicked.value = true
  // Spawn burst of particles on click
  if (isDesktop.value) {
    for (let i = 0; i < 8; i++) {
      spawnParticle(mouseX.value, mouseY.value)
    }
  }
}

const handleMouseUp = () => {
  isClicked.value = false
}

const handleMouseLeave = () => {
  isVisible.value = false
}

const handleMouseEnter = () => {
  isVisible.value = true
}

const handleResize = () => {
  if (canvas) {
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }
  isDesktop.value = window.innerWidth > 768
}

const tick = () => {
  // Spring lerp logic
  // dot follows quickly
  dotX.value += (mouseX.value - dotX.value) * 0.3
  dotY.value += (mouseY.value - dotY.value) * 0.3

  // ring follows with drag
  ringX.value += (mouseX.value - ringX.value) * 0.15
  ringY.value += (mouseY.value - ringY.value) * 0.15

  // Draw canvas particles
  if (canvas && ctx && isDesktop.value) {
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    for (let i = particles.length - 1; i >= 0; i--) {
      const p = particles[i]
      if (!p) continue
      p.x += p.vx
      p.y += p.vy
      p.alpha -= p.decay

      if (p.alpha <= 0) {
        particles.splice(i, 1)
        continue
      }

      // Draw glowing particle
      ctx.save()
      ctx.globalAlpha = p.alpha
      ctx.beginPath()

      // Draw sparkle/star shape
      const rot = (Math.PI / 2) * 3
      const cx = p.x
      const cy = p.y
      const spikes = 4
      const outerRadius = p.size
      const innerRadius = p.size / 2
      const step = Math.PI / spikes

      ctx.beginPath()
      ctx.moveTo(cx, cy - outerRadius)
      for (let j = 0; j < spikes; j++) {
        const sxOuter = cx + Math.cos(rot + j * step * 2) * outerRadius
        const syOuter = cy + Math.sin(rot + j * step * 2) * outerRadius
        ctx.lineTo(sxOuter, syOuter)
        const sxInner = cx + Math.cos(rot + j * step * 2 + step) * innerRadius
        const syInner = cy + Math.sin(rot + j * step * 2 + step) * innerRadius
        ctx.lineTo(sxInner, syInner)
      }
      ctx.closePath()

      ctx.fillStyle = p.color
      ctx.shadowBlur = 6
      ctx.shadowColor = p.color
      ctx.fill()
      ctx.restore()
    }
  }

  animationId = requestAnimationFrame(tick)
}

onMounted(() => {
  isDesktop.value = window.innerWidth > 768

  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseover', handleMouseOver)
  window.addEventListener('mousedown', handleMouseDown)
  window.addEventListener('mouseup', handleMouseUp)
  document.addEventListener('mouseleave', handleMouseLeave)
  document.addEventListener('mouseenter', handleMouseEnter)
  window.addEventListener('resize', handleResize)

  canvas = document.getElementById('cursor-canvas') as HTMLCanvasElement
  if (canvas) {
    ctx = canvas.getContext('2d')
    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
  }

  tick()
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseover', handleMouseOver)
  window.removeEventListener('mousedown', handleMouseDown)
  window.removeEventListener('mouseup', handleMouseUp)
  document.removeEventListener('mouseleave', handleMouseLeave)
  document.removeEventListener('mouseenter', handleMouseEnter)
  window.removeEventListener('resize', handleResize)
  cancelAnimationFrame(animationId)
})
</script>

<template>
  <div v-if="isDesktop" class="custom-cursor-container">
    <!-- Screen canvas for particles trail -->
    <canvas id="cursor-canvas" class="cursor-trail-canvas"></canvas>

    <!-- Cursor Dot -->
    <div
      class="cursor-dot"
      :class="{
        'cursor-active': isHovered,
        'cursor-clicked': isClicked,
        'cursor-hidden': !isVisible,
      }"
      :style="{ transform: `translate3d(${dotX}px, ${dotY}px, 0) translate3d(-50%, -50%, 0)` }"
    ></div>

    <!-- Cursor Ring Follower -->
    <div
      class="cursor-ring"
      :class="{
        'cursor-active': isHovered,
        'cursor-clicked': isClicked,
        'cursor-hidden': !isVisible,
      }"
      :style="{ transform: `translate3d(${ringX}px, ${ringY}px, 0) translate3d(-50%, -50%, 0)` }"
    >
      <div class="ring-glow"></div>
    </div>
  </div>
</template>

<style scoped>
.custom-cursor-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 9999;
}

.cursor-trail-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  mix-blend-mode: screen;
}

.cursor-dot {
  position: absolute;
  top: 0;
  left: 0;
  width: 6px;
  height: 6px;
  background-color: var(--white);
  border-radius: 50%;
  pointer-events: none;
  z-index: 10000;
  transition:
    width 0.2s ease,
    height 0.2s ease,
    background-color 0.2s ease,
    opacity 0.3s ease;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.8);
}

.cursor-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 32px;
  height: 32px;
  border: 1.5px solid rgba(255, 255, 255, 0.35);
  border-radius: 50%;
  pointer-events: none;
  z-index: 9999;
  transition:
    width 0.3s cubic-bezier(0.16, 1, 0.3, 1),
    height 0.3s cubic-bezier(0.16, 1, 0.3, 1),
    border-color 0.3s ease,
    background-color 0.3s ease,
    opacity 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ring-glow {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.2) 0%, transparent 80%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

/* Hovering interactive state */
.cursor-dot.cursor-active {
  width: 4px;
  height: 4px;
  background-color: var(--cyan);
  box-shadow: 0 0 10px var(--cyan);
}

.cursor-ring.cursor-active {
  width: 50px;
  height: 50px;
  border-color: var(--purple);
  background-color: rgba(139, 92, 246, 0.08);
}

.cursor-ring.cursor-active .ring-glow {
  opacity: 1;
}

/* Clicking State */
.cursor-dot.cursor-clicked {
  transform: scale(0.6);
  background-color: var(--pink);
}

.cursor-ring.cursor-clicked {
  width: 24px;
  height: 24px;
  border-color: var(--pink);
  background-color: rgba(236, 72, 153, 0.15);
}

/* Hidden State */
.cursor-dot.cursor-hidden,
.cursor-ring.cursor-hidden {
  opacity: 0;
}
</style>
