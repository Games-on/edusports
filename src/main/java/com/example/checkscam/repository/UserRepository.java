package com.example.checkscam.repository;

import com.example.checkscam.constant.RoleName;
import com.example.checkscam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByEmail(String email);
    
    // Đếm số user active
    long countByIsActive(boolean isActive);
    
    // Đếm số user theo role
    long countByRoles_Name(RoleName roleName);
    
    // Đếm user được tạo sau một thời điểm
    long countByCreatedAtAfter(Instant createdAt);
    
    // Tìm user theo email và active status
    Optional<User> findByEmailAndIsActive(String email, boolean isActive);
    
    // Tìm user theo name (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Tìm user theo role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") RoleName roleName);
    
    // Tìm user active theo role
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.isActive = :isActive")
    List<User> findByRoleNameAndIsActive(@Param("roleName") RoleName roleName, @Param("isActive") boolean isActive);
    
    // Tìm kiếm user theo email hoặc name
    @Query("SELECT u FROM User u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> searchByEmailOrName(@Param("search") String search);
    
    // Lấy top users mới nhất
    List<User> findTop10ByOrderByCreatedAtDesc();
    
    // Kiểm tra email đã tồn tại (ngoại trừ user hiện tại)
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :userId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);
}
