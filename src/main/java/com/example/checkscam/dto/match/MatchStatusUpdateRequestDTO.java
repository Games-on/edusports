package com.example.checkscam.dto.match;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MatchStatusUpdateRequestDTO {
    @NotNull(message = "Status is required")
    private String status;
}
