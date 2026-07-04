<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Users,
  UserPlus,
  Edit,
  Trash2,
  Database,
  Activity,
  Clock,
  Search,
  Plus,
  X,
  Shield,
  Key,
  Mail,
  User as UserIcon,
  CheckCircle,
  AlertTriangle
} from '@lucide/vue'

const router = useRouter()

// Session user state
interface SessionUser {
  id: number
  name: string
  email: string
}

const currentUser = ref<SessionUser | null>(null)

// API User representation
interface User {
  id: number
  name: string
  email: string
  createdAt: string
}

const users = ref<User[]>([])
const loading = ref(false)
const searchQuery = ref('')
const feedbackMessage = ref('')
const feedbackType = ref<'success' | 'error'>('success')

// Activity logs
interface ActivityLog {
  time: string
  message: string
  type: 'info' | 'success' | 'warning' | 'error'
}

const logs = ref<ActivityLog[]>([])

const addLog = (message: string, type: 'info' | 'success' | 'warning' | 'error' = 'info') => {
  const now = new Date()
  const timeStr = now.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
  logs.value.unshift({ time: timeStr, message, type })
}

// Uptime calculation
const initTime = new Date()
const uptime = ref('00:00:00')

const updateUptime = () => {
  const diff = Math.floor((new Date().getTime() - initTime.getTime()) / 1000)
  const hours = String(Math.floor(diff / 3600)).padStart(2, '0')
  const minutes = String(Math.floor((diff % 3600) / 60)).padStart(2, '0')
  const seconds = String(diff % 60).padStart(2, '0')
  uptime.value = `${hours}:${minutes}:${seconds}`
}

// Check auth & fetch data
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    router.push('/login')
    return
  }

  const userObj = JSON.parse(userStr)
  if (userObj.role !== 'ADMIN') {
    router.push('/')
    return
  }

  currentUser.value = userObj
  addLog(`Khởi động phiên làm việc cho quản trị viên ${currentUser.value?.name}`, 'info')

  fetchUsers()
  setInterval(updateUptime, 1000)
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await fetch('http://localhost:8080/api/users')
    if (res.ok) {
      users.value = await res.json()
      addLog('Đã đồng bộ danh sách người dùng từ H2 Database', 'success')
    } else {
      addLog('Lỗi khi tải danh sách người dùng', 'error')
    }
  } catch (err) {
    console.error(err)
    addLog('Không thể kết nối đến máy chủ backend', 'error')
  } finally {
    loading.value = false
  }
}

// Filtered Users
const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value
  const q = searchQuery.value.toLowerCase().trim()
  return users.value.filter(
    (u) => u.name.toLowerCase().includes(q) || u.email.toLowerCase().includes(q)
  )
})

// Modals State
const showAddModal = ref(false)
const showEditModal = ref(false)

// Form Fields
const nameField = ref('')
const emailField = ref('')
const passwordField = ref('')
const confirmPasswordField = ref('')
const editUserId = ref<number | null>(null)

const resetForm = () => {
  nameField.value = ''
  emailField.value = ''
  passwordField.value = ''
  confirmPasswordField.value = ''
  editUserId.value = null
}

const openAddModal = () => {
  resetForm()
  showAddModal.value = true
}

const openEditModal = (user: User) => {
  resetForm()
  editUserId.value = user.id
  nameField.value = user.name
  emailField.value = user.email
  showEditModal.value = true
}

// Alerts / Feedback helper
const showAlert = (msg: string, type: 'success' | 'error') => {
  feedbackMessage.value = msg
  feedbackType.value = type
  setTimeout(() => {
    feedbackMessage.value = ''
  }, 4000)
}

