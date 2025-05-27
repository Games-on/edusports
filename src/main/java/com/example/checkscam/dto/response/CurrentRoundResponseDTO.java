package com.example.checkscam.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CurrentRoundResponseDTO {
    private Long tournamentId;
    private String tournamentName;
    private int currentRound;
    private String currentRoundName;
    private int totalRounds;
    private int completedRounds;
    private boolean isRoundComplete;
    private boolean canAdvanceToNextRound;
    private int currentRoundMatches;
    private int completedCurrentRoundMatches;
    private String status;
}
