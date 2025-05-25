package com.example.checkscam.service.impl;

import com.example.checkscam.dto.request.UpdateUserRequest;
import com.example.checkscam.entity.User;
import com.example.checkscam.exception.DataNotFoundException;
import com.example.checkscam.repository.UserRepository;
import com.example.checkscam.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5MB}")
    private String maxFileSize;

    @Override
    @Transactional(readOnly = true)
    public Object getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("avatar", user.getAvatar());
        profile.put("isActive", user.isActive());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("lastLogin", user.getLastLogin());
        
        // Roles
        profile.put("roles", user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toSet()));

        return profile;
    }

    @Override
    public Object updateProfile(String email, UpdateUserRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Kiểm tra email mới nếu có thay đổi
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            boolean emailExists = userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId());
            if (emailExists) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        // Cập nhật thông tin
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        User savedUser = userRepository.save(user);
        return getCurrentUserProfile(savedUser.getEmail());
    }

    @Override
    public void changePassword(String email, String currentPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public String uploadAvatar(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Please select a file to upload");
        }

        // Check file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 5MB");
        }

        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        try {
            // Create upload directory if not exists
            Path uploadPath = Paths.get(uploadDir, "avatars");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : ".jpg";
            String filename = "avatar_" + user.getId() + "_" + UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Delete old avatar if exists
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                try {
                    Path oldAvatarPath = Paths.get(uploadDir, "avatars", user.getAvatar());
                    Files.deleteIfExists(oldAvatarPath);
                } catch (Exception e) {
                    // Log warning but don't fail the upload
                    System.out.println("Warning: Could not delete old avatar: " + e.getMessage());
                }
            }

            // Update user avatar
            user.setAvatar(filename);
            userRepository.save(user);

            return "/uploads/avatars/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
        }
    }

    @Override
    public void deleteAvatar(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            try {
                // Delete file
                Path avatarPath = Paths.get(uploadDir, "avatars", user.getAvatar());
                Files.deleteIfExists(avatarPath);
            } catch (Exception e) {
                // Log warning but continue
                System.out.println("Warning: Could not delete avatar file: " + e.getMessage());
            }

            // Update user
            user.setAvatar(null);
            userRepository.save(user);
        }
    }

    @Override
    public void deactivateAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        user.setActive(false);
        user.setRefreshToken(null); // Clear refresh token
        userRepository.save(user);
    }
}