// User Actions
const handleAddUser = async () => {
  if (!nameField.value.trim() || !emailField.value.trim() || !passwordField.value) {
    showAlert('Vui lòng điền đầy đủ các thông tin bắt buộc!', 'error')
    return
  }

  if (passwordField.value.length < 6) {
    showAlert('Mật khẩu phải chứa ít nhất 6 ký tự!', 'error')
    return
  }

  if (passwordField.value !== confirmPasswordField.value) {
    showAlert('Mật khẩu xác nhận không khớp!', 'error')
    return
  }

  try {
    const res = await fetch('http://localhost:8080/api/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: nameField.value.trim(),
        email: emailField.value.trim(),
        password: passwordField.value
      })
    })

    const data = await res.json()
    if (res.ok) {
      addLog(`Thêm mới tài khoản ${data.email} thành công`, 'success')
      showAlert('Thêm người dùng mới thành công!', 'success')
      showAddModal.value = false
      fetchUsers()
    } else {
      showAlert(data.message || 'Có lỗi xảy ra khi tạo người dùng!', 'error')
    }
  } catch (err) {
    console.error(err)
    showAlert('Lỗi kết nối máy chủ backend!', 'error')
  }
}

const handleEditUser = async () => {
  if (!editUserId.value) return

  if (!nameField.value.trim() || !emailField.value.trim()) {
    showAlert('Họ tên và email không được để trống!', 'error')
    return
  }

  if (passwordField.value && passwordField.value.length < 6) {
    showAlert('Mật khẩu mới phải chứa ít nhất 6 ký tự!', 'error')
    return
  }

  if (passwordField.value && passwordField.value !== confirmPasswordField.value) {
    showAlert('Mật khẩu xác nhận không khớp!', 'error')
    return
  }

  try {
    const res = await fetch(`http://localhost:8080/api/users/${editUserId.value}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: nameField.value.trim(),
        email: emailField.value.trim(),
        password: passwordField.value || null
      })
    })

    const data = await res.json()
    if (res.ok) {
      addLog(`Cập nhật tài khoản người dùng ID ${editUserId.value}`, 'success')
      showAlert('Cập nhật người dùng thành công!', 'success')
      showEditModal.value = false
      fetchUsers()

      // If updating oneself, update localstorage
      if (currentUser.value && currentUser.value.id === editUserId.value) {
        const updatedSession = { ...currentUser.value, name: data.name, email: data.email }
        localStorage.setItem('user', JSON.stringify(updatedSession))
        currentUser.value = updatedSession
      }
    } else {
      showAlert(data.message || 'Có lỗi xảy ra khi cập nhật!', 'error')
    }
  } catch (err) {
    console.error(err)
    showAlert('Lỗi kết nối máy chủ backend!', 'error')
  }
}

const handleDeleteUser = async (user: User) => {
  if (currentUser.value && currentUser.value.id === user.id) {
    showAlert('Không thể tự xóa tài khoản của chính mình!', 'error')
    addLog(`Yêu cầu xóa tài khoản ${user.email} bị từ chối (tự xóa tài khoản đang đăng nhập)`, 'warning')
    return
  }

  if (!confirm(`Bạn có chắc chắn muốn xóa người dùng "${user.name}" (${user.email})?`)) {
    return
  }

  try {
    const res = await fetch(`http://localhost:8080/api/users/${user.id}?loggedInUserId=${currentUser.value?.id || ''}`, {
      method: 'DELETE'
    })

    const data = await res.json()
    if (res.ok) {
      addLog(`Đã xóa tài khoản người dùng ${user.email}`, 'warning')
      showAlert('Xóa người dùng thành công!', 'success')
      fetchUsers()
    } else {
      showAlert(data.message || 'Có lỗi xảy ra khi xóa người dùng!', 'error')
    }
  } catch (err) {
    console.error(err)
    showAlert('Lỗi kết nối máy chủ backend!', 'error')
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return d.toLocaleDateString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<template>
  <div class="dashboard-container">
    <!-- Header of Dashboard -->
    <header class="dashboard-header animate-float-subtle">
      <div class="header-intro">
        <div class="badge-role">
          <Shield :size="14" />
          <span>Quản trị viên Hệ thống</span>
        </div>
        <h1>Bảng Quản Trị Hệ Thống</h1>
        <p>Giám sát người dùng, cơ sở dữ liệu và hoạt động của hệ thống thời gian thực.</p>
      </div>

      <!-- Quick welcome card -->
      <div class="welcome-card glass-card">
        <div class="avatar-circle">
          <span>{{ currentUser?.name ? currentUser.name.charAt(0).toUpperCase() : 'A' }}</span>
        </div>
        <div class="welcome-text">
          <h3>Xin chào, {{ currentUser?.name || 'Admin' }}</h3>
          <p>{{ currentUser?.email }}</p>
        </div>
      </div>
    </header>

    <!-- Global Alert Popup -->
    <transition name="alert-slide">
      <div v-if="feedbackMessage" class="floating-alert" :class="feedbackType">
        <CheckCircle v-if="feedbackType === 'success'" :size="18" />
        <AlertTriangle v-else :size="18" />
        <span>{{ feedbackMessage }}</span>
      </div>
    </transition>

    <!-- Stats Section -->
    <section class="stats-grid">
      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper purple-glow">
          <Users class="text-purple" :size="24" />
        </div>
        <div class="stat-info">
          <h2>{{ users.length }}</h2>
          <p>Tổng Người Dùng</p>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper pink-glow">
          <Database class="text-pink" :size="24" />
        </div>
        <div class="stat-info">
          <h2>H2 Database</h2>
          <p>Tệp Cơ Sở Dữ Liệu</p>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper blue-glow">
          <Activity class="text-blue" :size="24" />
        </div>
        <div class="stat-info">
          <h2>200 OK</h2>
          <p>Trạng Thái Máy Chủ</p>
        </div>
      </div>

      <div class="stat-card glass-card">
        <div class="stat-icon-wrapper cyan-glow">
          <Clock class="text-cyan" :size="24" />
        </div>
        <div class="stat-info">
          <h2>{{ uptime }}</h2>
          <p>Thời Gian Hoạt Động</p>
        </div>
      </div>
    </section>

    <!-- Main Workspace (Table + Logs) -->
    <div class="workspace-grid">
      <!-- Users Management Table -->
      <section class="users-section glass-card">
        <div class="section-actions-header">
          <div class="section-title">
            <h2>Quản Lý Tài Khoản</h2>
            <p>Danh sách người dùng đăng ký trên hệ thống.</p>
          </div>
          <button class="btn-aurora" @click="openAddModal">
            <Plus :size="16" />
            <span>Thêm Thành Viên</span>
          </button>
        </div>

        <!-- Search Bar -->
        <div class="search-bar-wrapper">
          <Search :size="18" class="search-icon" />
          <input
            type="text"
            placeholder="Tìm kiếm người dùng bằng tên hoặc email..."
            v-model="searchQuery"
            class="search-input"
          />
          <span v-if="searchQuery" class="clear-search" @click="searchQuery = ''">
            <X :size="16" />
          </span>
        </div>

        <!-- Table View -->
        <div class="table-scroll-container">
          <table class="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Họ & Tên</th>
                <th>Email</th>
                <th>Ngày Tạo</th>
                <th>Hành Động</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="loading">
                <td colspan="5" class="loading-state">Đang tải danh sách người dùng...</td>
              </tr>
              <tr v-else-if="filteredUsers.length === 0">
                <td colspan="5" class="empty-state">Không tìm thấy người dùng phù hợp.</td>
              </tr>
              <tr v-else v-for="user in filteredUsers" :key="user.id">
                <td class="col-id">#{{ user.id }}</td>
                <td class="col-name">
                  <div class="user-profile-cell">
                    <span class="user-avatar-small">{{ user.name.charAt(0).toUpperCase() }}</span>
                    <span class="user-fullname">{{ user.name }}</span>
                    <span v-if="currentUser && currentUser.id === user.id" class="badge-self">Tôi</span>
                  </div>
                </td>
                <td class="col-email">{{ user.email }}</td>
                <td class="col-date">{{ formatDate(user.createdAt) }}</td>
                <td class="col-actions">
                  <button class="action-btn edit-btn" @click="openEditModal(user)" title="Sửa">
                    <Edit :size="14" />
                  </button>
                  <button
                    class="action-btn delete-btn"
                    :disabled="currentUser?.id === user.id"
                    @click="handleDeleteUser(user)"
                    title="Xóa"
                  >
                    <Trash2 :size="14" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <!-- Sidebar Area (System Logs) -->
      <section class="logs-section glass-card">
        <div class="logs-header">
          <h2>Nhật Ký Hệ Thống</h2>
          <p>Các sự kiện hệ thống gần đây.</p>
        </div>
        <div class="logs-container">
          <div v-for="(log, idx) in logs" :key="idx" class="log-item" :class="log.type">
            <span class="log-time">{{ log.time }}</span>
            <span class="log-msg">{{ log.message }}</span>
          </div>
        </div>
      </section>
    </div>

    <!-- Modals Section -->
    <!-- Add User Modal -->
    <transition name="modal-fade">
      <div v-if="showAddModal" class="modal-overlay" @click.self="showAddModal = false">
        <div class="modal-content glass-card animate-zoom">
          <div class="modal-header">
            <h2>Tạo Tài Khoản Mới</h2>
            <button class="close-btn" @click="showAddModal = false">
              <X :size="20" />
            </button>
          </div>

          <form @submit.prevent="handleAddUser" class="modal-form">
            <div class="form-group">
              <label>Họ và Tên</label>
              <div class="modal-input-wrapper">
                <UserIcon class="form-input-icon" :size="16" />
                <input type="text" v-model="nameField" placeholder="Nguyễn Văn A" required />
              </div>
            </div>

            <div class="form-group">
              <label>Email</label>
              <div class="modal-input-wrapper">
                <Mail class="form-input-icon" :size="16" />
                <input type="email" v-model="emailField" placeholder="example@lumora.vn" required />
              </div>
            </div>

            <div class="form-group">
              <label>Mật Khẩu</label>
              <div class="modal-input-wrapper">
                <Key class="form-input-icon" :size="16" />
                <input type="password" v-model="passwordField" placeholder="Tối thiểu 6 ký tự" required />
              </div>
            </div>

            <div class="form-group">
              <label>Xác Nhận Mật Khẩu</label>
              <div class="modal-input-wrapper">
                <Key class="form-input-icon" :size="16" />
                <input type="password" v-model="confirmPasswordField" placeholder="Nhập lại mật khẩu" required />
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-glass" @click="showAddModal = false">Hủy Bỏ</button>
              <button type="submit" class="btn-aurora">Xác Nhận</button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Edit User Modal -->
    <transition name="modal-fade">
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="modal-content glass-card animate-zoom">
          <div class="modal-header">
            <h2>Cập Nhật Thành Viên</h2>
            <button class="close-btn" @click="showEditModal = false">
              <X :size="20" />
            </button>
          </div>

          <form @submit.prevent="handleEditUser" class="modal-form">
            <div class="form-group">
              <label>Họ và Tên</label>
              <div class="modal-input-wrapper">
                <UserIcon class="form-input-icon" :size="16" />
                <input type="text" v-model="nameField" required />
              </div>
            </div>

            <div class="form-group">
              <label>Email</label>
              <div class="modal-input-wrapper">
                <Mail class="form-input-icon" :size="16" />
                <input type="email" v-model="emailField" required />
              </div>
            </div>

            <div class="form-group">
              <label>Mật Khẩu Mới (Bỏ trống nếu không đổi)</label>
              <div class="modal-input-wrapper">
                <Key class="form-input-icon" :size="16" />
                <input type="password" v-model="passwordField" placeholder="Nhập mật khẩu mới nếu muốn đổi" />
              </div>
            </div>

            <div class="form-group">
              <label>Xác Nhận Mật Khẩu Mới</label>
              <div class="modal-input-wrapper">
                <Key class="form-input-icon" :size="16" />
                <input type="password" v-model="confirmPasswordField" placeholder="Nhập lại mật khẩu mới" />
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn-glass" @click="showEditModal = false">Hủy Bỏ</button>
              <button type="submit" class="btn-aurora">Cập Nhật</button>
            </div>
          </form>
        </div>
      </div>
    </transition>
  </div>
</template>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 24px 80px 24px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* Header style */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.header-intro h1 {
  font-size: 2.4rem;
  font-weight: 800;
  margin-top: 6px;
  margin-bottom: 8px;
}

.badge-role {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.2);
  border-radius: 99px;
  color: var(--purple);
  font-weight: 600;
  font-size: 0.8rem;
}

