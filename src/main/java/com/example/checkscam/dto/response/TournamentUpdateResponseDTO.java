package com.example.checkscam.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TournamentUpdateResponseDTO {
    private Long id;
    private String name;
    private int maxTeams;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdatedAt;
}
