package com.example.checkscam.service.impl;

import com.example.checkscam.dto.team.*;
import com.example.checkscam.entity.*;
import com.example.checkscam.exception.DataNotFoundException;
import com.example.checkscam.exception.InvalidParamException;
import com.example.checkscam.repository.*;
import com.example.checkscam.service.TeamService;
import com.example.checkscam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentRegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponseDTO> getTeamsByTournament(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + tournamentId));

        List<Team> teams = teamRepository.findByTournamentWithDetails(tournament);

        return teams.stream()
                .map(this::convertToTeamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponseDTO getTeamById(Long teamId) {
        Team team = teamRepository.findByIdWithDetails(teamId)
                .orElseThrow(() -> new DataNotFoundException("Team not found with id: " + teamId));

        TeamResponseDTO response = convertToDetailedTeamResponseDTO(team);
        
        // Get matches for this team
        List<Match> matches = matchRepository.findByTeam1OrTeam2(team, team);
        if (!matches.isEmpty()) {
            response.setMatches(matches.stream()
                    .map(match -> convertToMatchDTO(match, team))
                    .collect(Collectors.toList()));
        }

        return response;
    }

    @Override
    public TeamRegistrationResponseDTO registerTeamForTournament(Long tournamentId, TeamRegistrationRequestDTO request) {
        // Get current user
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new InvalidParamException("User not authenticated"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Get tournament
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + tournamentId));

        // Check if tournament is still accepting registrations
        if (tournament.getRegistrationDeadline() != null && !tournament.getRegistrationDeadline().isAfter(LocalDateTime.now())) {
            throw new InvalidParamException("Tournament registration period has ended");
        }

        // Check if user already registered for this tournament
        if (registrationRepository.existsByTournamentAndUser(tournament, currentUser)) {
            throw new InvalidParamException("User already registered for this tournament");
        }

        // Check tournament capacity
        long currentTeamsCount = registrationRepository.countApprovedRegistrationsByTournament(tournament);
        if (currentTeamsCount >= tournament.getMaxTeams()) {
            throw new InvalidParamException("Tournament is full");
        }

        // Check if team name is unique in tournament
        if (teamRepository.existsByTournamentAndName(tournament, request.getTeamName())) {
            throw new InvalidParamException("Team name already exists in this tournament");
        }

        long currentTime = Instant.now().toEpochMilli();

        // Create tournament registration
        TournamentRegistration registration = new TournamentRegistration();
        registration.setTournament(tournament);
        registration.setUser(currentUser);
        registration.setStatus(TournamentRegistration.RegistrationStatus.APPROVED); // Auto approve for now
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setNotes(request.getNotes());
        registration.setCreatedAt(currentTime);
        registration.setCreatedBy(currentUser);
        
        registration = registrationRepository.save(registration);

        // Create team
        Team team = new Team();
        team.setTournament(tournament);
        team.setName(request.getTeamName());
        team.setTeamColor(request.getTeamColor());
        team.setMemberCount(request.getMemberCount());
        team.setCaptain(currentUser);
        team.setContactInfo(request.getContactInfo());
        team.setLogoUrl(request.getLogoUrl());
        team.setStatus(Team.TeamStatus.ACTIVE);
        team.setCreatedAt(currentTime);
        team.setCreatedBy(currentUser);
        
        team = teamRepository.save(team);

        // Build response
        return TeamRegistrationResponseDTO.builder()
                .registration(TeamRegistrationResponseDTO.RegistrationDTO.builder()
                        .id(registration.getId())
                        .tournamentId(tournament.getId())
                        .userId(currentUser.getId())
                        .status(registration.getStatus().name())
                        .registrationDate(registration.getRegistrationDate().format(DATE_FORMATTER))
                        .notes(registration.getNotes())
                        .createdAt(formatTimestamp(registration.getCreatedAt()))
                        .build())
                .team(TeamRegistrationResponseDTO.TeamDTO.builder()
                        .id(team.getId())
                        .name(team.getName())
                        .tournamentId(tournament.getId())
                        .teamColor(team.getTeamColor())
                        .memberCount(team.getMemberCount())
                        .captainUserId(currentUser.getId())
                        .contactInfo(team.getContactInfo())
                        .logoUrl(team.getLogoUrl())
                        .status(team.getStatus().name())
                        .createdAt(formatTimestamp(team.getCreatedAt()))
                        .build())
                .tournament(TeamRegistrationResponseDTO.TournamentDTO.builder()
                        .id(tournament.getId())
                        .name(tournament.getName())
                        .sportType(tournament.getSportType())
                        .currentTeams((int) (currentTeamsCount + 1))
                        .maxTeams(tournament.getMaxTeams())
                        .availableSpots(tournament.getMaxTeams() - (int) (currentTeamsCount + 1))
                        .build())
                .build();
    }

    @Override
    public TeamUpdateResponseDTO updateTeam(Long teamId, TeamUpdateRequestDTO request) {
        // Get current user
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new InvalidParamException("User not authenticated"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Get team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new DataNotFoundException("Team not found with id: " + teamId));

        // Check permissions - only team captain or admin/organizer can update
        boolean isTeamCaptain = team.getCaptain().getId().equals(currentUser.getId());
        boolean isAdminOrOrganizer = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "ORGANIZER".equals(role.getName()));

        if (!isTeamCaptain && !isAdminOrOrganizer) {
            throw new InvalidParamException("You don't have permission to update this team");
        }

        // Update team fields
        if (request.getName() != null && !request.getName().equals(team.getName())) {
            // Check if new name is unique in tournament
            if (teamRepository.existsByTournamentAndName(team.getTournament(), request.getName())) {
                throw new InvalidParamException("Team name already exists in this tournament");
            }
            team.setName(request.getName());
        }

        if (request.getTeamColor() != null) {
            team.setTeamColor(request.getTeamColor());
        }

        if (request.getMemberCount() != null) {
            team.setMemberCount(request.getMemberCount());
        }

        if (request.getContactInfo() != null) {
            team.setContactInfo(request.getContactInfo());
        }

        if (request.getLogoUrl() != null) {
            team.setLogoUrl(request.getLogoUrl());
        }

        team.setLastUpdatedAt(Instant.now().toEpochMilli());
        team.setLastUpdatedBy(currentUser);

        team = teamRepository.save(team);

        return TeamUpdateResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .teamColor(team.getTeamColor())
                .memberCount(team.getMemberCount())
                .contactInfo(team.getContactInfo())
                .logoUrl(team.getLogoUrl())
                .lastUpdatedAt(formatTimestamp(team.getLastUpdatedAt()))
                .build();
    }

    @Override
    public void deleteTeam(Long teamId) {
        // Get current user
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new InvalidParamException("User not authenticated"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Get team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new DataNotFoundException("Team not found with id: " + teamId));

        // Check permissions - only team captain or admin can delete
        boolean isTeamCaptain = team.getCaptain().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));

        if (!isTeamCaptain && !isAdmin) {
            throw new InvalidParamException("You don't have permission to delete this team");
        }

        // Check if team has any matches
        List<Match> matches = matchRepository.findByTeam1OrTeam2(team, team);
        if (!matches.isEmpty()) {
            throw new InvalidParamException("Cannot delete team that has scheduled matches");
        }

        // Delete team (this will also handle registration cleanup if needed)
        teamRepository.delete(team);
    }

    private TeamResponseDTO convertToTeamResponseDTO(Team team) {
        return TeamResponseDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .teamColor(team.getTeamColor())
                .memberCount(team.getMemberCount())
                .status(team.getStatus().name())
                .logoUrl(team.getLogoUrl())
                .contactInfo(team.getContactInfo())
                .captain(TeamResponseDTO.CaptainDTO.builder()
                        .id(team.getCaptain().getId())
                        .name(team.getCaptain().getName())
                        .email(team.getCaptain().getEmail())
                        .build())
                .createdAt(formatTimestamp(team.getCreatedAt()))
                .build();
    }

    private TeamResponseDTO convertToDetailedTeamResponseDTO(Team team) {
        TeamResponseDTO response = convertToTeamResponseDTO(team);
        
        // Add tournament details
        response.setTournament(TeamResponseDTO.TournamentDTO.builder()
                .id(team.getTournament().getId())
                .name(team.getTournament().getName())
                .sportType(team.getTournament().getSportType())
                .build());

        return response;
    }

    private TeamResponseDTO.MatchDTO convertToMatchDTO(Match match, Team currentTeam) {
        Team opponent = match.getTeam1().getId().equals(currentTeam.getId()) 
                ? match.getTeam2() : match.getTeam1();

        return TeamResponseDTO.MatchDTO.builder()
                .id(match.getId())
                .roundName(match.getRoundName())
                .opponent(TeamResponseDTO.MatchDTO.OpponentDTO.builder()
                        .id(opponent.getId())
                        .name(opponent.getName())
                        .build())
                .matchDate(match.getMatchDate() != null 
                        ? match.getMatchDate().format(DATE_FORMATTER) : null)
                .status(match.getStatus().name())
                .build();
    }

    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) return null;
        return Instant.ofEpochMilli(timestamp).toString();
    }
}
