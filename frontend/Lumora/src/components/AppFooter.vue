<script setup lang="ts">
import { Sparkles, Globe, Share2, Layers } from '@lucide/vue'

const currentYear = new Date().getFullYear()
</script>

<template>
  <footer class="footer">
    <div class="footer-glow"></div>
    <div class="footer-container">
      <div class="footer-grid">
        <!-- Brand Info -->
        <div class="footer-brand-col">
          <div class="logo">
            <Sparkles class="logo-icon animate-pulse-slow" />
            <span class="logo-text">Lumora</span>
          </div>
          <p class="brand-desc">
            Xây dựng các sản phẩm số tuyệt đẹp với hiệu ứng ánh sáng cực quang (Aurora), mang đến
            trải nghiệm người dùng hiện đại và đỉnh cao.
          </p>
          <div class="social-links">
            <a href="#" class="social-link" aria-label="Globe"><Globe :size="18" /></a>
            <a href="#" class="social-link" aria-label="Share"><Share2 :size="18" /></a>
            <a href="#" class="social-link" aria-label="Layers"><Layers :size="18" /></a>
          </div>
        </div>

        <!-- Links Columns -->
        <div class="footer-links-col">
          <h3 class="col-title">Sản Phẩm</h3>
          <ul class="col-links">
            <li><a href="#" class="footer-link">Tính Năng</a></li>
            <li><a href="#" class="footer-link">Bảng Giá</a></li>
            <li><a href="#" class="footer-link">Mẫu Thiết Kế</a></li>
            <li><a href="#" class="footer-link">Tải Về</a></li>
          </ul>
        </div>

        <div class="footer-links-col">
          <h3 class="col-title">Công Ty</h3>
          <ul class="col-links">
            <li><a href="#" class="footer-link">Về Chúng Tôi</a></li>
            <li><a href="#" class="footer-link">Tuyển Dụng</a></li>
            <li><a href="#" class="footer-link">Tin Tức</a></li>
            <li><a href="#" class="footer-link">Đối Tác</a></li>
          </ul>
        </div>

        <!-- Subscription Form -->
        <div class="footer-sub-col">
          <h3 class="col-title">Đăng Ký Bản Tin</h3>
          <p class="sub-desc">Nhận thông tin cập nhật mới nhất về thiết kế và công nghệ.</p>
          <form class="sub-form" @submit.prevent>
            <input type="email" placeholder="Email của bạn..." class="sub-input" />
            <button type="submit" class="sub-btn">Gửi</button>
          </form>
        </div>
      </div>

      <div class="footer-bottom">
        <p class="copyright">&copy; {{ currentYear }} Lumora. Tất cả quyền được bảo lưu.</p>
        <div class="bottom-links">
          <a href="#" class="bottom-link">Điều khoản</a>
          <a href="#" class="bottom-link">Chính sách bảo mật</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.footer {
  position: relative;
  border-top: 1px solid var(--glass-border);
  background: rgba(9, 9, 11, 0.9);
  padding: 80px 0 40px 0;
  overflow: hidden;
  z-index: 10;
}

.footer-glow {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 200px;
  background: radial-gradient(circle, rgba(139, 92, 246, 0.15) 0%, transparent 70%);
  filter: blur(50px);
  pointer-events: none;
  z-index: -1;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-grid {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 2fr;
  gap: 48px;
  margin-bottom: 60px;
}

/* Brand column */
.footer-brand-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  color: var(--pink);
  filter: drop-shadow(0 0 6px var(--pink));
}

.logo-text {
  font-size: 1.3rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, var(--white), rgba(255, 255, 255, 0.7));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.brand-desc {
  font-size: 0.95rem;
  color: var(--text-secondary);
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  color: var(--text-secondary);
  transition: var(--transition-fast);
}

.social-link:hover {
  background: var(--purple);
  color: var(--white);
  border-color: var(--purple);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.3);
}

/* Links Columns */
.footer-links-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.col-title {
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: -0.01em;
  color: var(--white);
}

.col-links {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-link {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.footer-link:hover {
  color: var(--white);
  padding-left: 2px;
}

/* Subscription Column */
.footer-sub-col {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sub-desc {
  font-size: 0.95rem;
  color: var(--text-secondary);
}

.sub-form {
  display: flex;
  gap: 8px;
  width: 100%;
}

.sub-input {
  flex: 1;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
  border-radius: 8px;
  padding: 10px 16px;
  color: var(--white);
  font-size: 0.9rem;
  font-family: inherit;
  outline: none;
  transition: var(--transition-fast);
}

.sub-input:focus {
  border-color: var(--purple);
  background: rgba(255, 255, 255, 0.05);
}

.sub-btn {
  background: var(--white);
  color: var(--bg-color);
  border: none;
  border-radius: 8px;
  padding: 0 20px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: var(--transition-fast);
}

.sub-btn:hover {
  background: var(--purple);
  color: var(--white);
  box-shadow: 0 4px 15px rgba(139, 92, 246, 0.4);
}

/* Footer Bottom */
.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  padding-top: 30px;
  margin-top: 30px;
}

.copyright {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.bottom-links {
  display: flex;
  gap: 24px;
}

.bottom-link {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.bottom-link:hover {
  color: var(--white);
}

@media (max-width: 968px) {
  .footer-grid {
    grid-template-columns: 1fr 1fr;
    gap: 36px;
  }
}

@media (max-width: 576px) {
  .footer-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  .footer-bottom {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>
