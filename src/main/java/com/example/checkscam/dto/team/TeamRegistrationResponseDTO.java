package com.example.checkscam.dto.team;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRegistrationResponseDTO {
    private RegistrationDTO registration;
    private TeamDTO team;
    private TournamentDTO tournament;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationDTO {
        private Long id;
        private Long tournamentId;
        private Long userId;
        private String status;
        private String registrationDate;
        private String notes;
        private String createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDTO {
        private Long id;
        private String name;
        private Long tournamentId;
        private String teamColor;
        private int memberCount;
        private Long captainUserId;
        private String contactInfo;
        private String logoUrl;
        private String status;
        private String createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentDTO {
        private Long id;
        private String name;
        private String sportType;
        private int currentTeams;
        private int maxTeams;
        private int availableSpots;
    }
}