.welcome-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 24px;
}

.avatar-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--purple), var(--cyan));
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: var(--white);
  font-size: 1.2rem;
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.25);
}

.welcome-text h3 {
  font-size: 1.05rem;
  color: var(--white);
}

.welcome-text p {
  font-size: 0.85rem;
  color: var(--text-muted);
}

/* Stats Section */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
}

.stat-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--glass-border);
}

.purple-glow {
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.15);
  border-color: rgba(139, 92, 246, 0.2);
}

.pink-glow {
  box-shadow: 0 0 15px rgba(236, 72, 153, 0.15);
  border-color: rgba(236, 72, 153, 0.2);
}

.blue-glow {
  box-shadow: 0 0 15px rgba(59, 130, 246, 0.15);
  border-color: rgba(59, 130, 246, 0.2);
}

.cyan-glow {
  box-shadow: 0 0 15px rgba(6, 182, 212, 0.15);
  border-color: rgba(6, 182, 212, 0.2);
}

.stat-info h2 {
  font-size: 1.5rem;
  color: var(--white);
  margin-bottom: 2px;
}

.stat-info p {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

/* Workspace layout */
.workspace-grid {
  display: grid;
  grid-template-columns: 2.2fr 1fr;
  gap: 24px;
}

.users-section {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.section-actions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-title h2 {
  font-size: 1.4rem;
  color: var(--white);
}

.section-title p {
  font-size: 0.85rem;
  color: var(--text-muted);
}

/* Search bar */
.search-bar-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  left: 16px;
  color: var(--text-muted);
}

.search-input {
  width: 100%;
  padding: 12px 44px 12px 46px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  color: var(--white);
  outline: none;
  font-family: inherit;
  font-size: 0.9rem;
  transition: var(--transition-fast);
}

.search-input:focus {
  background: rgba(255, 255, 255, 0.04);
  border-color: var(--purple);
  box-shadow: 0 0 15px rgba(139, 92, 246, 0.1);
}

.clear-search {
  position: absolute;
  right: 16px;
  cursor: pointer;
  color: var(--text-muted);
}

.clear-search:hover {
  color: var(--pink);
}

/* Users Table */
.table-scroll-container {
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}

.users-table th {
  padding: 14px 16px;
  font-size: 0.8rem;
  text-transform: uppercase;
  color: var(--text-muted);
  border-bottom: 1px solid var(--glass-border);
  font-weight: 700;
  letter-spacing: 0.05em;
}

.users-table td {
  padding: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  font-size: 0.9rem;
}

.users-table tr:hover td {
  background: rgba(255, 255, 255, 0.01);
}

.col-id {
  color: var(--text-muted);
  font-weight: 500;
}

.col-name {
  color: var(--white);
  font-weight: 600;
}

.user-profile-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar-small {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--glass-border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 700;
  color: var(--cyan);
}

.badge-self {
  font-size: 0.7rem;
  padding: 2px 6px;
  background: rgba(6, 182, 212, 0.12);
  border: 1px solid rgba(6, 182, 212, 0.25);
  border-radius: 4px;
  color: var(--cyan);
  font-weight: 600;
}

.col-email {
  color: var(--text-secondary);
}

.col-date {
  color: var(--text-muted);
  font-size: 0.82rem;
}

/* Actions in table */
.col-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid var(--glass-border);
  background: rgba(255, 255, 255, 0.02);
  color: var(--text-muted);
  cursor: pointer;
  transition: var(--transition-fast);
}

