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
public class BracketGenerationResponseDTO {
    private TournamentDTO tournament;
    private BracketInfoDTO bracketInfo;
    private List<RoundDTO> rounds;
    private String generatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDTO {
        private Long id;
        private String name;
        private String status;
        private int totalTeams;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BracketInfoDTO {
        private int totalRounds;
        private int totalMatches;
        private boolean isShuffled;
        private String bracketType;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundDTO {
        private int roundNumber;
        private String roundName;
        private int matchCount;
        private List<MatchDTO> matches;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MatchDTO {
            private Long id;
            private TeamDTO team1;
            private TeamDTO team2;
            private String matchDate;
            private String location;
            private Integer matchNumber;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class TeamDTO {
                private Long id;
                private String name;
                private String logoUrl;
            }
        }
    }
}
