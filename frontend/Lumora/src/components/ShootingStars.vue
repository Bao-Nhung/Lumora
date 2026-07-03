<script setup lang="ts">
// Simple static component relying on pure CSS keyframe loops for maximum performance
</script>

<template>
  <div class="stars-overlay">
    <!-- Star elements with varying randomized delays, durations and positions -->
    <div class="shooting-star star-1"></div>
    <div class="shooting-star star-2"></div>
    <div class="shooting-star star-3"></div>
    <div class="shooting-star star-4"></div>
    <div class="shooting-star star-5"></div>
  </div>
</template>

<style scoped>
.stars-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: -9; /* Placed in front of Aurora blobs (-10) but behind main page layout */
  overflow: hidden;
}

.shooting-star {
  position: absolute;
  height: 2px;
  background: linear-gradient(-45deg, #ffffff, rgba(6, 182, 212, 0.6), transparent);
  filter: drop-shadow(0 0 6px rgba(255, 255, 255, 0.8));
  border-radius: 999px;
  opacity: 0;
  transform: rotate(-45deg);
  will-change: transform, opacity, width;
}

/* Animations for each star */
.star-1 {
  top: 0%;
  right: 10%;
  animation: shoot 8s linear infinite;
  animation-delay: 1s;
}

.star-2 {
  top: 15%;
  right: 30%;
  animation: shoot 12s linear infinite;
  animation-delay: 4s;
  background: linear-gradient(-45deg, #ffffff, rgba(236, 72, 153, 0.6), transparent);
  filter: drop-shadow(0 0 6px rgba(236, 72, 153, 0.8));
}

.star-3 {
  top: -5%;
  right: 50%;
  animation: shoot 10s linear infinite;
  animation-delay: 7s;
}

.star-4 {
  top: 30%;
  right: 5%;
  animation: shoot 15s linear infinite;
  animation-delay: 2s;
  background: linear-gradient(-45deg, #ffffff, rgba(139, 92, 246, 0.6), transparent);
  filter: drop-shadow(0 0 6px rgba(139, 92, 246, 0.8));
}

.star-5 {
  top: 10%;
  right: 70%;
  animation: shoot 9s linear infinite;
  animation-delay: 10s;
}

@keyframes shoot {
  0% {
    transform: translate3d(0, 0, 0) rotate(-45deg);
    opacity: 0;
    width: 0px;
  }
  1% {
    opacity: 1;
    width: 80px;
  }
  8% {
    transform: translate3d(-350px, 350px, 0) rotate(-45deg);
    opacity: 0;
    width: 0px;
  }
  100% {
    transform: translate3d(-350px, 350px, 0) rotate(-45deg);
    opacity: 0;
    width: 0px;
  }
}
</style>
