package com.example.checkscam.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentPublicDTO {
    private Long id;
    private String name;
    private String sportType;
    private String description;
    private int maxTeams;
    private int currentTeams;
    private int availableSpots;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private LocalDateTime registrationDeadline;
    private String status;
    private String rules;
    private String prizeInfo;
    private String contactInfo;
    private OrganizerDTO organizer;
    private RegistrationInfoDTO registrationInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganizerDTO {
        private String name;
        private String contactInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistrationInfoDTO {
        private boolean canRegister;
        private String registrationMessage;
        private boolean isRegistrationOpen;
        private boolean isFull;
        private long daysUntilDeadline;
        private long hoursUntilDeadline;
    }
}
