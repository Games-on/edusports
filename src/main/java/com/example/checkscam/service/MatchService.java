package com.example.checkscam.service;

import com.example.checkscam.dto.match.*;

public interface MatchService {
    
    /**
     * Get matches by tournament with optional filters
     */
    TournamentMatchesResponseDTO getMatchesByTournament(Long tournamentId, Integer round, String status);
    
    /**
     * Get match by ID with detailed information
     */
    MatchResponseDTO getMatchById(Long matchId);
    
    /**
     * Create a new match
     */
    MatchCreateResponseDTO createMatch(Long tournamentId, MatchCreateRequestDTO request);
    
    /**
     * Update match score
     */
    MatchScoreUpdateResponseDTO updateMatchScore(Long matchId, MatchScoreUpdateRequestDTO request);
    
    /**
     * Update match status
     */
    MatchStatusUpdateResponseDTO updateMatchStatus(Long matchId, MatchStatusUpdateRequestDTO request);
    
    /**
     * Get tournament bracket
     */
    TournamentBracketResponseDTO getTournamentBracket(Long tournamentId);
}
