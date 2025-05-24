package com.example.checkscam.rest;

import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.dto.match.*;
import com.example.checkscam.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * 4.1 Get Matches by Tournament
     * GET /api/tournaments/{tournament_id}/matches
     */
    @GetMapping("/api/tournaments/{tournament_id}/matches")
    public ResponseEntity<ApiResponse<TournamentMatchesResponseDTO>> getMatchesByTournament(
            @PathVariable("tournament_id") Long tournamentId,
            @RequestParam(value = "round", required = false) Integer round,
            @RequestParam(value = "status", required = false) String status) {
        try {
            TournamentMatchesResponseDTO response = matchService.getMatchesByTournament(tournamentId, round, status);
            return ResponseEntity.ok(ApiResponse.success(null, response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 4.2 Get Match by ID
     * GET /api/matches/{id}
     */
    @GetMapping("/api/matches/{id}")
    public ResponseEntity<ApiResponse<MatchResponseDTO>> getMatchById(@PathVariable Long id) {
        try {
            MatchResponseDTO match = matchService.getMatchById(id);
            return ResponseEntity.ok(ApiResponse.success(null, match));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 4.2.1 Create Match
     * POST /api/tournaments/{tournament_id}/matches
     */
    @PostMapping("/api/tournaments/{tournament_id}/matches")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MatchCreateResponseDTO>> createMatch(
            @PathVariable("tournament_id") Long tournamentId,
            @Valid @RequestBody MatchCreateRequestDTO request) {
        try {
            MatchCreateResponseDTO response = matchService.createMatch(tournamentId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Match created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 4.3 Update Match Score
     * PUT /api/matches/{id}/score
     */
    @PutMapping("/api/matches/{id}/score")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MatchScoreUpdateResponseDTO>> updateMatchScore(
            @PathVariable Long id,
            @Valid @RequestBody MatchScoreUpdateRequestDTO request) {
        try {
            MatchScoreUpdateResponseDTO response = matchService.updateMatchScore(id, request);
            return ResponseEntity.ok(ApiResponse.success("Match score updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 4.4 Update Match Status
     * PUT /api/matches/{id}/status
     */
    @PutMapping("/api/matches/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<MatchStatusUpdateResponseDTO>> updateMatchStatus(
            @PathVariable Long id,
            @Valid @RequestBody MatchStatusUpdateRequestDTO request) {
        try {
            MatchStatusUpdateResponseDTO response = matchService.updateMatchStatus(id, request);
            return ResponseEntity.ok(ApiResponse.success("Match status updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 4.5 Get Tournament Bracket
     * GET /api/tournaments/{tournament_id}/bracket
     */
    @GetMapping("/api/tournaments/{tournament_id}/bracket")
    public ResponseEntity<ApiResponse<TournamentBracketResponseDTO>> getTournamentBracket(
            @PathVariable("tournament_id") Long tournamentId) {
        try {
            TournamentBracketResponseDTO response = matchService.getTournamentBracket(tournamentId);
            return ResponseEntity.ok(ApiResponse.success(null, response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
