package com.example.checkscam.service;

import com.example.checkscam.dto.tournament.*;

public interface TournamentKnockoutService {
    
    /**
     * Generate knockout tournament bracket
     */
    BracketGenerationResponseDTO generateTournamentBracket(Long tournamentId, BracketGenerationRequestDTO request);
    
    /**
     * Advance tournament to next round
     */
    AdvanceRoundResponseDTO advanceToNextRound(Long tournamentId);
    
    /**
     * Complete tournament and determine champion
     */
    TournamentCompletionResponseDTO completeTournament(Long tournamentId);
    
    /**
     * Start tournament (change status from REGISTRATION to ONGOING)
     */
    void startTournament(Long tournamentId);
}
