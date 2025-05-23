package com.example.checkscam.service;


import com.example.checkscam.dto.request.TournamentRequestDTO;
import com.example.checkscam.dto.response.PaginatedResponseDTO;
import com.example.checkscam.dto.response.TournamentResponseDTO;

public interface TournamentService {
    PaginatedResponseDTO<TournamentResponseDTO> getAllTournaments(TournamentRequestDTO request);
}
