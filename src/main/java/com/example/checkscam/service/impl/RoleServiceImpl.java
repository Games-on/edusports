package com.example.checkscam.service.impl;

import com.example.checkscam.constant.RoleName;
import com.example.checkscam.entity.Role;
import com.example.checkscam.entity.User;
import com.example.checkscam.repository.RoleRepository;
import com.example.checkscam.repository.UserRepository;
import com.example.checkscam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public Object getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        
        return roles.stream().map(role -> {
            Map<String, Object> roleInfo = new HashMap<>();
            roleInfo.put("id", role.getId());
            roleInfo.put("name", role.getName().toString());
            roleInfo.put("userCount", userRepository.countByRoles_Name(role.getName()));
            return roleInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public Object getRoleStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Count users by role
        Map<String, Long> roleCounts = new HashMap<>();
        for (RoleName roleName : RoleName.values()) {
            long count = userRepository.countByRoles_Name(roleName);
            roleCounts.put(roleName.toString(), count);
        }
        
        stats.put("roleCounts", roleCounts);
        stats.put("totalRoles", RoleName.values().length);
        stats.put("totalUsers", userRepository.count());
        
        // Users without roles
        long totalUsersWithRoles = roleCounts.values().stream().mapToLong(Long::longValue).sum();
        long usersWithoutRoles = userRepository.count() - totalUsersWithRoles;
        stats.put("usersWithoutRoles", usersWithoutRoles);
        
        return stats;
    }

    @Override
    public Object getUsersByRole(String roleName) {
        try {
            RoleName roleEnum = RoleName.valueOf(roleName.toUpperCase());
            List<User> users = userRepository.findByRoleName(roleEnum);
            
            return users.stream().map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("name", user.getName());
                userInfo.put("email", user.getEmail());
                userInfo.put("isActive", user.isActive());
                userInfo.put("createdAt", user.getCreatedAt());
                return userInfo;
            }).collect(Collectors.toList());
            
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name: " + roleName);
        }
    }
}
