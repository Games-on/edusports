package com.example.checkscam.rest;

import com.example.checkscam.dto.response.DashboardStatsResponseDTO;
import com.example.checkscam.response.ResponseObject;
import com.example.checkscam.service.DashboardStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardStatsService dashboardStatsService;

    @GetMapping("/stats")
    public ResponseEntity<ResponseObject> getDashboardStats() {
        log.info("📊 [DashboardController] Getting dashboard statistics");
        
        try {
            DashboardStatsResponseDTO stats = dashboardStatsService.getDashboardStats();
            
            log.info("✅ [DashboardController] Dashboard statistics retrieved successfully");
            
            return ResponseEntity.ok(
                ResponseObject.builder()
                    .status(HttpStatus.OK)
                    .message("Dashboard statistics retrieved successfully")
                    .data(stats)
                    .build()
            );
            
        } catch (Exception e) {
            log.error("❌ [DashboardController] Error getting dashboard statistics: {}", e.getMessage(), e);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseObject.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message("Failed to retrieve dashboard statistics: " + e.getMessage())
                    .data(null)
                    .build()
                );
        }
    }
}
