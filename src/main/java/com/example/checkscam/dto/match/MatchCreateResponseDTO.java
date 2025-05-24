package com.example.checkscam.dto.match;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchCreateResponseDTO {
    private Long id;
    private TournamentDTO tournament;
    private int roundNumber;
    private String roundName;
    private TeamDTO team1;
    private TeamDTO team2;
    private String matchDate;
    private String location;
    private String status;
    private Integer matchNumber;
    private String referee;
    private String notes;
    private String createdAt;

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
    public static class TeamDTO {
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
            private String email;
        }
    }
}
