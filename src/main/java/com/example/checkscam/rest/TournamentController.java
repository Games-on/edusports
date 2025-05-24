package com.example.checkscam.rest;

import com.example.checkscam.dto.request.TournamentRequestDTO;
import com.example.checkscam.dto.response.PaginatedResponseDTO;
import com.example.checkscam.dto.response.TournamentResponseDTO;
import com.example.checkscam.dto.response.TournamentCreateResponseDTO;
import com.example.checkscam.dto.response.TournamentUpdateResponseDTO;
import com.example.checkscam.dto.response.TournamentStartResponseDTO;
import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.response.ResponseObject;
import com.example.checkscam.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService tournamentService;

    @GetMapping
    public PaginatedResponseDTO<TournamentResponseDTO> getAllTournaments(TournamentRequestDTO request) {
        return tournamentService.getAllTournaments(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TournamentResponseDTO>> getTournamentById(@PathVariable Long id) {
        try {
            TournamentResponseDTO tournament = tournamentService.getTournamentById(id);
            return ResponseEntity.ok(ApiResponse.success(null, tournament));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Tournament not found"));
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<TournamentCreateResponseDTO>> createTournament(@Valid @RequestBody TournamentRequestDTO request) {
        try {
            TournamentCreateResponseDTO tournament = tournamentService.createTournament(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tournament created successfully", tournament));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create tournament: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<TournamentUpdateResponseDTO>> updateTournament(
            @PathVariable Long id,
            @Valid @RequestBody TournamentRequestDTO request) {
        try {
            TournamentUpdateResponseDTO tournament = tournamentService.updateTournament(id, request);
            return ResponseEntity.ok(ApiResponse.success("Tournament updated successfully", tournament));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update tournament: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteTournament(@PathVariable Long id) {
        try {
            tournamentService.deleteTournament(id);
            return ResponseEntity.ok(ApiResponse.success("Tournament deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete tournament: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<TournamentStartResponseDTO>> startTournament(@PathVariable Long id) {
        try {
            TournamentStartResponseDTO result = tournamentService.startTournament(id);
            return ResponseEntity.ok(ApiResponse.success("Tournament started successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to start tournament: " + e.getMessage()));
        }
    }
}