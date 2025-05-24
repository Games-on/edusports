package com.example.checkscam.dto.team;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class TeamRegistrationRequestDTO {
    @NotBlank(message = "Team name is required")
    @Size(max = 100, message = "Team name must not exceed 100 characters")
    private String teamName;

    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "Team color must be a valid hex color code")
    private String teamColor;

    @Min(value = 1, message = "Member count must be at least 1")
    @Max(value = 50, message = "Member count must not exceed 50")
    private int memberCount;

    @Size(max = 500, message = "Contact info must not exceed 500 characters")
    private String contactInfo;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
