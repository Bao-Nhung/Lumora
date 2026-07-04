<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Sparkles, Mail, Lock, User, ArrowRight, Check } from '@lucide/vue'

const router = useRouter()

const isSignUp = ref(false)
const email = ref('')
const password = ref('')
const name = ref('')
const acceptedTerms = ref(false)

const emailFocused = ref(false)
const passwordFocused = ref(false)
const nameFocused = ref(false)

// Simple reactive validation (accepts email or alphanumeric username of length >= 3)
const isEmailValid = computed(() => {
  if (!email.value) return false
  const isEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)
  const isUsername = /^[a-zA-Z0-9_.-]{3,30}$/.test(email.value)
  return isEmail || isUsername
})

const passwordStrength = computed(() => {
  if (!password.value) return 0
  let score = 0
  if (password.value.length >= 6) score++
  if (/[A-Z]/.test(password.value)) score++
  if (/[0-9]/.test(password.value)) score++
  if (/[^A-Za-z0-9]/.test(password.value)) score++
  return score // 0 to 4
})

const strengthText = computed(() => {
  switch (passwordStrength.value) {
    case 1:
      return 'Yếu'
    case 2:
      return 'Trung bình'
    case 3:
      return 'Mạnh'
    case 4:
      return 'Rất mạnh'
    default:
      return ''
  }
})

const strengthColor = computed(() => {
  switch (passwordStrength.value) {
    case 1:
      return 'var(--pink)'
    case 2:
      return '#F59E0B' // Orange
    case 3:
      return 'var(--blue)'
    case 4:
      return 'var(--cyan)'
    default:
      return 'transparent'
  }
})

const isFormValid = computed(() => {
  if (isSignUp.value) {
    return (
      name.value.length >= 2 &&
      isEmailValid.value &&
      password.value.length >= 6 &&
      acceptedTerms.value
    )
  }
  return isEmailValid.value && password.value.length >= 6
})

const feedbackMessage = ref('')
const feedbackType = ref<'success' | 'error'>('success')
const loading = ref(false)

const toggleMode = () => {
  isSignUp.value = !isSignUp.value
  // Reset form
  email.value = ''
  password.value = ''
  name.value = ''
  acceptedTerms.value = false
  // Reset feedback
  feedbackMessage.value = ''
}

