# Team Management APIs Implementation

## APIs Implemented

### 3.1 Get Teams by Tournament
- **Endpoint:** `GET /api/tournaments/{tournament_id}/teams`
- **Description:** Lấy danh sách các đội trong một giải đấu
- **Authentication:** Không yêu cầu
- **Response:** Danh sách các đội với thông tin cơ bản

### 3.2 Get Team by ID  
- **Endpoint:** `GET /api/teams/{id}`
- **Description:** Lấy thông tin chi tiết của một đội
- **Authentication:** Không yêu cầu
- **Response:** Thông tin chi tiết đội bao gồm giải đấu và lịch thi đấu

### 3.3 Create Team (Register for Tournament)
- **Endpoint:** `POST /api/tournaments/{tournament_id}/register`
- **Description:** Đăng ký đội tham gia giải đấu
- **Authentication:** Yêu cầu (USER, ADMIN, ORGANIZER)
- **Request Body:** Thông tin đội và đăng ký
- **Response:** Thông tin đăng ký và đội vừa tạo

### 3.4 Update Team
- **Endpoint:** `PUT /api/teams/{id}`
- **Description:** Cập nhật thông tin đội
- **Authentication:** Yêu cầu (Đội trưởng hoặc ADMIN/ORGANIZER)
- **Request Body:** Thông tin cần cập nhật
- **Response:** Thông tin đội sau khi cập nhật

### 3.5 Delete Team
- **Endpoint:** `DELETE /api/teams/{id}`  
- **Description:** Xóa đội
- **Authentication:** Yêu cầu (Đội trưởng hoặc ADMIN)
- **Response:** Thông báo xóa thành công

## DTOs Created

1. **TeamResponseDTO** - Response cho thông tin đội
2. **TeamRegistrationRequestDTO** - Request đăng ký đội
3. **TeamRegistrationResponseDTO** - Response đăng ký đội
4. **TeamUpdateRequestDTO** - Request cập nhật đội
5. **TeamUpdateResponseDTO** - Response cập nhật đội

## Services & Repositories

1. **TeamService** - Interface định nghĩa các phương thức
2. **TeamServiceImpl** - Implementation của TeamService
3. **TournamentRegistrationRepository** - Repository cho đăng ký giải đấu
4. Updated **TeamRepository** - Thêm các query cần thiết
5. Updated **MatchRepository** - Thêm query tìm match theo đội

## Controller

**TeamController** - REST controller xử lý các endpoint team management

## Security & Validation

- Sử dụng Spring Security để authentication và authorization
- Validation cho input data
- Permission check cho update/delete operations
- Business logic validation (unique team name, tournament capacity, etc.)

## Key Features

1. **Auto-approval registration** - Đăng ký đội tự động được phê duyệt
2. **Permission-based operations** - Chỉ đội trưởng hoặc admin mới có thể sửa/xóa đội
3. **Tournament capacity check** - Kiểm tra giới hạn số đội trong giải
4. **Unique team name** - Tên đội phải unique trong một giải đấu
5. **Match integration** - Hiển thị lịch thi đấu của đội
6. **Registration deadline check** - Kiểm tra hạn đăng ký

## Database Relations

- Team belongs to Tournament (Many-to-One)
- Team has Captain (Many-to-One with User)  
- TournamentRegistration links User and Tournament
- Match references Team1 and Team2
