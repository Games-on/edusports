package com.example.checkscam.rest;

import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    /**
     * Lấy danh sách tất cả roles
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllRoles() {
        try {
            Object roles = roleService.getAllRoles();
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Roles retrieved successfully", roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve roles: " + e.getMessage()));
        }
    }

    /**
     * Lấy thống kê role usage
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Object>> getRoleStatistics() {
        try {
            Object stats = roleService.getRoleStatistics();
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Role statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get role statistics: " + e.getMessage()));
        }
    }

    /**
     * Lấy users theo role
     */
    @GetMapping("/{roleName}/users")
    public ResponseEntity<ApiResponse<Object>> getUsersByRole(@PathVariable String roleName) {
        try {
            Object users = roleService.getUsersByRole(roleName);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }
}
