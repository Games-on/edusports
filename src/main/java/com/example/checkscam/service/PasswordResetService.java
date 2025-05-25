package com.example.checkscam.service;

public interface PasswordResetService {
    
    /**
     * Gửi email reset password
     */
    void sendPasswordResetEmail(String email);
    
    /**
     * Verify reset token
     */
    Object verifyResetToken(String token);
    
    /**
     * Reset password với token
     */
    void resetPassword(String token, String newPassword);
}
