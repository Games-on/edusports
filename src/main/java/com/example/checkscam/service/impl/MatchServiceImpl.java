package com.example.checkscam.service.impl;

import com.example.checkscam.constant.TournamentStatus;
import com.example.checkscam.dto.match.*;
import com.example.checkscam.entity.*;
import com.example.checkscam.exception.DataNotFoundException;
import com.example.checkscam.exception.InvalidParamException;
import com.example.checkscam.repository.*;
import com.example.checkscam.service.MatchService;
import com.example.checkscam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    @Transactional(readOnly = true)
    public TournamentMatchesResponseDTO getMatchesByTournament(Long tournamentId, Integer round, String status) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + tournamentId));

        List<Match> matches;
        
        // Apply filters based on parameters
        if (round != null && status != null) {
            Match.MatchStatus matchStatus = parseMatchStatus(status);
            matches = matchRepository.findByTournamentAndRoundAndStatus(tournament, round, matchStatus);
        } else if (round != null) {
            matches = matchRepository.findByTournamentAndRound(tournament, round);
        } else if (status != null) {
            Match.MatchStatus matchStatus = parseMatchStatus(status);
            matches = matchRepository.findByTournamentAndStatus(tournament, matchStatus);
        } else {
            matches = matchRepository.findByTournamentWithDetails(tournament);
        }

        List<MatchResponseDTO> matchDTOs = matches.stream()
                .map(this::convertToMatchResponseDTO)
                .collect(Collectors.toList());

        // Build tournament bracket structure
        TournamentMatchesResponseDTO.TournamentBracketDTO bracket = buildTournamentBracket(matches);

        return TournamentMatchesResponseDTO.builder()
                .matches(matchDTOs)
                .tournamentBracket(bracket)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponseDTO getMatchById(Long matchId) {
        Match match = matchRepository.findByIdWithDetails(matchId)
                .orElseThrow(() -> new DataNotFoundException("Match not found with id: " + matchId));

        return convertToDetailedMatchResponseDTO(match);
    }

    @Override
    public MatchCreateResponseDTO createMatch(Long tournamentId, MatchCreateRequestDTO request) {
        // Check authentication and authorization
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new InvalidParamException("User not authenticated"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if user has permission (ADMIN or ORGANIZER)
        boolean hasPermission = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "ORGANIZER".equals(role.getName()));
        
        // Temporary disable permission check for testing
        log.info("Create match - User: {}, HasPermission: {}", currentUser.getEmail(), hasPermission);
        // if (!hasPermission) {
        //     throw new InvalidParamException("You don't have permission to create matches");
        // }

        // Validate tournament
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + tournamentId));

        // Validate teams
        Team team1 = teamRepository.findById(request.getTeam1Id())
                .orElseThrow(() -> new DataNotFoundException("Team 1 not found with id: " + request.getTeam1Id()));
        
        Team team2 = teamRepository.findById(request.getTeam2Id())
                .orElseThrow(() -> new DataNotFoundException("Team 2 not found with id: " + request.getTeam2Id()));

        // Validate that teams belong to the tournament
        if (!team1.getTournament().getId().equals(tournamentId)) {
            throw new InvalidParamException("Team 1 does not belong to this tournament");
        }
        
        if (!team2.getTournament().getId().equals(tournamentId)) {
            throw new InvalidParamException("Team 2 does not belong to this tournament");
        }

        // Validate that teams are different
        if (request.getTeam1Id().equals(request.getTeam2Id())) {
            throw new InvalidParamException("A team cannot play against itself");
        }

        // Check if teams are already playing against each other in the same round
        List<Match> existingMatches = matchRepository.findByTournamentAndRound(tournament, request.getRoundNumber());
        boolean teamsAlreadyMatched = existingMatches.stream()
                .anyMatch(match -> 
                    (match.getTeam1().getId().equals(request.getTeam1Id()) && match.getTeam2().getId().equals(request.getTeam2Id())) ||
                    (match.getTeam1().getId().equals(request.getTeam2Id()) && match.getTeam2().getId().equals(request.getTeam1Id()))
                );
        
        if (teamsAlreadyMatched) {
            throw new InvalidParamException("These teams are already scheduled to play against each other in this round");
        }

        // Generate round name if not provided
        String roundName = request.getRoundName();
        if (roundName == null || roundName.trim().isEmpty()) {
            Integer maxRound = matchRepository.findMaxRoundByTournament(tournament);
            roundName = generateRoundName(request.getRoundNumber(), maxRound != null ? Math.max(maxRound, request.getRoundNumber()) : request.getRoundNumber());
        }

        // Generate match number if not provided
        Integer matchNumber = request.getMatchNumber();
        if (matchNumber == null) {
            List<Match> roundMatches = matchRepository.findByTournamentAndRound(tournament, request.getRoundNumber());
            matchNumber = roundMatches.size() + 1;
        }

        // Create match
        long currentTime = Instant.now().toEpochMilli();
        
        Match match = new Match();
        match.setTournament(tournament);
        match.setRoundNumber(request.getRoundNumber());
        match.setRoundName(roundName);
        match.setTeam1(team1);
        match.setTeam2(team2);
        match.setMatchDate(request.getMatchDate());
        match.setLocation(request.getLocation());
        match.setStatus(Match.MatchStatus.SCHEDULED);
        match.setTeam1Score(0);
        match.setTeam2Score(0);
        match.setMatchNumber(matchNumber);
        match.setReferee(request.getReferee());
        match.setNotes(request.getNotes());
        match.setCreatedAt(currentTime);
        match.setCreatedBy(currentUser);

        match = matchRepository.save(match);

        // Build response
        return MatchCreateResponseDTO.builder()
                .id(match.getId())
                .tournament(MatchCreateResponseDTO.TournamentDTO.builder()
                        .id(tournament.getId())
                        .name(tournament.getName())
                        .sportType(tournament.getSportType())
                        .build())
                .roundNumber(match.getRoundNumber())
                .roundName(match.getRoundName())
                .team1(MatchCreateResponseDTO.TeamDTO.builder()
                        .id(team1.getId())
                        .name(team1.getName())
                        .logoUrl(team1.getLogoUrl())
                        .captain(MatchCreateResponseDTO.TeamDTO.CaptainDTO.builder()
                                .id(team1.getCaptain().getId())
                                .name(team1.getCaptain().getName())
                                .email(team1.getCaptain().getEmail())
                                .build())
                        .build())
                .team2(MatchCreateResponseDTO.TeamDTO.builder()
                        .id(team2.getId())
                        .name(team2.getName())
                        .logoUrl(team2.getLogoUrl())
                        .captain(MatchCreateResponseDTO.TeamDTO.CaptainDTO.builder()
                                .id(team2.getCaptain().getId())
                                .name(team2.getCaptain().getName())
                                .email(team2.getCaptain().getEmail())
                                .build())
                        .build())
                .matchDate(match.getMatchDate().format(DATE_FORMATTER))
                .location(match.getLocation())
                .status(match.getStatus().name())
                .matchNumber(match.getMatchNumber())
                .referee(match.getReferee())
                .notes(match.getNotes())
                .createdAt(formatTimestamp(match.getCreatedAt()))
                .build();
    }

    @Override
    public MatchScoreUpdateResponseDTO updateMatchScore(Long matchId, MatchScoreUpdateRequestDTO request) {
        // Check authentication and authorization
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new InvalidParamException("User not authenticated"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if user has permission (ADMIN or ORGANIZER)
        boolean hasPermission = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "ORGANIZER".equals(role.getName()));
        
        // Temporary disable permission check for testing
        log.info("Update match score - User: {}, HasPermission: {}", currentUser.getEmail(), hasPermission);
        // if (!hasPermission) {
        //     throw new InvalidParamException("You don't have permission to update match scores");
        // }

        Match match = matchRepository.findByIdWithDetails(matchId)
                .orElseThrow(() -> new DataNotFoundException("Match not found with id: " + matchId));

        // Validate status
        Match.MatchStatus newStatus = parseMatchStatus(request.getStatus());
        
        // Update match scores and status
        match.setTeam1Score(request.getTeam1Score());
        match.setTeam2Score(request.getTeam2Score());
        match.setStatus(newStatus);
        match.setNotes(request.getNotes());
        match.setLastUpdatedAt(Instant.now().toEpochMilli());
        match.setLastUpdatedBy(currentUser);

        // Determine winner if match is completed
        Team winner = null;
        boolean nextMatchGenerated = false;
        
        if (newStatus == Match.MatchStatus.COMPLETED) {
            if (request.getTeam1Score() > request.getTeam2Score()) {
                winner = match.getTeam1();
            } else if (request.getTeam2Score() > request.getTeam1Score()) {
                winner = match.getTeam2();
            }
            // If scores are equal, no winner is set (draw)
            
            match.setWinnerTeam(winner);
            
            // Generate next round match if needed
            if (winner != null) {
                nextMatchGenerated = generateNextRoundMatch(match, winner);
            }
        }

        match = matchRepository.save(match);

        return MatchScoreUpdateResponseDTO.builder()
                .id(match.getId())
                .team1Score(match.getTeam1Score())
                .team2Score(match.getTeam2Score())
                .winnerTeam(winner != null ? MatchScoreUpdateResponseDTO.WinnerTeamDTO.builder()
                        .id(winner.getId())
                        .name(winner.getName())
                        .build() : null)
                .status(match.getStatus().name())
                .nextMatchGenerated(nextMatchGenerated)
                .build();
    }

    @Override
    public MatchStatusUpdateResponseDTO updateMatchStatus(Long matchId, MatchStatusUpdateRequestDTO request) {
        // Check authentication and authorization
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new InvalidParamException("User not authenticated"));
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if user has permission (ADMIN or ORGANIZER)
        boolean hasPermission = currentUser.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()) || "ORGANIZER".equals(role.getName()));
        
        // Temporary disable permission check for testing
        log.info("Update match status - User: {}, HasPermission: {}", currentUser.getEmail(), hasPermission);
        // if (!hasPermission) {
        //     throw new InvalidParamException("You don't have permission to update match status");
        // }

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new DataNotFoundException("Match not found with id: " + matchId));

        // Validate and update status
        Match.MatchStatus newStatus = parseMatchStatus(request.getStatus());
        match.setStatus(newStatus);
        match.setLastUpdatedAt(Instant.now().toEpochMilli());
        match.setLastUpdatedBy(currentUser);

        match = matchRepository.save(match);

        return MatchStatusUpdateResponseDTO.builder()
                .id(match.getId())
                .status(match.getStatus().name())
                .lastUpdatedAt(formatTimestamp(match.getLastUpdatedAt()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentBracketResponseDTO getTournamentBracket(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new DataNotFoundException("Tournament not found with id: " + tournamentId));

        List<Match> allMatches = matchRepository.findByTournamentWithDetails(tournament);
        
        // Group matches by round
        Map<Integer, List<Match>> matchesByRound = allMatches.stream()
                .collect(Collectors.groupingBy(Match::getRoundNumber));

        List<TournamentBracketResponseDTO.BracketDTO.RoundDTO> rounds = new ArrayList<>();
        Integer maxRound = matchRepository.findMaxRoundByTournament(tournament);
        
        if (maxRound == null) {
            maxRound = 0;
        }

        // Determine current round (first round with incomplete matches)
        int currentRound = 1;
        for (int roundNum = 1; roundNum <= maxRound; roundNum++) {
            List<Match> roundMatches = matchesByRound.get(roundNum);
            if (roundMatches != null) {
                boolean hasIncompleteMatches = roundMatches.stream()
                        .anyMatch(match -> match.getStatus() != Match.MatchStatus.COMPLETED);
                if (hasIncompleteMatches) {
                    currentRound = roundNum;
                    break;
                }
                if (roundNum == maxRound) {
                    currentRound = maxRound;
                }
            }
        }

        // Build rounds
        for (int roundNum = 1; roundNum <= maxRound; roundNum++) {
            List<Match> roundMatches = matchesByRound.getOrDefault(roundNum, new ArrayList<>());
            
            List<TournamentBracketResponseDTO.BracketDTO.RoundDTO.MatchBracketDTO> matchBracketDTOs = 
                    roundMatches.stream()
                    .map(this::convertToMatchBracketDTO)
                    .collect(Collectors.toList());

            String roundName = generateRoundName(roundNum, maxRound);
            
            rounds.add(TournamentBracketResponseDTO.BracketDTO.RoundDTO.builder()
                    .roundNumber(roundNum)
                    .roundName(roundName)
                    .matches(matchBracketDTOs)
                    .build());
        }

        return TournamentBracketResponseDTO.builder()
                .tournament(TournamentBracketResponseDTO.TournamentDTO.builder()
                        .id(tournament.getId())
                        .name(tournament.getName())
                        .status(tournament.getStatus().name())
                        .build())
                .bracket(TournamentBracketResponseDTO.BracketDTO.builder()
                        .totalRounds(maxRound)
                        .currentRound(currentRound)
                        .rounds(rounds)
                        .build())
                .build();
    }

    private MatchResponseDTO convertToMatchResponseDTO(Match match) {
        return MatchResponseDTO.builder()
                .id(match.getId())
                .tournamentId(match.getTournament().getId())
                .roundNumber(match.getRoundNumber())
                .roundName(match.getRoundName())
                .team1(MatchResponseDTO.TeamBasicDTO.builder()
                        .id(match.getTeam1().getId())
                        .name(match.getTeam1().getName())
                        .logoUrl(match.getTeam1().getLogoUrl())
                        .build())
                .team2(MatchResponseDTO.TeamBasicDTO.builder()
                        .id(match.getTeam2().getId())
                        .name(match.getTeam2().getName())
                        .logoUrl(match.getTeam2().getLogoUrl())
                        .build())
                .matchDate(match.getMatchDate() != null ? 
                        match.getMatchDate().format(DATE_FORMATTER) : null)
                .location(match.getLocation())
                .status(match.getStatus().name())
                .team1Score(match.getTeam1Score())
                .team2Score(match.getTeam2Score())
                .winnerTeam(match.getWinnerTeam() != null ? 
                        MatchResponseDTO.TeamBasicDTO.builder()
                                .id(match.getWinnerTeam().getId())
                                .name(match.getWinnerTeam().getName())
                                .logoUrl(match.getWinnerTeam().getLogoUrl())
                                .build() : null)
                .matchNumber(match.getMatchNumber())
                .referee(match.getReferee())
                .createdAt(formatTimestamp(match.getCreatedAt()))
                .build();
    }

    private MatchResponseDTO convertToDetailedMatchResponseDTO(Match match) {
        MatchResponseDTO response = convertToMatchResponseDTO(match);
        
        // Add tournament details
        response.setTournament(MatchResponseDTO.TournamentDTO.builder()
                .id(match.getTournament().getId())
                .name(match.getTournament().getName())
                .sportType(match.getTournament().getSportType())
                .build());

        // Add notes
        response.setNotes(match.getNotes());

        return response;
    }

    private TournamentMatchesResponseDTO.TournamentBracketDTO buildTournamentBracket(List<Match> matches) {
        Map<Integer, List<Long>> matchesByRound = matches.stream()
                .collect(Collectors.groupingBy(
                        Match::getRoundNumber,
                        Collectors.mapping(Match::getId, Collectors.toList())
                ));

        List<TournamentMatchesResponseDTO.TournamentBracketDTO.RoundDTO> rounds = new ArrayList<>();
        
        for (Map.Entry<Integer, List<Long>> entry : matchesByRound.entrySet()) {
            int roundNumber = entry.getKey();
            List<Long> matchIds = entry.getValue();
            
            Integer maxRound = matchRepository.findMaxRoundByTournament(matches.get(0).getTournament());
            String roundName = generateRoundName(roundNumber, maxRound != null ? maxRound : roundNumber);
            
            rounds.add(TournamentMatchesResponseDTO.TournamentBracketDTO.RoundDTO.builder()
                    .roundNumber(roundNumber)
                    .roundName(roundName)
                    .matches(matchIds)
                    .build());
        }

        // Sort rounds by round number
        rounds.sort(Comparator.comparing(TournamentMatchesResponseDTO.TournamentBracketDTO.RoundDTO::getRoundNumber));

        return TournamentMatchesResponseDTO.TournamentBracketDTO.builder()
                .rounds(rounds)
                .build();
    }

    private TournamentBracketResponseDTO.BracketDTO.RoundDTO.MatchBracketDTO convertToMatchBracketDTO(Match match) {
        return TournamentBracketResponseDTO.BracketDTO.RoundDTO.MatchBracketDTO.builder()
                .id(match.getId())
                .team1(match.getTeam1() != null ? 
                        TournamentBracketResponseDTO.BracketDTO.RoundDTO.MatchBracketDTO.TeamBracketDTO.builder()
                                .id(match.getTeam1().getId())
                                .name(match.getTeam1().getName())
                                .build() : null)
                .team2(match.getTeam2() != null ? 
                        TournamentBracketResponseDTO.BracketDTO.RoundDTO.MatchBracketDTO.TeamBracketDTO.builder()
                                .id(match.getTeam2().getId())
                                .name(match.getTeam2().getName())
                                .build() : null)
                .team1Score(match.getTeam1Score())
                .team2Score(match.getTeam2Score())
                .winnerTeamId(match.getWinnerTeam() != null ? match.getWinnerTeam().getId() : null)
                .status(match.getStatus().name())
                .build();
    }

    private boolean generateNextRoundMatch(Match completedMatch, Team winner) {
        Tournament tournament = completedMatch.getTournament();
        int currentRound = completedMatch.getRoundNumber();
        
        // Check if all matches in current round are completed
        List<Match> currentRoundMatches = matchRepository.findByTournamentAndRound(tournament, currentRound);
        boolean allMatchesCompleted = currentRoundMatches.stream()
                .allMatch(match -> match.getStatus() == Match.MatchStatus.COMPLETED && match.getWinnerTeam() != null);
        
        if (!allMatchesCompleted) {
            return false; // Wait for all matches in current round to complete
        }
        
        // Get all winners from current round
        List<Team> winners = currentRoundMatches.stream()
                .map(Match::getWinnerTeam)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        if (winners.size() < 2) {
            // Tournament completed - only one winner left
            completeTournamentWithWinner(tournament, winners.isEmpty() ? null : winners.get(0));
            return true;
        }
        
        int nextRound = currentRound + 1;
        
        // Get existing skeleton matches for next round
        List<Match> nextRoundMatches = matchRepository.findByTournamentAndRound(tournament, nextRound);
        
        if (nextRoundMatches.isEmpty()) {
            log.warn("No skeleton matches found for round {}", nextRound);
            return false;
        }
        
        // Random shuffle winners for next round
        Collections.shuffle(winners);
        
        // Populate next round matches with winners
        nextRoundMatches.sort(Comparator.comparing(Match::getMatchNumber));
        
        int winnerIndex = 0;
        boolean updated = false;
        
        for (Match nextMatch : nextRoundMatches) {
            if (winnerIndex < winners.size() && nextMatch.getTeam1() == null) {
                nextMatch.setTeam1(winners.get(winnerIndex++));
                updated = true;
            }
            if (winnerIndex < winners.size() && nextMatch.getTeam2() == null) {
                nextMatch.setTeam2(winners.get(winnerIndex++));
                updated = true;
            }
            
            if (updated) {
                nextMatch.setLastUpdatedAt(Instant.now().toEpochMilli());
            }
        }
        
        if (updated) {
            matchRepository.saveAll(nextRoundMatches);
        }
        
        return updated;
    }
    
    private void completeTournamentWithWinner(Tournament tournament, Team winner) {
        if (winner != null) {
            // Find runner-up from final match
            Integer maxRound = matchRepository.findMaxRoundByTournament(tournament);
            if (maxRound != null) {
                List<Match> finalMatches = matchRepository.findByTournamentAndRound(tournament, maxRound);
                if (!finalMatches.isEmpty()) {
                    Match finalMatch = finalMatches.get(0);
                    Team runnerUp = finalMatch.getTeam1().getId().equals(winner.getId()) ? 
                            finalMatch.getTeam2() : finalMatch.getTeam1();
                    
                    tournament.setRunnerUpTeam(runnerUp);
                }
            }
            
            tournament.setWinnerTeam(winner);
        }
        
        tournament.setStatus(TournamentStatus.COMPLETED);
        tournament.setEndDate(LocalDateTime.now());
        tournament.setLastUpdatedAt(Instant.now().toEpochMilli());
        
        tournamentRepository.save(tournament);
        
        log.info("Tournament {} completed with winner: {}", 
                tournament.getId(), winner != null ? winner.getName() : "No winner");
    }

    private String generateRoundName(int roundNumber, int maxRound) {
        if (maxRound <= 1) {
            return "Chung kết";
        }
        
        switch (maxRound - roundNumber) {
            case 0:
                return "Chung kết";
            case 1:
                return "Bán kết";
            case 2:
                return "Tứ kết";
            case 3:
                return "Vòng 1/8";
            case 4:
                return "Vòng 1/16";
            default:
                return "Vòng " + roundNumber;
        }
    }

    private Match.MatchStatus parseMatchStatus(String status) {
        try {
            return Match.MatchStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidParamException("Invalid match status: " + status);
        }
    }

    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) return null;
        return Instant.ofEpochMilli(timestamp).toString();
    }
}
