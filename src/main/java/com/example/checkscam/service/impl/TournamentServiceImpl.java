package com.example.checkscam.service.impl;


import com.example.checkscam.constant.TournamentStatus;
import com.example.checkscam.dto.request.TournamentRequestDTO;
import com.example.checkscam.dto.response.PaginatedResponseDTO;
import com.example.checkscam.dto.response.TournamentResponseDTO;
import com.example.checkscam.entity.Tournament;
import com.example.checkscam.repository.TournamentRepository;
import com.example.checkscam.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {
    private final TournamentRepository tournamentRepository;

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
        dto.setCurrentTeams(tournament.getCurrentTeams());
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
}
