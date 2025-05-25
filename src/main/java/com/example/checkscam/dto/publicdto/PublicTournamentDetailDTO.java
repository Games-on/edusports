package com.example.checkscam.dto.publicdto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicTournamentDetailDTO {
    private Long id;
    private String name;
    private String sportType;
    private String description;
    private String location;
    private String startDate;
    private String endDate;
    private String registrationDeadline;
    private String status;
    private String rules;
    private String prizeInfo;
    private String contactInfo;
    private TournamentStatsDTO stats;
    private RegistrationInfoDTO registrationInfo;
    private List<TeamPreviewDTO> recentTeams;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentStatsDTO {
        private int maxTeams;
        private int currentTeams;
        private int availableSpots;
        private boolean isRegistrationOpen;
        private boolean isFull;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationInfoDTO {
        private boolean canRegister;
        private String registrationStatus;
        private List<String> requirements;
        private String registrationNote;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamPreviewDTO {
        private Long id;
        private String name;
        private String teamColor;
        private int memberCount;
        private String logoUrl;
        private String captainName;
    }
}