const handleSubmit = async () => {
  if (!isFormValid.value || loading.value) return
  loading.value = true
  feedbackMessage.value = ''

  try {
    const url = isSignUp.value
      ? 'http://localhost:8080/api/auth/register'
      : 'http://localhost:8080/api/auth/login'

    const body = isSignUp.value
      ? { name: name.value, email: email.value, password: password.value }
      : { email: email.value, password: password.value }

    const res = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
    })

    const data = await res.json()

    if (data.success) {
      feedbackType.value = 'success'
      feedbackMessage.value = data.message

      if (!isSignUp.value) {
        // Store user session details
        localStorage.setItem('user', JSON.stringify(data.user))
        // Delay redirect so user can see success message
        setTimeout(() => {
          router.push('/')
        }, 1500)
      } else {
        // Successful registration -> switch to login after delay
        setTimeout(() => {
          toggleMode()
          feedbackMessage.value = 'Đăng ký thành công! Vui lòng đăng nhập.'
          feedbackType.value = 'success'
        }, 1500)
      }
    } else {
      feedbackType.value = 'error'
      feedbackMessage.value = data.message
    }
  } catch (err) {
    console.error(err)
    feedbackType.value = 'error'
    feedbackMessage.value = 'Không thể kết nối đến máy chủ. Vui lòng thử lại sau!'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-wrapper glass-card">
      <!-- Left decorative panel -->
      <div class="visual-panel">
        <div class="visual-glow"></div>
        <div class="visual-content">
          <div class="brand">
            <Sparkles class="brand-icon animate-pulse-slow" />
            <span class="brand-name">Lumora</span>
          </div>
          <div class="visual-text">
            <h2>Khai mở thế giới sáng tạo số</h2>
            <p>
              Đăng nhập tài khoản để khám phá tất cả các mẫu thiết kế và công cụ tùy biến cực quang
              độc quyền.
            </p>
          </div>
        </div>
      </div>

      <!-- Right Form panel -->
      <div class="form-panel">
        <div class="form-header">
          <h2>Chào Mừng Trở Lại</h2>
          <p>Vui lòng đăng nhập để tiếp tục.</p>
        </div>

        <!-- Feedback Alert Banner -->
        <transition name="field-slide">
          <div v-if="feedbackMessage" class="feedback-alert" :class="feedbackType">
            <span>{{ feedbackMessage }}</span>
          </div>
        </transition>

        <form class="auth-form" @submit.prevent="handleSubmit">
          <!-- Full Name (Only for Sign Up) -->
          <transition name="field-slide">
            <div v-if="isSignUp" class="input-group" :class="{ focused: nameFocused || name }">
              <label class="floating-label">Tên của bạn</label>
              <div class="input-wrapper">
                <User class="input-icon" :size="18" />
                <input
                  type="text"
                  v-model="name"
                  class="form-input"
                  @focus="nameFocused = true"
                  @blur="nameFocused = false"
                  required
                />
              </div>
            </div>
          </transition>

          <!-- Email Address -->
          <div class="input-group" :class="{ focused: emailFocused || email }">
            <label class="floating-label">Tên đăng nhập hoặc Email</label>
            <div class="input-wrapper">
              <Mail class="input-icon" :size="18" />
              <input
                type="text"
                v-model="email"
                class="form-input"
                @focus="emailFocused = true"
                @blur="emailFocused = false"
                required
              />
              <span v-if="email && isEmailValid" class="validation-check">
                <Check :size="14" />
              </span>
            </div>
          </div>

          <!-- Password -->
          <div class="input-group" :class="{ focused: passwordFocused || password }">
            <label class="floating-label">Mật khẩu</label>
            <div class="input-wrapper">
              <Lock class="input-icon" :size="18" />
              <input
                type="password"
                v-model="password"
                class="form-input"
                @focus="passwordFocused = true"
                @blur="passwordFocused = false"
                required
              />
            </div>
            <!-- Password Strength Bar (Only for Sign Up) -->
            <div v-if="isSignUp && password" class="strength-bar-wrapper">
              <div class="strength-bar">
                <div
                  class="strength-fill"
                  :style="{
                    width: `${(passwordStrength / 4) * 100}%`,
                    backgroundColor: strengthColor,
                  }"
                ></div>
              </div>
              <span class="strength-label" :style="{ color: strengthColor }">
                {{ strengthText }}
              </span>
            </div>
          </div>

          <!-- Terms & Conditions / Remember Me -->
          <div class="form-options">
            <label v-if="isSignUp" class="checkbox-label">
              <input type="checkbox" v-model="acceptedTerms" class="checkbox-input" />
              <span class="custom-checkbox"></span>
              <span class="checkbox-text">Tôi đồng ý với điều khoản & điều kiện sử dụng.</span>
            </label>
            <div v-else class="login-options">
              <label class="checkbox-label">
                <input type="checkbox" class="checkbox-input" />
                <span class="custom-checkbox"></span>
                <span class="checkbox-text">Ghi nhớ đăng nhập</span>
              </label>
              <a href="#" class="forgot-link">Quên mật khẩu?</a>
            </div>
          </div>

          <!-- Submit Button -->
          <button
            type="submit"
            class="submit-btn"
            :class="{ active: isFormValid && !loading }"
            :disabled="!isFormValid || loading"
          >
            <span v-if="loading">Đang xử lý...</span>
            <span v-else>{{ isSignUp ? 'Đăng Ký Tài Khoản' : 'Đăng Nhập' }}</span>
            <ArrowRight v-if="!loading" :size="18" />
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 120px 24px 80px 24px;
}

.login-wrapper {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  width: 100%;
  max-width: 960px;
  min-height: 560px;
  overflow: hidden;
  border-radius: 24px;
}