.edit-btn:hover {
  background: rgba(6, 182, 212, 0.1);
  border-color: var(--cyan);
  color: var(--cyan);
}

.delete-btn:hover:not(:disabled) {
  background: rgba(236, 72, 153, 0.1);
  border-color: var(--pink);
  color: var(--pink);
}

.delete-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.loading-state,
.empty-state {
  text-align: center;
  padding: 40px !important;
  color: var(--text-muted);
}

/* Logs section */
.logs-section {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.logs-header h2 {
  font-size: 1.4rem;
  color: var(--white);
}

.logs-header p {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.logs-container {
  flex: 1;
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-right: 8px;
}

.log-item {
  display: flex;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.01);
  border: 1px solid rgba(255, 255, 255, 0.02);
  font-size: 0.8rem;
  line-height: 1.4;
}

.log-time {
  color: var(--text-muted);
  font-family: monospace;
  font-weight: 600;
  flex-shrink: 0;
}

.log-msg {
  color: var(--text-secondary);
}

/* Log item types */
.log-item.success {
  border-color: rgba(6, 182, 212, 0.15);
  background: rgba(6, 182, 212, 0.03);
}
.log-item.success .log-msg {
  color: var(--cyan);
}

.log-item.warning {
  border-color: rgba(245, 158, 11, 0.15);
  background: rgba(245, 158, 11, 0.03);
}
.log-item.warning .log-msg {
  color: #f59e0b;
}

.log-item.error {
  border-color: rgba(236, 72, 153, 0.15);
  background: rgba(236, 72, 153, 0.03);
}
.log-item.error .log-msg {
  color: var(--pink);
}

/* Modals styling */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  backdrop-filter: blur(8px);
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.modal-content {
  width: 100%;
  max-width: 480px;
  padding: 32px;
  position: relative;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5);
  border-color: rgba(255, 255, 255, 0.12);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 1.5rem;
  color: var(--white);
}

