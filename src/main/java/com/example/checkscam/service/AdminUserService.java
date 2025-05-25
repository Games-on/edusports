package com.example.checkscam.service;

import com.example.checkscam.dto.admin.AdminUserCreateRequestDTO;
import com.example.checkscam.dto.admin.AdminUserResponseDTO;
import com.example.checkscam.dto.response.PagedResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AdminUserService {
    
    /**
     * Tạo user mới bởi admin
     */
    AdminUserResponseDTO createUser(AdminUserCreateRequestDTO request);
    
    /**
     * Lấy danh sách users với phân trang và filter
     */
    PagedResponseDTO<AdminUserResponseDTO> getAllUsers(
            Pageable pageable, String search, String role, Boolean isActive);
    
    /**
     * Lấy user theo ID
     */
    AdminUserResponseDTO getUserById(Long id);
    
    /**
     * Cập nhật user
     */
    AdminUserResponseDTO updateUser(Long id, AdminUserCreateRequestDTO request);
    
    /**
     * Xóa user (soft delete)
     */
    void deleteUser(Long id);
    
    /**
     * Kích hoạt/vô hiệu hóa user
     */
    AdminUserResponseDTO toggleUserStatus(Long id, boolean isActive);
    
    /**
     * Gán roles cho user
     */
    AdminUserResponseDTO assignRoles(Long id, Set<String> roles);
    
    /**
     * Reset mật khẩu user
     */
    void resetUserPassword(Long id, String newPassword);
    
    /**
     * Lấy thống kê users
     */
    Object getUserStatistics();
    
    /**
     * Xóa nhiều users
     */
    void bulkDeleteUsers(List<Long> userIds);
    
    /**
     * Export users to CSV
     */
    String exportUsers(String search, String role, Boolean isActive);
}
