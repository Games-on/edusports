package com.example.checkscam.dto.match;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchStatusUpdateResponseDTO {
    private Long id;
    private String status;
    private String lastUpdatedAt;
}
