package com.example.checkscam.dto.tournament;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentCompletionResponseDTO {
    private TournamentDTO tournament;
    private ChampionDTO champion;
    private RunnerUpDTO runnerUp;
    private StatsDTO stats;
    private String completedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDTO {
        private Long id;
        private String name;
        private String status;
        private int totalRounds;
        private int totalMatches;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChampionDTO {
        private Long id;
        private String name;
        private String logoUrl;
        private CaptainDTO captain;
        private int totalWins;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RunnerUpDTO {
        private Long id;
        private String name;
        private String logoUrl;
        private int totalWins;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsDTO {
        private int totalTeams;
        private int completedMatches;
        private String tournamentDuration;
    }
}
