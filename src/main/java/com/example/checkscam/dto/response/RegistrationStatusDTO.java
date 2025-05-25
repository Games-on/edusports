package com.example.checkscam.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStatusDTO {
    private boolean canRegister;
    private String message;
    private boolean isLoggedIn;
    private boolean hasExistingTeam;
    private boolean isRegistrationOpen;
    private boolean isTournamentFull;
    private String tournamentStatus;
    private int availableSpots;
    private ExistingTeamDTO existingTeam;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExistingTeamDTO {
        private Long id;
        private String name;
        private String status;
    }
}
