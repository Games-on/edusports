package com.example.checkscam.service.impl;

import com.example.checkscam.constant.RoleName;
import com.example.checkscam.dto.admin.AdminUserCreateRequestDTO;
import com.example.checkscam.dto.admin.AdminUserResponseDTO;
import com.example.checkscam.dto.response.PagedResponseDTO;
import com.example.checkscam.entity.Role;
import com.example.checkscam.entity.User;
import com.example.checkscam.exception.DataNotFoundException;
import com.example.checkscam.repository.RoleRepository;
import com.example.checkscam.repository.UserRepository;
import com.example.checkscam.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminUserResponseDTO createUser(AdminUserCreateRequestDTO request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(request.isActive());
        user.setCreatedAt(Instant.now());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());

        // Gán roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                try {
                    RoleName roleEnum = RoleName.valueOf(roleName.toUpperCase());
                    Role role = roleRepository.findByName(roleEnum);
                    if (role != null) {
                        roles.add(role);
                    }
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + roleName);
                }
            }
        } else {
            // Default role là STUDENT
            Role studentRole = roleRepository.findByName(RoleName.STUDENT);
            if (studentRole != null) {
                roles.add(studentRole);
            }
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return convertToAdminResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDTO<AdminUserResponseDTO> getAllUsers(
            Pageable pageable, String search, String role, Boolean isActive) {

        Specification<User> spec = createUserSpecification(search, role, isActive);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        List<AdminUserResponseDTO> userDTOs = userPage.getContent().stream()
                .map(this::convertToAdminResponseDTO)
                .collect(Collectors.toList());

        return new PagedResponseDTO<>(
                userDTOs,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isFirst(),
                userPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
        return convertToAdminResponseDTO(user);
    }

    @Override
    public AdminUserResponseDTO updateUser(Long id, AdminUserCreateRequestDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));

        // Kiểm tra email trùng (ngoại trừ user hiện tại)
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new RuntimeException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        
        // Chỉ update password nếu có password mới
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user.setActive(request.isActive());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());

        // Update roles
        if (request.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                try {
                    RoleName roleEnum = RoleName.valueOf(roleName.toUpperCase());
                    Role role = roleRepository.findByName(roleEnum);
                    if (role != null) {
                        roles.add(role);
                    }
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + roleName);
                }
            }
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        return convertToAdminResponseDTO(savedUser);
    }

//    @Override
//    public void deleteUser(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
//
//        // Soft delete
//        user.setActive(false);
//        userRepository.save(user);
//    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public AdminUserResponseDTO toggleUserStatus(Long id, boolean isActive) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
        
        user.setActive(isActive);
        User savedUser = userRepository.save(user);
        return convertToAdminResponseDTO(savedUser);
    }

    @Override
    public AdminUserResponseDTO assignRoles(Long id, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            try {
                RoleName roleEnum = RoleName.valueOf(roleName.toUpperCase());
                Role role = roleRepository.findByName(roleEnum);
                if (role != null) {
                    roles.add(role);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + roleName);
            }
        }
        
        user.setRoles(roles);
        User savedUser = userRepository.save(user);
        return convertToAdminResponseDTO(savedUser);
    }

    @Override
    public void resetUserPassword(Long id, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + id));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserStatistics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long inactiveUsers = totalUsers - activeUsers;
        
        // Thống kê theo role
        Map<String, Long> roleStats = new HashMap<>();
        for (RoleName roleName : RoleName.values()) {
            long count = userRepository.countByRoles_Name(roleName);
            roleStats.put(roleName.name(), count);
        }

        // Thống kê người dùng mới trong 30 ngày
        Instant thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60);
        long newUsersLast30Days = userRepository.countByCreatedAtAfter(thirtyDaysAgo);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("roleStatistics", roleStats);
        stats.put("newUsersLast30Days", newUsersLast30Days);
        
        return stats;
    }

    @Override
    public void bulkDeleteUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            user.setActive(false);
        }
        userRepository.saveAll(users);
    }

    @Override
    @Transactional(readOnly = true)
    public String exportUsers(String search, String role, Boolean isActive) {
        Specification<User> spec = createUserSpecification(search, role, isActive);
        List<User> users = userRepository.findAll(spec);
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Email,Status,Roles,Created At\n");
        
        for (User user : users) {
            String roles = user.getRoles().stream()
                    .map(r -> r.getName().toString())
                    .collect(Collectors.joining(";"));
            
            csv.append(String.format("%d,%s,%s,%s,%s,%s\n",
                    user.getId(),
                    escapeCsv(user.getName()),
                    escapeCsv(user.getEmail()),
                    user.isActive() ? "Active" : "Inactive",
                    escapeCsv(roles),
                    user.getCreatedAt().toString()
            ));
        }
        
        return csv.toString();
    }

    /**
     * Tạo Specification để filter users
     */
    private Specification<User> createUserSpecification(String search, String role, Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm kiếm theo name hoặc email
            if (search != null && !search.isEmpty()) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")), searchPattern);
                Predicate emailPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")), searchPattern);
                predicates.add(criteriaBuilder.or(namePredicate, emailPredicate));
            }

            // Filter theo role
            if (role != null && !role.isEmpty()) {
                try {
                    RoleName roleEnum = RoleName.valueOf(role.toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.join("roles").get("name"), roleEnum));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid role
                }
            }

            // Filter theo status
            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Chuyển đổi User entity thành AdminUserResponseDTO
     */
    private AdminUserResponseDTO convertToAdminResponseDTO(User user) {
        AdminUserResponseDTO dto = new AdminUserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setActive(user.isActive());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        
        // Convert roles to string set
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);
        
        return dto;
    }

    /**
     * Escape CSV values to handle commas and quotes
     */
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
