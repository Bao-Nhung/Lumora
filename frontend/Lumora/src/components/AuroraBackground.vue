<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    speed?: number // 1 to 5, where 3 is default
    intensity?: number // 0 to 1
    purpleBlobColor?: string
    pinkBlobColor?: string
    blueBlobColor?: string
    cyanBlobColor?: string
  }>(),
  {
    speed: 3,
    intensity: 0.7,
    purpleBlobColor: 'var(--purple)',
    pinkBlobColor: 'var(--pink)',
    blueBlobColor: 'var(--blue)',
    cyanBlobColor: 'var(--cyan)',
  },
)

const speedDuration = computed(() => {
  const base = 25 // base duration in seconds
  return `${base / (props.speed / 3)}s`
})

const speedDurationFast = computed(() => {
  const base = 15
  return `${base / (props.speed / 3)}s`
})

const speedDurationSlow = computed(() => {
  const base = 35
  return `${base / (props.speed / 3)}s`
})

const opacityStyle = computed(() => {
  return props.intensity
})

// Helper: convert any color (hex, named, CSS var) to rgba string with opacity
// Since CSS variables can't be manipulated with string concat, we use color-mix or filter for real colors.
// For simplicity and compatibility, we build gradients using the color directly with wrapper divs having opacity.
// Each curtain has its own opacity, so we just use the color directly.
const curtain1Gradient = computed(() => {
  const c = props.purpleBlobColor
  const c2 = props.cyanBlobColor
  const c3 = props.blueBlobColor
  return `linear-gradient(90deg, transparent 0%, ${c} 20%, ${c2} 50%, ${c3} 80%, transparent 100%)`
})

const curtain2Gradient = computed(() => {
  const c = props.pinkBlobColor
  const c2 = props.purpleBlobColor
  const c3 = props.cyanBlobColor
  return `linear-gradient(90deg, transparent 0%, ${c} 25%, ${c2} 50%, ${c3} 75%, transparent 100%)`
})

const curtain3Gradient = computed(() => {
  const c = props.cyanBlobColor
  const c2 = props.pinkBlobColor
  const c3 = props.purpleBlobColor
  return `linear-gradient(90deg, transparent 0%, ${c} 30%, ${c2} 60%, ${c3} 80%, transparent 100%)`
})
</script>

<template>
  <div class="aurora-container" :style="{ opacity: opacityStyle }">
    <!-- Wavy Aurora Curtains (Northern Lights Ribbons) -->
    <div class="aurora-curtains">
      <div
        class="aurora-curtain curtain-1"
        :style="{
          animationDuration: speedDurationSlow,
          background: curtain1Gradient,
        }"
      ></div>
      <div
        class="aurora-curtain curtain-2"
        :style="{
          animationDuration: speedDuration,
          background: curtain2Gradient,
        }"
      ></div>
      <div
        class="aurora-curtain curtain-3"
        :style="{
          animationDuration: speedDurationFast,
          background: curtain3Gradient,
        }"
      ></div>
    </div>

    <!-- Soft Ambient corner glow blobs (maintaining depth) -->
    <div
      class="aurora-blob blob-purple"
      :style="{
        animationDuration: speedDuration,
        backgroundColor: props.purpleBlobColor,
      }"
    ></div>
    <div
      class="aurora-blob blob-pink"
      :style="{
        animationDuration: speedDurationFast,
        backgroundColor: props.pinkBlobColor,
      }"
    ></div>
    <div
      class="aurora-blob blob-blue"
      :style="{
        animationDuration: speedDurationSlow,
        backgroundColor: props.blueBlobColor,
      }"
    ></div>
    <div
      class="aurora-blob blob-cyan"
      :style="{
        animationDuration: speedDuration,
        backgroundColor: props.cyanBlobColor,
      }"
    ></div>

    <!-- Overlay tech grid -->
    <div class="aurora-overlay"></div>
  </div>
</template>

<style scoped>
.aurora-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: -10;
  overflow: hidden;
  background-color: var(--bg-color);
  pointer-events: none;
  transition: opacity 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

/* Wavy curtains styling */
.aurora-curtains {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 70vh;
  overflow: hidden;
  filter: blur(80px) saturate(1.8) contrast(1.2);
  mix-blend-mode: screen;
  opacity: 0.65;
  will-change: filter;
}

.aurora-curtain {
  position: absolute;
  width: 200%;
  height: 40vh;
  top: -10%;
  left: -50%;
  opacity: 0.45;
  will-change: transform, opacity;
}

.curtain-1 {
  animation: wave-1 infinite alternate ease-in-out;
}

.curtain-2 {
  animation: wave-2 infinite alternate ease-in-out;
  animation-delay: -3s;
}

