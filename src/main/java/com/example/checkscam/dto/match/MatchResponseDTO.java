package com.example.checkscam.dto.match;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponseDTO {
    private Long id;
    private Long tournamentId;
    private int roundNumber;
    private String roundName;
    private TeamBasicDTO team1;
    private TeamBasicDTO team2;
    private String matchDate;
    private String location;
    private String status;
    private int team1Score;
    private int team2Score;
    private TeamBasicDTO winnerTeam;
    private Integer matchNumber;
    private String referee;
    private String notes;
    private String createdAt;
    
    // For detailed match view
    private TournamentDTO tournament;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamBasicDTO {
        private Long id;
        private String name;
        private String logoUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDetailDTO {
        private Long id;
        private String name;
        private String logoUrl;
        private CaptainDTO captain;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CaptainDTO {
            private Long id;
            private String name;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDTO {
        private Long id;
        private String name;
        private String sportType;
    }
}
