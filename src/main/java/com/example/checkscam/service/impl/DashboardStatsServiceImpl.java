package com.example.checkscam.service.impl;

import com.example.checkscam.constant.TournamentStatus;
import com.example.checkscam.dto.response.DashboardStatsResponseDTO;
import com.example.checkscam.entity.Match;
import com.example.checkscam.entity.Team; // Import Team class
import com.example.checkscam.repository.MatchRepository;
import com.example.checkscam.repository.TeamRepository;
import com.example.checkscam.repository.TournamentRepository;
import com.example.checkscam.repository.UserRepository;
import com.example.checkscam.service.DashboardStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@Transactional(readOnly = true)
@Slf4j
public class DashboardStatsServiceImpl implements DashboardStatsService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public DashboardStatsResponseDTO getDashboardStats() {
        log.info("📊 Calculating dashboard statistics...");
        
        try {
            // Tính số tournaments đang hoạt động (REGISTRATION, READY_TO_START, ONGOING)
            Long activeTournaments = tournamentRepository.countByStatusIn(
                Arrays.asList(
                    TournamentStatus.REGISTRATION,
                    TournamentStatus.READY_TO_START,
                    TournamentStatus.ONGOING
                )
            );
            log.info("✅ Active tournaments: {}", activeTournaments);

            // Tính tổng số tournaments
            Long totalTournaments = tournamentRepository.count();
            log.info("✅ Total tournaments: {}", totalTournaments);

            // Tính số tournaments đã hoàn thành
            Long completedTournaments = tournamentRepository.countByStatus(TournamentStatus.COMPLETED);
            log.info("✅ Completed tournaments: {}", completedTournaments);

            // Tính số teams đã đăng ký (ACTIVE teams) - FIX: Use Team.TeamStatus
            Long registeredTeams = teamRepository.countByStatus(Team.TeamStatus.ACTIVE);
            log.info("✅ Registered teams: {}", registeredTeams);

            // Tính số matches đã chơi
            Long matchesPlayed = matchRepository.countByStatus(Match.MatchStatus.COMPLETED);
            log.info("✅ Matches played: {}", matchesPlayed);

            // Tính tổng số matches
            Long totalMatches = matchRepository.count();
            log.info("✅ Total matches: {}", totalMatches);

            // Tính số matches đang pending
            Long pendingMatches = matchRepository.countByStatusIn(
                Arrays.asList(Match.MatchStatus.SCHEDULED, Match.MatchStatus.LIVE)
            );
            log.info("✅ Pending matches: {}", pendingMatches);

            // Tính success rate (% matches được hoàn thành thành công)
            Double successRate = 0.0;
            if (totalMatches > 0) {
                successRate = (matchesPlayed.doubleValue() / totalMatches.doubleValue()) * 100;
            }
            log.info("✅ Success rate: {}%", String.format("%.1f", successRate));

            // Tính tổng số users
            Long totalUsers = userRepository.count();
            log.info("✅ Total users: {}", totalUsers);

            DashboardStatsResponseDTO stats = new DashboardStatsResponseDTO(
                activeTournaments,
                registeredTeams,
                matchesPlayed,
                successRate,
                totalUsers,
                completedTournaments,
                totalTournaments,
                pendingMatches,
                totalMatches
            );

            log.info("📊 Dashboard statistics calculation completed successfully");
            return stats;

        } catch (Exception e) {
            log.error("❌ Error calculating dashboard statistics: {}", e.getMessage(), e);
            
            // Return default stats in case of error
            return new DashboardStatsResponseDTO(
                0L, 0L, 0L, 0.0, 0L, 0L, 0L, 0L, 0L
            );
        }
    }
}
