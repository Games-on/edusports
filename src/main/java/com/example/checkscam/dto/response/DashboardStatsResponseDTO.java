package com.example.checkscam.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponseDTO {
    private Long activeTournaments;
    private Long registeredTeams;
    private Long matchesPlayed;
    private Double successRate;
    private Long totalUsers;
    private Long completedTournaments;
    
    // Additional stats for better insights
    private Long totalTournaments;
    private Long pendingMatches;
    private Long totalMatches;
}
