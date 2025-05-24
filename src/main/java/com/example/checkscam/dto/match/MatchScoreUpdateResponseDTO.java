package com.example.checkscam.dto.match;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreUpdateResponseDTO {
    private Long id;
    private int team1Score;
    private int team2Score;
    private WinnerTeamDTO winnerTeam;
    private String status;
    private boolean nextMatchGenerated;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinnerTeamDTO {
        private Long id;
        private String name;
    }
}
