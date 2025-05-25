package com.example.checkscam.service;

import com.example.checkscam.dto.request.UpdateUserRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    
    /**
     * Lấy thông tin profile của user hiện tại
     */
    Object getCurrentUserProfile(String email);
    
    /**
     * Cập nhật thông tin profile
     */
    Object updateProfile(String email, UpdateUserRequest request);
    
    /**
     * Đổi mật khẩu
     */
    void changePassword(String email, String currentPassword, String newPassword);
    
    /**
     * Upload avatar
     */
    String uploadAvatar(String email, MultipartFile file);
    
    /**
     * Xóa avatar
     */
    void deleteAvatar(String email);
    
    /**
     * Deactivate account
     */
    void deactivateAccount(String email);
}
