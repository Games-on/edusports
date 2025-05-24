package com.example.checkscam.dto.response;

import com.example.checkscam.constant.TournamentStatus;
import lombok.Data;

@Data
public class TournamentStartResponseDTO {
    private Long id;
    private TournamentStatus status;
    private int matchesGenerated;
}
