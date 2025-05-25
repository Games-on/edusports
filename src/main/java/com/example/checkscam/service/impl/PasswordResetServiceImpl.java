package com.example.checkscam.service.impl;

import com.example.checkscam.entity.PasswordResetToken;
import com.example.checkscam.entity.User;
import com.example.checkscam.exception.DataNotFoundException;
import com.example.checkscam.repository.PasswordResetTokenRepository;
import com.example.checkscam.repository.UserRepository;
import com.example.checkscam.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found with email: " + email));

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        // Invalidate existing tokens for this user
        passwordResetTokenRepository.invalidateAllTokensForUser(user);

        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiresAt(Instant.now().plusSeconds(3600)); // 1 hour

        passwordResetTokenRepository.save(resetToken);

        // TODO: Send email with reset link
        // For now, just log the token (in production, you would send an email)
        System.out.println("Password reset token for " + email + ": " + token);
        System.out.println("Reset link: http://localhost:3000/reset-password?token=" + token);
    }

    @Override
    @Transactional(readOnly = true)
    public Object verifyResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token has expired");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valid", true);
        result.put("email", resetToken.getUser().getEmail());
        result.put("expiresAt", resetToken.getExpiresAt());

        return result;
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Reset token has expired");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setRefreshToken(null); // Clear refresh tokens to force re-login
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Invalidate all other tokens for this user
        passwordResetTokenRepository.invalidateAllTokensForUser(user);
    }
}
