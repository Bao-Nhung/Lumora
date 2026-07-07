-- ======================================================================
-- DỰ ÁN LUMORA - THIẾT KẾ CƠ SỞ DỮ LIỆU PHÂN QUYỀN (SQL SERVER)
-- File: database/lumora.sql
-- ======================================================================

-- ======================================================================
-- PHƯƠNG ÁN 1: THIẾT KẾ CHUẨN HÓA (Normalized Database Design) - KHUYÊN DÙNG
-- Tách riêng bảng Roles và bảng Users liên kết thông qua Khóa ngoại (Foreign Key)
-- ======================================================================

-- 1. Tạo bảng Roles (Quản lý các nhóm quyền)
CREATE TABLE Roles (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    RoleName NVARCHAR(50) NOT NULL UNIQUE,      -- Tên quyền (VD: 'ADMIN', 'CUSTOMER')
    Description NVARCHAR(255) NULL              -- Mô tả chi tiết chức năng quyền
);

-- 2. Tạo bảng Users (Quản lý thông tin người dùng liên kết với Roles)
CREATE TABLE Users (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,                -- Họ tên người dùng
    Email VARCHAR(100) NOT NULL UNIQUE,          -- Email dùng đăng nhập (độc nhất)
    Password VARCHAR(255) NOT NULL,             -- Mật khẩu mã hóa (SHA-256)
    RoleId INT NOT NULL,                        -- ID quyền liên kết
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(), -- Thời gian khởi tạo tài khoản
    CONSTRAINT FK_Users_Roles FOREIGN KEY (RoleId) REFERENCES Roles(Id)
);

-- 3. Thêm dữ liệu mẫu vào bảng Roles
INSERT INTO Roles (RoleName, Description) VALUES 
('ADMIN', N'Quản trị viên - Có toàn quyền quản lý hệ thống, người dùng và dữ liệu'),
('CUSTOMER', N'Khách hàng - Có quyền xem sản phẩm, đặt hàng và quản lý tài khoản cá nhân');

-- 4. Thêm tài khoản mẫu mặc định (mã hóa mật khẩu bằng SHA-256 qua hàm HASHBYTES)
-- Mật khẩu mặc định:
-- admin -> Admin56270 (Vai trò ADMIN)
-- ngbao -> Bao56270 (Vai trò CUSTOMER)
DECLARE @AdminRoleId INT, @CustomerRoleId INT;
SELECT @AdminRoleId = Id FROM Roles WHERE RoleName = 'ADMIN';
SELECT @CustomerRoleId = Id FROM Roles WHERE RoleName = 'CUSTOMER';

INSERT INTO Users (Name, Email, Password, RoleId) VALUES
(N'Admin', 'admin', LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', 'Admin56270'), 2)), @AdminRoleId),
(N'NgBao', 'ngbao', LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', 'Bao56270'), 2)), @CustomerRoleId);

GO


/*
-- ======================================================================
-- PHƯƠNG ÁN 2: THIẾT KẾ BẢNG PHẲNG (Flat Table)
-- Lưu trực tiếp chuỗi Role trong bảng Users.
-- Thiết kế này khớp 1-1 với Entity Java hiện tại trong mã nguồn của bạn (User.java)
-- ======================================================================

CREATE TABLE Users (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    Role NVARCHAR(50) NOT NULL DEFAULT 'CUSTOMER',
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    -- Ràng buộc (Constraint) chỉ chấp nhận các vai trò hợp lệ
    CONSTRAINT CK_Users_Role CHECK (Role IN ('ADMIN', 'CUSTOMER', 'USER'))
);

-- Thêm tài khoản mẫu mặc định cho Phương án 2
INSERT INTO Users (Name, Email, Password, Role) VALUES
(N'Admin', 'admin', LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', 'Admin56270'), 2)), 'ADMIN'),
(N'NgBao', 'ngbao', LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', 'Bao56270'), 2)), 'USER');
*/
