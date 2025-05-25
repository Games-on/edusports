package com.example.checkscam.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private boolean isActive;
    private Instant createdAt;
    private Instant lastLogin;
    private Set<String> roles;
    private String phone;
    private String avatar;
    
    // Constructor without optional fields
    public AdminUserResponseDTO(Long id, String name, String email, boolean isActive, Instant createdAt, Set<String> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.roles = roles;
    }
}
