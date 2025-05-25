# EduSports - Tài liệu API Quản Lý Người Dùng

## Tổng quan
Hệ thống quản lý người dùng hoàn chỉnh cho ứng dụng EduSports với các tính năng:
- Quản lý người dùng cho Admin
- Quản lý profile cá nhân
- Reset mật khẩu
- Quản lý roles
- Upload avatar
- Tìm kiếm và phân trang

## Authentication
Tất cả API đều yêu cầu JWT token trong header:
```
Authorization: Bearer <your-jwt-token>
```

## I. Admin User Management APIs

### 1. Tạo User Mới (Admin Only)
**POST** `/api/v1/admin/users`

**Headers:**
- Authorization: Bearer <admin-token>
- Content-Type: application/json

**Request Body:**
```json
{
  "name": "Nguyễn Văn A",
  "email": "user@example.com",
  "password": "password123",
  "isActive": true,
  "roles": ["STUDENT", "ORGANIZER"],
  "phone": "0123456789",
  "avatar": null
}
```

**Response:**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "name": "Nguyễn Văn A",
    "email": "user@example.com",
    "isActive": true,
    "createdAt": "2025-05-25T10:00:00Z",
    "lastLogin": null,
    "roles": ["STUDENT", "ORGANIZER"],
    "phone": "0123456789",
    "avatar": null
  }
}
```

### 2. Lấy Danh Sách Users với Phân Trang
**GET** `/api/v1/admin/users`

**Query Parameters:**
- `page`: Số trang (default: 0)
- `size`: Kích thước trang (default: 10)
- `sortBy`: Trường sắp xếp (default: "id")
- `sortDirection`: Hướng sắp xếp (ASC/DESC, default: "ASC")
- `search`: Tìm kiếm theo name hoặc email
- `role`: Lọc theo role (ADMIN/ORGANIZER/STUDENT)
- `isActive`: Lọc theo trạng thái (true/false)

**Example:**
```
GET /api/v1/admin/users?page=0&size=10&search=nguyen&role=STUDENT&isActive=true
```

**Response:**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 50,
    "totalPages": 5,
    "first": true,
    "last": false
  }
}
```

### 3. Lấy User Theo ID
**GET** `/api/v1/admin/users/{id}`

### 4. Cập Nhật User
**PUT** `/api/v1/admin/users/{id}`

### 5. Xóa User (Soft Delete)
**DELETE** `/api/v1/admin/users/{id}`

### 6. Kích Hoạt/Vô Hiệu Hóa User
**PATCH** `/api/v1/admin/users/{id}/status?isActive=true`

### 7. Gán Roles cho User
**PATCH** `/api/v1/admin/users/{id}/roles`

**Request Body:**
```json
["ADMIN", "ORGANIZER"]
```

### 8. Reset Mật Khẩu User
**PATCH** `/api/v1/admin/users/{id}/reset-password?newPassword=newpass123`

### 9. Thống Kê Users
**GET** `/api/v1/admin/users/statistics`

**Response:**
```json
{
  "success": true,
  "data": {
    "totalUsers": 100,
    "activeUsers": 95,
    "inactiveUsers": 5,
    "roleStatistics": {
      "ADMIN": 2,
      "ORGANIZER": 15,
      "STUDENT": 83
    },
    "newUsersLast30Days": 12
  }
}
```

### 10. Xóa Nhiều Users
**DELETE** `/api/v1/admin/users/bulk`

**Request Body:**
```json
[1, 2, 3, 4, 5]
```

### 11. Export Users to CSV
**GET** `/api/v1/admin/users/export?search=&role=&isActive=`

## II. User Profile Management APIs

