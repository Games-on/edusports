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
public class TournamentMatchesResponseDTO {
    private List<MatchResponseDTO> matches;
    private TournamentBracketDTO tournamentBracket;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentBracketDTO {
        private List<RoundDTO> rounds;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RoundDTO {
            private int roundNumber;
            private String roundName;
            private List<Long> matches;
        }
    }
}
