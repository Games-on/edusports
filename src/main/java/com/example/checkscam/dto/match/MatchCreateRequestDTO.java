package com.example.checkscam.dto.match;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.Future;
import java.time.LocalDateTime;

@Data
public class MatchCreateRequestDTO {
    @NotNull(message = "Team 1 ID is required")
    private Long team1Id;

    @NotNull(message = "Team 2 ID is required")
    private Long team2Id;

    @NotNull(message = "Round number is required")
    @Min(value = 1, message = "Round number must be at least 1")
    private Integer roundNumber;

    @Size(max = 100, message = "Round name must not exceed 100 characters")
    private String roundName;

    @NotNull(message = "Match date is required")
    @Future(message = "Match date must be in the future")
    private LocalDateTime matchDate;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Min(value = 1, message = "Match number must be at least 1")
    private Integer matchNumber;

    @Size(max = 100, message = "Referee name must not exceed 100 characters")
    private String referee;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
