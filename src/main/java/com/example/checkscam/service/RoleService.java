package com.example.checkscam.service;

public interface RoleService {
    
    /**
     * Lấy danh sách tất cả roles
     */
    Object getAllRoles();
    
    /**
     * Lấy thống kê role usage
     */
    Object getRoleStatistics();
    
    /**
     * Lấy users theo role
     */
    Object getUsersByRole(String roleName);
}
