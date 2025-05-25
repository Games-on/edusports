package com.example.checkscam.rest;

import com.example.checkscam.dto.request.UpdateUserRequest;
import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.service.UserProfileService;
import com.example.checkscam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/user/profile")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * Lấy thông tin profile của user hiện tại
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getProfile() {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Object profile = userProfileService.getCurrentUserProfile(email);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Profile retrieved successfully", profile));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get profile: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật thông tin profile
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Object>> updateProfile(
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            Object updatedProfile = userProfileService.updateProfile(email, request);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Profile updated successfully", updatedProfile));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    /**
     * Đổi mật khẩu
     */
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            userProfileService.changePassword(email, currentPassword, newPassword);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Password changed successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    /**
     * Upload avatar
     */
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            String avatarUrl = userProfileService.uploadAvatar(email, file);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Avatar uploaded successfully", avatarUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to upload avatar: " + e.getMessage()));
        }
    }

    /**
     * Xóa avatar
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<ApiResponse<Void>> deleteAvatar() {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            userProfileService.deleteAvatar(email);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Avatar deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to delete avatar: " + e.getMessage()));
        }
    }

    /**
     * Deactivate account (user tự vô hiệu hóa tài khoản)
     */
    @PatchMapping("/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount() {
        try {
            String email = SecurityUtil.getCurrentUserLogin()
                    .orElseThrow(() -> new RuntimeException("User not authenticated"));
            
            userProfileService.deactivateAccount(email);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Account deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to deactivate account: " + e.getMessage()));
        }
    }
}
