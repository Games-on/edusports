# Hướng dẫn Test Tournament APIs - Đã Fix Access Denied

## 🔧 **Các lỗi đã fix:**

### 1. **Security Configuration Issues**
- ✅ Fix authority prefix inconsistency (ROLE_ prefix)
- ✅ Update security rules để cho phép auth endpoints
- ✅ Fix JWT authentication converter

### 2. **Role System Setup**  
- ✅ Tạo default roles: ADMIN, ORGANIZER, STUDENT
- ✅ Tạo default users với roles
- ✅ Update @PreAuthorize để sử dụng hasAuthority thay vì hasRole

### 3. **Access Control**
- ✅ GET tournaments: Public access
- ✅ POST/PUT/DELETE tournaments: Requires ADMIN or ORGANIZER role
- ✅ Auth endpoints: Public access

## 🚀 **Cách test APIs:**

### **Bước 1: Khởi động application**
Sau khi khởi động, bạn sẽ thấy logs:
```
✅ Đã tạo các role ADMIN, ORGANIZER, và STUDENT.
✅ Đã tạo tài khoản ADMIN mặc định (admin@gmail.com / 123456).
✅ Đã tạo tài khoản ORGANIZER mặc định (organizer@gmail.com / 123456).
```

### **Bước 2: Login để lấy token**

**POST** `http://localhost:8080/api/v1/auth/login`
```json
{
  "username": "admin@gmail.com",
  "password": "123456"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "access_token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
      "id": 1,
      "email": "admin@gmail.com",
      "name": "Admin User"
    }
  }
}
```

### **Bước 3: Test Tournament APIs**

**Tạo Tournament (với token ADMIN/ORGANIZER)**
**POST** `http://localhost:8080/api/tournaments`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json
```

**Body:**
```json
{
  "name": "Giải Cầu lông Sinh viên 2025",
  "sportType": "Cầu lông",
  "description": "Giải đấu cầu lông dành cho sinh viên toàn trường",
  "maxTeams": 16,
  "startDate": "2025-06-01T08:00:00Z",
  "endDate": "2025-06-05T18:00:00Z",
  "location": "Nhà thi đấu A",
  "registrationDeadline": "2025-05-25T23:59:59Z",
  "rules": "Knockout 16 đội, thi đấu 3 sets",
  "prizeInfo": "Giải nhất: 5 triệu, Giải nhì: 3 triệu",
  "contactInfo": "Email: caulongsv@gmail.com"
}
```

### **Bước 4: Test các API khác**

**Get All Tournaments (Public)**
**GET** `http://localhost:8080/api/tournaments`

**Get Tournament by ID (Public)**
**GET** `http://localhost:8080/api/tournaments/1`

**Update Tournament (Requires Auth)**
**PUT** `http://localhost:8080/api/tournaments/1`
```json
{
  "name": "Giải Cầu lông Sinh viên 2025 - Updated",
  "description": "Updated description",
  "maxTeams": 20
}
```

**Start Tournament (Requires Auth)**
**POST** `http://localhost:8080/api/tournaments/1/start`

**Delete Tournament (ADMIN only)**
**DELETE** `http://localhost:8080/api/tournaments/1`

## 👥 **Default Accounts:**

| Email | Password | Role | Permissions |
|-------|----------|------|-------------|
| admin@gmail.com | 123456 | ADMIN | All tournament operations |
| organizer@gmail.com | 123456 | ORGANIZER | Create, Update, Start tournaments |

## 🔍 **Troubleshooting:**

### Nếu vẫn gặp lỗi Access Denied:
1. **Kiểm tra token:** Đảm bảo token được include trong header
2. **Kiểm tra role:** Sử dụng account có role phù hợp
3. **Check logs:** Xem JWT parsing có lỗi không
4. **Verify endpoint:** Đảm bảo endpoint path đúng

### Debug JWT Token:
```bash
# Decode JWT token tại jwt.io để kiểm tra claims
# Payload should contain:
{
  "sub": "admin@gmail.com",
  "roles": ["ROLE_ADMIN"],
  "exp": 1234567890
}
```

## ✨ **Expected Results:**

- ✅ Login thành công với admin/organizer
- ✅ Tạo tournament thành công với valid token
- ✅ Public endpoints (GET) hoạt động không cần token
- ✅ Protected endpoints yêu cầu valid token + role
- ✅ Proper error messages cho unauthorized requests

Bây giờ bạn có thể test lại API tạo tournament và sẽ không còn gặp lỗi Access Denied!
