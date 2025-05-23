package com.example.checkscam.dto.team;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateResponseDTO {
    private Long id;
    private String name;
    private String teamColor;
    private int memberCount;
    private String contactInfo;
    private String logoUrl;
    private String lastUpdatedAt;
}
