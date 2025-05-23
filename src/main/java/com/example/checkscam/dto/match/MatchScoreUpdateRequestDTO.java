package com.example.checkscam.dto.match;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class MatchScoreUpdateRequestDTO {
    @NotNull(message = "Team 1 score is required")
    @Min(value = 0, message = "Team 1 score must be non-negative")
    private Integer team1Score;

    @NotNull(message = "Team 2 score is required")
    @Min(value = 0, message = "Team 2 score must be non-negative")
    private Integer team2Score;

    @NotNull(message = "Status is required")
    private String status;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
