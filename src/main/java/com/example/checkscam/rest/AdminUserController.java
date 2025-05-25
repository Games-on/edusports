package com.example.checkscam.rest;

import com.example.checkscam.dto.admin.AdminUserCreateRequestDTO;
import com.example.checkscam.dto.admin.AdminUserResponseDTO;
import com.example.checkscam.dto.request.UpdateUserRequest;
import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.dto.response.PagedResponseDTO;
import com.example.checkscam.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    /**
     * Tạo user mới bởi admin
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> createUser(
            @Valid @RequestBody AdminUserCreateRequestDTO request) {
        try {
            AdminUserResponseDTO user = adminUserService.createUser(request);
            return ResponseEntity.status(201)
                    .body(ApiResponse.success("User created successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to create user: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách tất cả users với phân trang và tìm kiếm
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponseDTO<AdminUserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive) {
        try {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PagedResponseDTO<AdminUserResponseDTO> users = adminUserService
                    .getAllUsers(pageable, search, role, isActive);
            
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }

    /**
     * Lấy thông tin user theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> getUserById(@PathVariable Long id) {
        try {
            AdminUserResponseDTO user = adminUserService.getUserById(id);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User retrieved successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật thông tin user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody AdminUserCreateRequestDTO request) {
        try {
            AdminUserResponseDTO user = adminUserService.updateUser(id, request);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user: " + e.getMessage()));
        }
    }

    /**
     * Xóa user (soft delete - set isActive = false)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            adminUserService.deleteUser(id);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete user: " + e.getMessage()));
        }
    }

    /**
     * Kích hoạt/vô hiệu hóa user
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam boolean isActive) {
        try {
            AdminUserResponseDTO user = adminUserService.toggleUserStatus(id, isActive);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User status updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user status: " + e.getMessage()));
        }
    }

    /**
     * Gán roles cho user
     */
    @PatchMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<AdminUserResponseDTO>> assignRoles(
            @PathVariable Long id,
            @RequestBody Set<String> roles) {
        try {
            AdminUserResponseDTO user = adminUserService.assignRoles(id, roles);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User roles updated successfully", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update user roles: " + e.getMessage()));
        }
    }

    /**
     * Reset mật khẩu user
     */
    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(
            @PathVariable Long id,
            @RequestParam String newPassword) {
        try {
            adminUserService.resetUserPassword(id, newPassword);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Password reset successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to reset password: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê users
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getUserStatistics() {
        try {
            Object stats = adminUserService.getUserStatistics();
            return ResponseEntity.ok()
                    .body(ApiResponse.success("User statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get statistics: " + e.getMessage()));
        }
    }

    /**
     * Bulk operations - Xóa nhiều users
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<Void>> bulkDeleteUsers(@RequestBody List<Long> userIds) {
        try {
            adminUserService.bulkDeleteUsers(userIds);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Users deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete users: " + e.getMessage()));
        }
    }

    /**
     * Export users to CSV
     */
    @GetMapping("/export")
    public ResponseEntity<ApiResponse<String>> exportUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive) {
        try {
            String csvData = adminUserService.exportUsers(search, role, isActive);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Users exported successfully", csvData));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to export users: " + e.getMessage()));
        }
    }
}
