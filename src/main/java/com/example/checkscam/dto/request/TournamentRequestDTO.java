package com.example.checkscam.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class TournamentRequestDTO {
    // For pagination (GET requests)
    private Integer page = 1;
    private Integer limit = 10;
    private String status;
    private String sportType;
    private String search;
    
    // For create/update operations
    @NotBlank(message = "Tournament name is required")
    private String name;
    
    private String description;
    
    @Min(value = 2, message = "Max teams must be at least 2")
    private Integer maxTeams;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime endDate;
    
    private String location;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime registrationDeadline;
    
    private String rules;
    
    private String prizeInfo;
    
    private String contactInfo;
}
