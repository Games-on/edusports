package com.example.checkscam.dto.request;


import lombok.Data;

@Data
public class TournamentRequestDTO {
    private Integer page = 1;
    private Integer limit = 10;
    private String status;
    private String sportType;
    private String search;
}