/* Left panel style */
.visual-panel {
  position: relative;
  background: rgba(139, 92, 246, 0.05);
  border-right: 1px solid var(--glass-border);
  padding: 40px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.visual-glow {
  position: absolute;
  top: -20%;
  left: -20%;
  width: 150%;
  height: 150%;
  background: radial-gradient(
    circle at top left,
    rgba(236, 72, 153, 0.15),
    rgba(139, 92, 246, 0.15),
    transparent 60%
  );
  filter: blur(40px);
  pointer-events: none;
}

.visual-content {
  position: relative;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  z-index: 2;
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-icon {
  color: var(--pink);
  filter: drop-shadow(0 0 6px var(--pink));
}

.brand-name {
  font-size: 1.3rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, var(--white), rgba(255, 255, 255, 0.7));
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.visual-text h2 {
  font-size: 1.8rem;
  margin-bottom: 12px;
  color: var(--white);
}

.visual-text p {
  font-size: 0.95rem;
}

/* Right Panel Style */
.form-panel {
  padding: 50px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 32px;
}

.form-header h2 {
  font-size: 1.8rem;
  margin-bottom: 8px;
}

.form-header p {
  font-size: 0.92rem;
}

.toggle-link {
  background: none;
  border: none;
  color: var(--cyan);
  font-weight: 600;
  cursor: pointer;
  padding: 0;
  margin-left: 4px;
}

.toggle-link:hover {
  text-decoration: underline;
}

/* Auth Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.input-group {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.floating-label {
  position: absolute;
  left: 44px;
  top: 14px;
  font-size: 0.95rem;
  color: var(--text-muted);
  pointer-events: none;
  transition: var(--transition-smooth);
}

.input-group.focused .floating-label {
  top: -8px;
  left: 12px;
  font-size: 0.78rem;
  color: var(--purple);
  background: var(--bg-color);
  padding: 0 6px;
  border-radius: 4px;
  z-index: 5;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  color: var(--text-muted);
  transition: var(--transition-fast);
}

.input-group.focused .input-icon {
  color: var(--purple);
}

.form-input {
  width: 100%;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid var(--glass-border);
  border-radius: 12px;
  padding: 14px 16px 14px 44px;
  color: var(--white);
  font-size: 0.95rem;
  font-family: inherit;
  outline: none;
  transition: var(--transition-smooth);
}

.form-input:focus {
  background: rgba(255, 255, 255, 0.04);
  border-color: var(--purple);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.15);
}

.validation-check {
  position: absolute;
  right: 16px;
  background: rgba(6, 182, 212, 0.1);
  color: var(--cyan);
  border-radius: 50%;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Strength bar */
.strength-bar-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 4px;
}

.strength-bar {
  flex: 1;
  height: 4px;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 2px;
  overflow: hidden;
}

.strength-fill {
  height: 100%;
  width: 0;
  transition: var(--transition-smooth);
}

.strength-label {
  font-size: 0.75rem;
  font-weight: 700;
}

/* Form Options */
.form-options {
  margin-top: 4px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  user-select: none;
}

.checkbox-input {
  display: none;
}

.custom-checkbox {
  width: 18px;
  height: 18px;
  border-radius: 6px;
  border: 1px solid var(--glass-border);
  background: rgba(255, 255, 255, 0.02);
  display: inline-block;
  position: relative;
  transition: var(--transition-fast);
}

.checkbox-input:checked + .custom-checkbox {
  background: var(--purple);
  border-color: var(--purple);
}

.checkbox-input:checked + .custom-checkbox::after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-text {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.forgot-link {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.forgot-link:hover {
  color: var(--cyan);
}

/* Submit Button */
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 14px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.98rem;
  border: none;
  background: rgba(255, 255, 255, 0.05);
  color: var(--text-muted);
  cursor: not-allowed;
  transition: var(--transition-smooth);
}

.submit-btn.active {
  background: linear-gradient(135deg, var(--purple), var(--cyan));
  color: var(--white);
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.3);
}

.submit-btn.active:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(6, 182, 212, 0.4);
}

/* Field animation */
.field-slide-enter-active,
.field-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.field-slide-enter-from,
.field-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Feedback Alert Banner */
.feedback-alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  text-align: left;
  animation: slide-down 0.3s ease;
  transition: all 0.3s ease;
}
.feedback-alert.success {
  background: rgba(6, 182, 212, 0.1);
  border: 1px solid rgba(6, 182, 212, 0.25);
  color: var(--cyan);
}
.feedback-alert.error {
  background: rgba(236, 72, 153, 0.1);
  border: 1px solid rgba(236, 72, 153, 0.25);
  color: var(--pink);
}

@keyframes slide-down {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .login-wrapper {
    grid-template-columns: 1fr;
  }
  .visual-panel {
    display: none;
  }
}
</style>