.close-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  transition: var(--transition-fast);
}

.close-btn:hover {
  color: var(--pink);
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-secondary);
}

.modal-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.form-input-icon {
  position: absolute;
  left: 16px;
  color: var(--text-muted);
}

.modal-input-wrapper input {
  width: 100%;
  padding: 12px 16px 12px 42px;
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid var(--glass-border);
  border-radius: 10px;
  color: var(--white);
  outline: none;
  font-family: inherit;
  font-size: 0.9rem;
  transition: var(--transition-fast);
}

.modal-input-wrapper input:focus {
  background: rgba(255, 255, 255, 0.04);
  border-color: var(--purple);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 10px;
}

.form-actions button {
  padding: 10px 22px;
  font-size: 0.9rem;
}

/* Floating Alerts Banner */
.floating-alert {
  position: fixed;
  top: 96px;
  right: 24px;
  z-index: 300;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.92rem;
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  animation: slide-in 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

.floating-alert.success {
  background: rgba(6, 182, 212, 0.15);
  border: 1px solid rgba(6, 182, 212, 0.35);
  color: var(--cyan);
  box-shadow: 0 0 25px rgba(6, 182, 212, 0.15);
}

.floating-alert.error {
  background: rgba(236, 72, 153, 0.15);
  border: 1px solid rgba(236, 72, 153, 0.35);
  color: var(--pink);
  box-shadow: 0 0 25px rgba(236, 72, 153, 0.15);
}

/* Animations */
@keyframes slide-in {
  from {
    transform: translateX(100%) scale(0.9);
    opacity: 0;
  }
  to {
    transform: translateX(0) scale(1);
    opacity: 1;
  }
}

.alert-slide-enter-active,
.alert-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}
.alert-slide-enter-from,
.alert-slide-leave-to {
  opacity: 0;
  transform: translateX(30px) scale(0.9);
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.animate-zoom {
  animation: zoom-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes zoom-in {
  from {
    transform: scale(0.85);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.animate-float-subtle {
  animation: float-subtle 8s ease-in-out infinite;
}

@keyframes float-subtle {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@media (max-width: 968px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }
  .welcome-card {
    width: 100%;
  }
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .workspace-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  .section-actions-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .section-actions-header button {
    width: 100%;
  }
}
</style>