### 1. Lấy Profile Hiện Tại
**GET** `/api/v1/user/profile`

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Nguyễn Văn A",
    "email": "user@example.com",
    "phone": "0123456789",
    "avatar": "/uploads/avatars/avatar_1_uuid.jpg",
    "isActive": true,
    "createdAt": "2025-05-25T10:00:00Z",
    "lastLogin": "2025-05-25T15:30:00Z",
    "roles": ["STUDENT"]
  }
}
```

### 2. Cập Nhật Profile
**PUT** `/api/v1/user/profile`

**Request Body:**
```json
{
  "name": "Nguyễn Văn B",
  "email": "newmail@example.com",
  "phone": "0987654321"
}
```

### 3. Đổi Mật Khẩu
**PATCH** `/api/v1/user/profile/change-password?currentPassword=oldpass&newPassword=newpass123`

### 4. Upload Avatar
**POST** `/api/v1/user/profile/avatar`

**Request:** multipart/form-data
- `file`: Image file (max 5MB)

**Response:**
```json
{
  "success": true,
  "message": "Avatar uploaded successfully",
  "data": "/uploads/avatars/avatar_1_uuid.jpg"
}
```

### 5. Xóa Avatar
**DELETE** `/api/v1/user/profile/avatar`

### 6. Vô Hiệu Hóa Tài Khoản
**PATCH** `/api/v1/user/profile/deactivate`

## III. Password Reset APIs

### 1. Yêu Cầu Reset Mật Khẩu
**POST** `/api/v1/auth/password/forgot?email=user@example.com`

### 2. Xác Thực Reset Token
**GET** `/api/v1/auth/password/reset/verify?token=reset-token-uuid`

**Response:**
```json
{
  "success": true,
  "data": {
    "valid": true,
    "email": "user@example.com",
    "expiresAt": "2025-05-25T16:00:00Z"
  }
}
```

### 3. Reset Mật Khẩu
**POST** `/api/v1/auth/password/reset?token=reset-token-uuid&newPassword=newpass123`

## IV. Role Management APIs (Admin Only)

### 1. Lấy Danh Sách Roles
**GET** `/api/v1/admin/roles`

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "ADMIN",
      "userCount": 2
    },
    {
      "id": 2,
      "name": "ORGANIZER", 
      "userCount": 15
    },
    {
      "id": 3,
      "name": "STUDENT",
      "userCount": 83
    }
  ]
}
```

### 2. Thống Kê Roles
**GET** `/api/v1/admin/roles/statistics`

### 3. Lấy Users Theo Role
**GET** `/api/v1/admin/roles/{roleName}/users`

**Example:** `/api/v1/admin/roles/STUDENT/users`

## V. Existing Auth APIs (Enhanced)

### 1. Đăng Ký
**POST** `/api/v1/auth/register`

### 2. Đăng Nhập
**POST** `/api/v1/auth/login`

### 3. Lấy Thông Tin Tài Khoản
**GET** `/api/v1/auth/account`

### 4. Đăng Xuất
**POST** `/api/v1/auth/logout`

### 5. Refresh Token
**POST** `/api/v1/auth/refresh`

## Status Codes
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden
- `404`: Not Found
- `500`: Internal Server Error

## Error Response Format
```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

## Notes
1. Tất cả timestamps sử dụng định dạng ISO 8601 UTC
2. File upload giới hạn 5MB cho avatar
3. Password reset token có hiệu lực trong 1 giờ
4. Soft delete: Users không bị xóa khỏi database, chỉ set isActive = false
5. Role hierarchy: ADMIN > ORGANIZER > STUDENT
6. Chỉ ADMIN mới có thể truy cập các API admin/*

## Cấu Hình Cần Thiết

### application.properties
```properties
# File upload
app.upload.dir=uploads
app.upload.max-file-size=5MB

# JWT
checkscam.jwt.refresh-token-validity-in-seconds=86400
```

### Database Migration
Cần tạo các bảng mới:
- password_reset_tokens
- Thêm columns vào bảng users: last_login, phone, avatar

## Các Tính Năng Đã Hoàn Thiện
✅ CRUD users cho admin
✅ Tìm kiếm và phân trang
✅ Profile management
✅ Upload avatar
✅ Password reset
✅ Role management  
✅ User statistics
✅ Bulk operations
✅ CSV export
✅ Input validation
✅ Error handling
✅ Security (role-based access)
