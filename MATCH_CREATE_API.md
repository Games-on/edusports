# Match Management APIs Implementation - Updated

## APIs Implemented

### 4.1 Get Matches by Tournament
- **Endpoint:** `GET /api/tournaments/{tournament_id}/matches`
- **Query Parameters:** 
  - `round` (optional): Filter by round number
  - `status` (optional): SCHEDULED, LIVE, COMPLETED, CANCELLED
- **Description:** Lấy danh sách trận đấu theo giải đấu với khả năng filter
- **Authentication:** Không yêu cầu

### 4.2 Get Match by ID  
- **Endpoint:** `GET /api/matches/{id}`
- **Description:** Lấy thông tin chi tiết của một trận đấu
- **Authentication:** Không yêu cầu

### 4.2.1 Create Match
- **Endpoint:** `POST /api/tournaments/{tournament_id}/matches`
- **Description:** Tạo trận đấu mới cho giải đấu
- **Authentication:** Yêu cầu (ADMIN, ORGANIZER)
- **Request Body:** Thông tin trận đấu cần tạo
- **Response:** Thông tin trận đấu vừa tạo

**Request Body Example:**
```json
{
  "team1Id": 1,
  "team2Id": 2,
  "roundNumber": 1,
  "roundName": "Vòng 1", // Optional - will be auto-generated if not provided
  "matchDate": "2025-06-01T08:00:00",
  "location": "Sân A1",
  "matchNumber": 1, // Optional - will be auto-generated if not provided
  "referee": "Trọng tài A",
  "notes": "Ghi chú trận đấu"
}
```

**Response Example (201):**
```json
{
  "success": true,
  "message": "Match created successfully",
  "data": {
    "id": 1,
    "tournament": {
      "id": 1,
      "name": "Giải Cầu lông Sinh viên 2025",
      "sportType": "Cầu lông"
    },
    "roundNumber": 1,
    "roundName": "Vòng 1",
    "team1": {
      "id": 1,
      "name": "Team Alpha",
      "logoUrl": "https://example.com/logo1.png",
      "captain": {
        "id": 2,
        "name": "Nguyễn Văn A",
        "email": "nguyenvana@example.com"
      }
    },
    "team2": {
      "id": 2,
      "name": "Team Beta",
      "logoUrl": "https://example.com/logo2.png",
      "captain": {
        "id": 3,
        "name": "Nguyễn Văn B",
        "email": "nguyenvanb@example.com"
      }
    },
    "matchDate": "2025-06-01T08:00:00Z",
    "location": "Sân A1",
    "status": "SCHEDULED",
    "matchNumber": 1,
    "referee": "Trọng tài A",
    "notes": "Ghi chú trận đấu",
    "createdAt": "2025-05-24T10:00:00Z"
  }
}
```

### 4.3 Update Match Score
- **Endpoint:** `PUT /api/matches/{id}/score`
- **Description:** Cập nhật điểm số trận đấu
- **Authentication:** Yêu cầu (ADMIN, ORGANIZER)

### 4.4 Update Match Status
- **Endpoint:** `PUT /api/matches/{id}/status`
- **Description:** Cập nhật trạng thái trận đấu
- **Authentication:** Yêu cầu (ADMIN, ORGANIZER)

### 4.5 Get Tournament Bracket
- **Endpoint:** `GET /api/tournaments/{tournament_id}/bracket`
- **Description:** Lấy bảng đấu của giải
- **Authentication:** Không yêu cầu

## DTOs Created for Create Match

1. **MatchCreateRequestDTO** - Request tạo trận đấu mới
2. **MatchCreateResponseDTO** - Response sau khi tạo trận đấu

## Key Features of Create Match API

1. **Validation:**
   - Kiểm tra teams thuộc về tournament
   - Teams phải khác nhau (không thể tự đấu với mình)
   - Kiểm tra teams chưa đấu với nhau trong cùng vòng
   - Validate match date phải trong tương lai

2. **Auto-generation:**
   - Tự động tạo round name nếu không cung cấp
   - Tự động tạo match number nếu không cung cấp
   - Tự động set status = SCHEDULED
   - Tự động set score = 0-0

3. **Permission Control:**
   - Chỉ ADMIN và ORGANIZER mới có thể tạo trận đấu

4. **Business Logic:**
   - Kiểm tra logic nghiệp vụ khi tạo trận đấu
   - Đảm bảo tính nhất quán của dữ liệu

## Validation Rules

- **team1Id, team2Id:** Required, must exist and belong to tournament
- **roundNumber:** Required, minimum value = 1
- **matchDate:** Required, must be in the future
- **roundName:** Optional, max 100 characters
- **location:** Optional, max 200 characters
- **matchNumber:** Optional, minimum value = 1
- **referee:** Optional, max 100 characters
- **notes:** Optional, max 1000 characters

## Error Handling

The API handles various error scenarios:
- Invalid tournament ID
- Invalid team IDs
- Teams not belonging to tournament
- Teams already matched in same round
- Authentication/Authorization errors
- Validation errors
