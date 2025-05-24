package com.example.checkscam.dto.response;


import com.example.checkscam.constant.TournamentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentResponseDTO {
    private Long id;
    private String name;
    private String sportType;
    private String description;
    private int maxTeams;
    private int currentTeams;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime endDate;

    private String location;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime registrationDeadline;

    private TournamentStatus status;
    private TeamDTO winnerTeam;
    private TeamDTO runnerUpTeam;
    private String rules;
    private String prizeInfo;
    private String contactInfo;


    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdatedAt;

    private UserDTO createdBy;
    
    private List<TeamDTO> teams;

    @Data
    public static class TeamDTO {
        private Long id;
        private String name;
        private String teamColor;
        private int memberCount;
        private String status;
        private UserDTO captain;
    }

    @Data
    public static class UserDTO {
        private Long id;
        private String name;
    }
}