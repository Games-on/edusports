package com.example.checkscam.dto.response;

import com.example.checkscam.constant.TournamentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentCreateResponseDTO {
    private Long id;
    private String name;
    private String sportType;
    private TournamentStatus status;
    private int currentTeams;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdAt;
}
