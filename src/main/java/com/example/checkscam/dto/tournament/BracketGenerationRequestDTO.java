package com.example.checkscam.dto.tournament;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BracketGenerationRequestDTO {
    @NotNull(message = "Shuffle teams is required")
    private Boolean shuffleTeams = true;
    
    private String notes;
}
