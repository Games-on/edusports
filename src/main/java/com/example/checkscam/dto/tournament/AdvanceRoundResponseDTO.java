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
public class AdvanceRoundResponseDTO {
    private TournamentDTO tournament;
    private RoundAdvancementDTO advancement;
    private List<MatchDTO> newMatches;
    private boolean tournamentCompleted;
    private WinnerDTO champion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDTO {
        private Long id;
        private String name;
        private String status;
        private int currentRound;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundAdvancementDTO {
        private int fromRound;
        private int toRound;
        private int winnersCount;
        private boolean isShuffled;
        private String advancedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchDTO {
        private Long id;
        private TeamDTO team1;
        private TeamDTO team2;
        private String roundName;
        private Integer matchNumber;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TeamDTO {
            private Long id;
            private String name;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WinnerDTO {
        private Long id;
        private String name;
        private String logoUrl;
    }
}
