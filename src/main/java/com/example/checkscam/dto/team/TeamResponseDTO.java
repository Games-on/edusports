package com.example.checkscam.dto.team;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponseDTO {
    private Long id;
    private String name;
    private String teamColor;
    private int memberCount;
    private String status;
    private String logoUrl;
    private String contactInfo;
    private CaptainDTO captain;
    private String createdAt;
    
    // For detailed team view
    private TournamentDTO tournament;
    private List<MatchDTO> matches;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CaptainDTO {
        private Long id;
        private String name;
        private String email;
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchDTO {
        private Long id;
        private String roundName;
        private OpponentDTO opponent;
        private String matchDate;
        private String status;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OpponentDTO {
            private Long id;
            private String name;
        }
    }
}