.curtain-3 {
  animation: wave-3 infinite alternate ease-in-out;
  animation-delay: -6s;
}

/* Base glowing blobs for ambient lighting */
.aurora-blob {
  position: absolute;
  filter: blur(140px);
  border-radius: 50%;
  mix-blend-mode: screen;
  opacity: 0.25;
  will-change: transform, opacity;
}

.blob-purple {
  top: -15%;
  left: 10%;
  width: 45vw;
  height: 45vw;
  animation: drift-1 infinite alternate ease-in-out;
}

.blob-pink {
  bottom: -15%;
  right: 10%;
  width: 50vw;
  height: 50vw;
  animation: drift-2 infinite alternate ease-in-out;
}

.blob-blue {
  top: 40%;
  right: 15%;
  width: 40vw;
  height: 40vw;
  animation: drift-3 infinite alternate ease-in-out;
}

.blob-cyan {
  bottom: 10%;
  left: 5%;
  width: 35vw;
  height: 35vw;
  animation: drift-4 infinite alternate ease-in-out;
}

.aurora-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.007) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.007) 1px, transparent 1px);
  background-size: 50px 50px;
  background-position: center;
  mask-image: radial-gradient(circle at center, black 40%, transparent 95%);
  -webkit-mask-image: radial-gradient(circle at center, black 40%, transparent 95%);
  opacity: 0.8;
}

/* Wavy animation keyframes */
@keyframes wave-1 {
  0% {
    transform: translate3d(5%, -5%, 0) rotate(-2deg) skewX(-15deg) scaleY(1);
    opacity: 0.35;
  }
  50% {
    transform: translate3d(-5%, 5%, 0) rotate(2deg) skewX(15deg) scaleY(1.3);
    opacity: 0.6;
  }
  100% {
    transform: translate3d(10%, -10%, 0) rotate(-1deg) skewX(-5deg) scaleY(0.9);
    opacity: 0.35;
  }
}

@keyframes wave-2 {
  0% {
    transform: translate3d(-10%, 8%, 0) rotate(3deg) skewX(20deg) scaleY(1.25);
    opacity: 0.4;
  }
  50% {
    transform: translate3d(5%, -8%, 0) rotate(-3deg) skewX(-20deg) scaleY(0.85);
    opacity: 0.65;
  }
  100% {
    transform: translate3d(-5%, 5%, 0) rotate(2deg) skewX(10deg) scaleY(1.1);
    opacity: 0.4;
  }
}

@keyframes wave-3 {
  0% {
    transform: translate3d(8%, -10%, 0) rotate(-4deg) skewX(-25deg) scaleY(0.9);
    opacity: 0.3;
  }
  50% {
    transform: translate3d(-8%, 10%, 0) rotate(4deg) skewX(25deg) scaleY(1.4);
    opacity: 0.6;
  }
  100% {
    transform: translate3d(5%, -5%, 0) rotate(-2deg) skewX(-15deg) scaleY(1);
    opacity: 0.3;
  }
}

/* Drifting keyframes */
@keyframes drift-1 {
  0% {
    transform: translate3d(0, 0, 0) scale(1) rotate(0deg);
  }
  50% {
    transform: translate3d(8vw, 5vh, 0) scale(1.1) rotate(45deg);
    opacity: 0.35;
  }
  100% {
    transform: translate3d(-4vw, 10vh, 0) scale(0.9) rotate(90deg);
  }
}

@keyframes drift-2 {
  0% {
    transform: translate3d(0, 0, 0) scale(1.1) rotate(0deg);
  }
  50% {
    transform: translate3d(-6vw, -8vh, 0) scale(0.95) rotate(-60deg);
    opacity: 0.4;
  }
  100% {
    transform: translate3d(4vw, 4vh, 0) scale(1.05) rotate(-120deg);
  }
}

@keyframes drift-3 {
  0% {
    transform: translate3d(0, 0, 0) scale(0.9) rotate(0deg);
  }
  50% {
    transform: translate3d(-10vw, 6vh, 0) scale(1.05) rotate(120deg);
    opacity: 0.3;
  }
  100% {
    transform: translate3d(6vw, -4vh, 0) scale(0.95) rotate(240deg);
  }
}

@keyframes drift-4 {
  0% {
    transform: translate3d(0, 0, 0) scale(1.05) rotate(0deg);
  }
  50% {
    transform: translate3d(10vw, -10vh, 0) scale(0.9) rotate(-30deg);
    opacity: 0.4;
  }
  100% {
    transform: translate3d(-4vw, 6vh, 0) scale(1.1) rotate(60deg);
  }
}
</style>
