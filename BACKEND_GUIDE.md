# Hướng Dẫn Chạy & Cấu Hình Backend Lumora

Tài liệu này hướng dẫn chi tiết cách chạy và cấu hình ứng dụng Backend (Java Spring Boot) cho dự án **Lumora**.

---

## 🛠️ Yêu cầu hệ thống
Trước khi khởi chạy, hãy đảm bảo máy tính của bạn đã cài đặt:
1. **Java JDK 21** trở lên.
2. Kiểm tra phiên bản Java bằng lệnh:
   ```bash
   java -version
   ```

---

## 🚀 Hướng dẫn khởi chạy Backend

### Cách 1: Sử dụng Maven Wrapper (Khuyên dùng)
Bạn không cần cài đặt sẵn Maven trên máy, dự án đã tích hợp sẵn Maven Wrapper (`mvnw`).

1. Mở Terminal (Command Prompt hoặc PowerShell trên Windows) và di chuyển vào thư mục backend chứa mã nguồn:
   ```bash
   cd f:\DEV\Web_Lumora\backend
   ```

2. Chạy lệnh khởi động Spring Boot:
   * **Trên Windows (PowerShell hoặc cmd):**
     ```powershell
     ./mvnw spring-boot:run
     ```
     ''''cmd
     mvnw spring-boot:run
   * **Trên macOS / Linux:**
     ```bash
     ./mvnw spring-boot:run
     ```

---

## 🔑 Tài khoản mặc định (Tự động khởi tạo)
Khi backend khởi động lần đầu tiên, hệ thống sẽ tự động chèn một tài khoản quản trị/thử nghiệm vào cơ sở dữ liệu nếu chưa tồn tại:
* **Tên đăng nhập (Username/Email):** `NgBao` (hoặc `ngbao`)
* **Mật khẩu:** `Bao56270`
* **Họ tên:** `NgBao`

*(Chức năng đăng ký tài khoản mới đã bị vô hiệu hóa để bảo mật và sử dụng nội bộ).*

---

## 💾 Quản lý cơ sở dữ liệu (H2 Database Console)
Backend sử dụng cơ sở dữ liệu **H2** dạng tệp tin lưu tại `database/lumora`. Bạn có thể truy cập giao diện quản lý cơ sở dữ liệu thông qua trình duyệt:

* **Đường dẫn**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* **Thông tin kết nối (Connection Settings)**:
  * **Saved Settings**: Generic H2 (Embedded)
  * **Driver Class**: `org.h2.Driver`
  * **JDBC URL**: `jdbc:h2:file:../database/lumora` (đường dẫn tương đối từ thư mục chạy ứng dụng)
  * **User Name**: `sa`
  * **Password**: `password`

> 💡 **Lưu ý**: Nhấn nút **Connect** hoặc **Test Connection** để truy cập trực tiếp các bảng cơ sở dữ liệu (như bảng `users`).

---

## ⚙️ Cấu hình cổng và kết nối khác
Các cấu hình cốt lõi của backend nằm tại tệp tin:
👉 [application.properties](file:///f:/DEV/Web_Lumora/backend/src/main/resources/application.properties)

* Cổng hoạt động mặc định: `8080`
* Để đổi cổng, sửa giá trị: `server.port=8080`
