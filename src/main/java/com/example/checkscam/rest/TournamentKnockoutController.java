package com.example.checkscam.rest;

import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.dto.tournament.*;
import com.example.checkscam.service.TournamentKnockoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TournamentKnockoutController {

    private final TournamentKnockoutService tournamentKnockoutService;

    /**
     * Generate Tournament Bracket
     * POST /api/tournaments/{tournament_id}/generate-bracket
     */
    @PostMapping("/api/tournaments/{tournament_id}/generate-bracket")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ORGANIZER') or hasAuthority('ADMIN') or hasAuthority('ORGANIZER')")
    public ResponseEntity<ApiResponse<BracketGenerationResponseDTO>> generateTournamentBracket(
            @PathVariable("tournament_id") Long tournamentId,
            @Valid @RequestBody BracketGenerationRequestDTO request) {
        try {
            BracketGenerationResponseDTO response = tournamentKnockoutService.generateTournamentBracket(tournamentId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tournament bracket generated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Start Tournament
     * POST /api/tournaments/{tournament_id}/start
     */
    @PostMapping("/api/tournaments/{tournament_id}/start-knockout")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<String>> startTournament(
            @PathVariable("tournament_id") Long tournamentId) {
        try {
            tournamentKnockoutService.startTournament(tournamentId);
            return ResponseEntity.ok(ApiResponse.success("Tournament started successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Advance Tournament Round
     * POST /api/tournaments/{tournament_id}/advance-round
     */
    @PostMapping("/api/tournaments/{tournament_id}/advance-round")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<AdvanceRoundResponseDTO>> advanceToNextRound(
            @PathVariable("tournament_id") Long tournamentId) {
        try {
            AdvanceRoundResponseDTO response = tournamentKnockoutService.advanceToNextRound(tournamentId);
            return ResponseEntity.ok(ApiResponse.success("Advanced to next round successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Complete Tournament
     * POST /api/tournaments/{tournament_id}/complete
     */
    @PostMapping("/api/tournaments/{tournament_id}/complete")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<TournamentCompletionResponseDTO>> completeTournament(
            @PathVariable("tournament_id") Long tournamentId) {
        try {
            TournamentCompletionResponseDTO response = tournamentKnockoutService.completeTournament(tournamentId);
            return ResponseEntity.ok(ApiResponse.success("Tournament completed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
