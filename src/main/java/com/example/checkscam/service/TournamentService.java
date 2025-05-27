package com.example.checkscam.service;

import com.example.checkscam.dto.request.TournamentRequestDTO;
import com.example.checkscam.dto.response.PaginatedResponseDTO;
import com.example.checkscam.dto.response.TournamentResponseDTO;
import com.example.checkscam.dto.response.TournamentCreateResponseDTO;
import com.example.checkscam.dto.response.TournamentUpdateResponseDTO;
import com.example.checkscam.dto.response.TournamentStartResponseDTO;
import com.example.checkscam.dto.response.CurrentRoundResponseDTO;

public interface TournamentService {
    PaginatedResponseDTO<TournamentResponseDTO> getAllTournaments(TournamentRequestDTO request);
    
    TournamentResponseDTO getTournamentById(Long id);
    
    TournamentCreateResponseDTO createTournament(TournamentRequestDTO request);
    
    TournamentUpdateResponseDTO updateTournament(Long id, TournamentRequestDTO request);
    
    void deleteTournament(Long id);
    
    TournamentStartResponseDTO startTournament(Long id);
    
    CurrentRoundResponseDTO getCurrentRound(Long tournamentId);
}
