package com.example.checkscam.rest;

import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * Gửi email reset password
     */
    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.sendPasswordResetEmail(email);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Password reset email sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to send reset email: " + e.getMessage()));
        }
    }

    /**
     * Verify reset token
     */
    @GetMapping("/reset/verify")
    public ResponseEntity<ApiResponse<Object>> verifyResetToken(@RequestParam String token) {
        try {
            Object result = passwordResetService.verifyResetToken(token);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Token verified successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid or expired token: " + e.getMessage()));
        }
    }

    /**
     * Reset password với token
     */
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Password reset successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to reset password: " + e.getMessage()));
        }
    }
}
