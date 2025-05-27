package com.example.checkscam.service.impl;

import com.example.checkscam.constant.TournamentStatus;
import com.example.checkscam.dto.request.TournamentRequestDTO;
import com.example.checkscam.dto.response.PaginatedResponseDTO;
import com.example.checkscam.dto.response.TournamentResponseDTO;
import com.example.checkscam.dto.response.TournamentCreateResponseDTO;
import com.example.checkscam.dto.response.TournamentUpdateResponseDTO;
import com.example.checkscam.dto.response.TournamentStartResponseDTO;
import com.example.checkscam.dto.response.CurrentRoundResponseDTO;
import com.example.checkscam.entity.Tournament;
import com.example.checkscam.entity.Team;
import com.example.checkscam.entity.User;
import com.example.checkscam.entity.Match;
import com.example.checkscam.exception.DataNotFoundException;
import com.example.checkscam.repository.TournamentRepository;
import com.example.checkscam.repository.TeamRepository;
import com.example.checkscam.repository.UserRepository;
import com.example.checkscam.repository.MatchRepository;
import com.example.checkscam.service.TournamentService;
import com.example.checkscam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Override
    public PaginatedResponseDTO<TournamentResponseDTO> getAllTournaments(TournamentRequestDTO request) {
        // Adjust page (1-based to 0-based)
        int page = Math.max(1, request.getPage());
        int limit = Math.max(1, request.getLimit());
        Pageable pageable = PageRequest.of(page - 1, limit);

        // Convert status to enum (null if invalid)
        TournamentStatus tournamentStatus = null;
        if (request.getStatus() != null) {
            try {
                tournamentStatus = TournamentStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Fetch paginated tournaments
        Page<Tournament> tournamentPage = tournamentRepository.findTournaments(
                tournamentStatus, request.getSportType(), request.getSearch(), pageable);

        // Map entities to DTOs
        var tournaments = tournamentPage.getContent().stream().map(this::toDTO).toList();

        // Build pagination metadata
        var pagination = new PaginatedResponseDTO.Pagination();
        pagination.setCurrentPage(page);
        pagination.setTotalPages(tournamentPage.getTotalPages());
        pagination.setTotalItems(tournamentPage.getTotalElements());
        pagination.setItemsPerPage(limit);

        // Build response
        var dataWrapper = new PaginatedResponseDTO.DataWrapper<TournamentResponseDTO>();
        dataWrapper.setTournaments(tournaments);
        dataWrapper.setPagination(pagination);

        var response = new PaginatedResponseDTO<TournamentResponseDTO>();
        response.setSuccess(true);
        response.setData(dataWrapper);

        return response;
    }

    private TournamentResponseDTO toDTO(Tournament tournament) {
        var dto = new TournamentResponseDTO();
        dto.setId(tournament.getId());
        dto.setName(tournament.getName());
        dto.setSportType(tournament.getSportType());
        dto.setDescription(tournament.getDescription());
        dto.setMaxTeams(tournament.getMaxTeams());
        dto.setCurrentTeams(teamRepository.countByTournament(tournament));
        dto.setStartDate(tournament.getStartDate());
        dto.setEndDate(tournament.getEndDate());
        dto.setLocation(tournament.getLocation());
        dto.setRegistrationDeadline(tournament.getRegistrationDeadline());
        dto.setStatus(tournament.getStatus());
        dto.setRules(tournament.getRules());
        dto.setPrizeInfo(tournament.getPrizeInfo());
        dto.setContactInfo(tournament.getContactInfo());

        // Convert created_at (Long) to LocalDateTime
        if (tournament.getCreatedAt() != null) {
            dto.setCreatedAt(LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(tournament.getCreatedAt()), ZoneId.of("UTC")));
        }

        // Map created_by
        if (tournament.getCreatedBy() != null) {
            var userDTO = new TournamentResponseDTO.UserDTO();
            userDTO.setId(tournament.getCreatedBy().getId());
            userDTO.setName(tournament.getCreatedBy().getName());
            dto.setCreatedBy(userDTO);
        }

        // Map winner_team
        if (tournament.getWinnerTeam() != null) {
            var teamDTO = new TournamentResponseDTO.TeamDTO();
            teamDTO.setId(tournament.getWinnerTeam().getId());
            teamDTO.setName(tournament.getWinnerTeam().getName());
            dto.setWinnerTeam(teamDTO);
        }

        // Map runner_up_team
        if (tournament.getRunnerUpTeam() != null) {
            var teamDTO = new TournamentResponseDTO.TeamDTO();
            teamDTO.setId(tournament.getRunnerUpTeam().getId());
            teamDTO.setName(tournament.getRunnerUpTeam().getName());
            dto.setRunnerUpTeam(teamDTO);
        }

        return dto;
    }

    @Override
    public TournamentResponseDTO getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + id));
        
        TournamentResponseDTO dto = toDTO(tournament);
        
        // Add teams information
        List<Team> teams = teamRepository.findByTournament(tournament);
        List<TournamentResponseDTO.TeamDTO> teamDTOs = teams.stream()
                .map(this::toTeamDTO)
                .collect(Collectors.toList());
        dto.setTeams(teamDTOs);
        
        return dto;
    }
    
    @Override
    public CurrentRoundResponseDTO getCurrentRound(Long tournamentId) {
        // Get tournament
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + tournamentId));
        
        // Get all matches for this tournament
        List<Match> allMatches = matchRepository.findByTournament(tournament);
        
        if (allMatches.isEmpty()) {
            // No matches yet - tournament not started
            return CurrentRoundResponseDTO.builder()
                    .tournamentId(tournamentId)
                    .tournamentName(tournament.getName())
                    .currentRound(1)
                    .currentRoundName("Vòng 1")
                    .totalRounds(0)
                    .completedRounds(0)
                    .isRoundComplete(false)
                    .canAdvanceToNextRound(false)
                    .currentRoundMatches(0)
                    .completedCurrentRoundMatches(0)
                    .status(tournament.getStatus().name())
                    .build();
        }
        
        // Find max round number
        int maxRound = allMatches.stream()
                .mapToInt(Match::getRoundNumber)
                .max()
                .orElse(1);
        
        // Calculate current round and statistics
        int currentRound = 1;
        int completedRounds = 0;
        String currentRoundName = "Vòng 1";
        
        // Find the current active round
        for (int round = 1; round <= maxRound; round++) {
            final int currentRoundNumber = round; // Make final for lambda
            List<Match> roundMatches = allMatches.stream()
                    .filter(match -> match.getRoundNumber() == currentRoundNumber)
                    .collect(Collectors.toList());
            
            long completedMatches = roundMatches.stream()
                    .filter(match -> match.getStatus() == Match.MatchStatus.COMPLETED)
                    .count();
            
            if (completedMatches == roundMatches.size() && !roundMatches.isEmpty()) {
                // This round is completed
                completedRounds = round;
            } else if (!roundMatches.isEmpty()) {
                // This round has matches but not all completed - this is current round
                currentRound = round;
                currentRoundName = roundMatches.get(0).getRoundName();
                break;
            }
        }
        
        // If all rounds completed, current round is next round
        if (completedRounds == maxRound && maxRound > 0) {
            currentRound = maxRound + 1;
            currentRoundName = "Round " + currentRound;
        }
        
        // Get current round matches and statistics
        final int finalCurrentRound = currentRound; // Make final for lambda
        List<Match> currentRoundMatches = allMatches.stream()
                .filter(match -> match.getRoundNumber() == finalCurrentRound)
                .collect(Collectors.toList());
        
        int currentRoundMatchCount = currentRoundMatches.size();
        int completedCurrentRoundMatches = (int) currentRoundMatches.stream()
                .filter(match -> match.getStatus() == Match.MatchStatus.COMPLETED)
                .count();
        
        boolean isRoundComplete = currentRoundMatchCount > 0 && 
                completedCurrentRoundMatches == currentRoundMatchCount;
        boolean canAdvanceToNextRound = isRoundComplete && currentRound <= maxRound;
        
        return CurrentRoundResponseDTO.builder()
                .tournamentId(tournamentId)
                .tournamentName(tournament.getName())
                .currentRound(currentRound)
                .currentRoundName(currentRoundName)
                .totalRounds(maxRound)
                .completedRounds(completedRounds)
                .isRoundComplete(isRoundComplete)
                .canAdvanceToNextRound(canAdvanceToNextRound)
                .currentRoundMatches(currentRoundMatchCount)
                .completedCurrentRoundMatches(completedCurrentRoundMatches)
                .status(tournament.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public TournamentCreateResponseDTO createTournament(TournamentRequestDTO request) {
        // Get current user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Tournament tournament = new Tournament();
        tournament.setName(request.getName());
        tournament.setSportType(request.getSportType());
        tournament.setDescription(request.getDescription());
        tournament.setMaxTeams(request.getMaxTeams());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setLocation(request.getLocation());
        tournament.setRegistrationDeadline(request.getRegistrationDeadline());
        tournament.setRules(request.getRules());
        tournament.setPrizeInfo(request.getPrizeInfo());
        tournament.setContactInfo(request.getContactInfo());
        tournament.setStatus(TournamentStatus.REGISTRATION);
        tournament.setCreatedAt(System.currentTimeMillis());
        tournament.setCreatedBy(currentUser);

        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Create response DTO for creation
        TournamentCreateResponseDTO responseDTO = new TournamentCreateResponseDTO();
        responseDTO.setId(savedTournament.getId());
        responseDTO.setName(savedTournament.getName());
        responseDTO.setSportType(savedTournament.getSportType());
        responseDTO.setStatus(savedTournament.getStatus());
        responseDTO.setCurrentTeams(0);
        responseDTO.setCreatedAt(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(savedTournament.getCreatedAt()), ZoneId.of("UTC")));
        
        return responseDTO;
    }

    @Override
    @Transactional
    public TournamentUpdateResponseDTO updateTournament(Long id, TournamentRequestDTO request) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + id));

        // Get current user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Update fields if provided
        if (request.getName() != null) {
            tournament.setName(request.getName());
        }
        if (request.getDescription() != null) {
            tournament.setDescription(request.getDescription());
        }
        if (request.getMaxTeams() != null) {
            tournament.setMaxTeams(request.getMaxTeams());
        }
        if (request.getLocation() != null) {
            tournament.setLocation(request.getLocation());
        }
        if (request.getStartDate() != null) {
            tournament.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            tournament.setEndDate(request.getEndDate());
        }
        if (request.getRegistrationDeadline() != null) {
            tournament.setRegistrationDeadline(request.getRegistrationDeadline());
        }
        if (request.getRules() != null) {
            tournament.setRules(request.getRules());
        }
        if (request.getPrizeInfo() != null) {
            tournament.setPrizeInfo(request.getPrizeInfo());
        }
        if (request.getContactInfo() != null) {
            tournament.setContactInfo(request.getContactInfo());
        }
        
        tournament.setLastUpdatedAt(System.currentTimeMillis());
        tournament.setLastUpdatedBy(currentUser);

        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Create update response DTO
        TournamentUpdateResponseDTO responseDTO = new TournamentUpdateResponseDTO();
        responseDTO.setId(savedTournament.getId());
        responseDTO.setName(savedTournament.getName());
        responseDTO.setMaxTeams(savedTournament.getMaxTeams());
        responseDTO.setLastUpdatedAt(LocalDateTime.ofInstant(
                Instant.ofEpochMilli(savedTournament.getLastUpdatedAt()), ZoneId.of("UTC")));
        
        return responseDTO;
    }

    @Override
    @Transactional
    public void deleteTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + id));
        
        // Check if tournament can be deleted (e.g., not started yet)
        if (tournament.getStatus() == TournamentStatus.ONGOING) {
            throw new RuntimeException("Cannot delete a tournament that is currently ongoing");
        }
        
        tournamentRepository.delete(tournament);
    }

    @Override
    @Transactional
    public TournamentStartResponseDTO startTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + id));
        
        if (tournament.getStatus() != TournamentStatus.REGISTRATION) {
            throw new RuntimeException("Tournament is not in registration status");
        }
        
        // Check if we have enough teams
        int teamCount = teamRepository.countByTournament(tournament);
        if (teamCount < 2) {
            throw new RuntimeException("Need at least 2 teams to start tournament");
        }
        
        // Update tournament status
        tournament.setStatus(TournamentStatus.ONGOING);
        tournament.setLastUpdatedAt(System.currentTimeMillis());
        tournamentRepository.save(tournament);
        
        // TODO: Generate matches here
        int matchesGenerated = generateMatches(tournament);
        
        TournamentStartResponseDTO result = new TournamentStartResponseDTO();
        result.setId(tournament.getId());
        result.setStatus(tournament.getStatus());
        result.setMatchesGenerated(matchesGenerated);
        
        return result;
    }
    
    private int generateMatches(Tournament tournament) {
        // Simple implementation - just return team count for now
        // In real implementation, you would create match entities based on tournament format
        return teamRepository.countByTournament(tournament);
    }
    
    private TournamentResponseDTO.TeamDTO toTeamDTO(Team team) {
        TournamentResponseDTO.TeamDTO dto = new TournamentResponseDTO.TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setTeamColor(team.getTeamColor());
        dto.setMemberCount(team.getMemberCount());
        dto.setStatus(team.getStatus().name());
        
        if (team.getCaptain() != null) {
            TournamentResponseDTO.UserDTO captainDTO = new TournamentResponseDTO.UserDTO();
            captainDTO.setId(team.getCaptain().getId());
            captainDTO.setName(team.getCaptain().getName());
            dto.setCaptain(captainDTO);
        }
        
        return dto;
    }
}
