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
public class TournamentBracketResponseDTO {
    private TournamentDTO tournament;
    private BracketDTO bracket;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDTO {
        private Long id;
        private String name;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BracketDTO {
        private int totalRounds;
        private int currentRound;
        private List<RoundDTO> rounds;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RoundDTO {
            private int roundNumber;
            private String roundName;
            private List<MatchBracketDTO> matches;

            @Data
            @Builder
            @NoArgsConstructor
            @AllArgsConstructor
            public static class MatchBracketDTO {
                private Long id;
                private TeamBracketDTO team1;
                private TeamBracketDTO team2;
                private int team1Score;
                private int team2Score;
                private Long winnerTeamId;
                private String status;

                @Data
                @Builder
                @NoArgsConstructor
                @AllArgsConstructor
                public static class TeamBracketDTO {
                    private Long id;
                    private String name;
                }
            }
        }
    }
}
