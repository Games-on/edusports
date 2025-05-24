package com.example.checkscam.rest;

import com.example.checkscam.dto.response.ApiResponse;
import com.example.checkscam.dto.team.*;
import com.example.checkscam.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * 3.1 Get Teams by Tournament
     * GET /api/tournaments/{tournament_id}/teams
     */
    @GetMapping("/api/tournaments/{tournament_id}/teams")
    public ResponseEntity<ApiResponse<List<TeamResponseDTO>>> getTeamsByTournament(
            @PathVariable("tournament_id") Long tournamentId) {
        try {
            List<TeamResponseDTO> teams = teamService.getTeamsByTournament(tournamentId);
            return ResponseEntity.ok(ApiResponse.success(null, teams));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 3.2 Get Team by ID
     * GET /api/teams/{id}
     */
    @GetMapping("/api/teams/{id}")
    public ResponseEntity<ApiResponse<TeamResponseDTO>> getTeamById(@PathVariable Long id) {
        try {
            TeamResponseDTO team = teamService.getTeamById(id);
            return ResponseEntity.ok(ApiResponse.success(null, team));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 3.3 Create Team (Register for Tournament)
     * POST /api/tournaments/{tournament_id}/register
     */
    @PostMapping("/api/tournaments/{tournament_id}/register")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<TeamRegistrationResponseDTO>> registerTeamForTournament(
            @PathVariable("tournament_id") Long tournamentId,
            @Valid @RequestBody TeamRegistrationRequestDTO request) {
        try {
            TeamRegistrationResponseDTO response = teamService.registerTeamForTournament(tournamentId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Team registered for tournament successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 3.4 Update Team
     * PUT /api/teams/{id}
     */
    @PutMapping("/api/teams/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_ORGANIZER')")
    public ResponseEntity<ApiResponse<TeamUpdateResponseDTO>> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamUpdateRequestDTO request) {
        try {
            TeamUpdateResponseDTO response = teamService.updateTeam(id, request);
            return ResponseEntity.ok(ApiResponse.success("Team updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 3.5 Delete Team
     * DELETE /api/teams/{id}
     */
    @DeleteMapping("/api/teams/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.ok(ApiResponse.success("Team deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
