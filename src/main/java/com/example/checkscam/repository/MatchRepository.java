package com.example.checkscam.repository;

import com.example.checkscam.entity.Match;
import com.example.checkscam.entity.Tournament;
import com.example.checkscam.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {
    
    @Query("SELECT m FROM Match m WHERE m.tournament = :tournament")
    List<Match> findByTournament(@Param("tournament") Tournament tournament);
    
    @Query("SELECT m FROM Match m WHERE m.team1 = :team1 OR m.team2 = :team2 ORDER BY m.matchDate ASC")
    List<Match> findByTeam1OrTeam2(@Param("team1") Team team1, @Param("team2") Team team2);
    
    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.team1 LEFT JOIN FETCH m.team2 LEFT JOIN FETCH m.tournament WHERE m.tournament = :tournament ORDER BY m.roundNumber ASC, m.matchNumber ASC")
    List<Match> findByTournamentWithDetails(@Param("tournament") Tournament tournament);
    
    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.team1 LEFT JOIN FETCH m.team2 LEFT JOIN FETCH m.tournament WHERE m.tournament = :tournament AND m.roundNumber = :round ORDER BY m.matchNumber ASC")
    List<Match> findByTournamentAndRound(@Param("tournament") Tournament tournament, @Param("round") int round);
    
    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.team1 LEFT JOIN FETCH m.team2 LEFT JOIN FETCH m.tournament WHERE m.tournament = :tournament AND m.status = :status ORDER BY m.roundNumber ASC, m.matchNumber ASC")
    List<Match> findByTournamentAndStatus(@Param("tournament") Tournament tournament, @Param("status") Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.team1 LEFT JOIN FETCH m.team2 LEFT JOIN FETCH m.tournament WHERE m.tournament = :tournament AND m.roundNumber = :round AND m.status = :status ORDER BY m.matchNumber ASC")
    List<Match> findByTournamentAndRoundAndStatus(@Param("tournament") Tournament tournament, @Param("round") int round, @Param("status") Match.MatchStatus status);
    
    @Query("SELECT m FROM Match m LEFT JOIN FETCH m.team1 LEFT JOIN FETCH m.team2 LEFT JOIN FETCH m.tournament LEFT JOIN FETCH m.winnerTeam WHERE m.id = :id")
    Optional<Match> findByIdWithDetails(@Param("id") Long id);
    
    @Query("SELECT DISTINCT m.roundNumber FROM Match m WHERE m.tournament = :tournament ORDER BY m.roundNumber ASC")
    List<Integer> findDistinctRoundsByTournament(@Param("tournament") Tournament tournament);
    
    @Query("SELECT MAX(m.roundNumber) FROM Match m WHERE m.tournament = :tournament")
    Integer findMaxRoundByTournament(@Param("tournament") Tournament tournament);
    
    @Query("SELECT m FROM Match m WHERE m.tournament = :tournament AND m.roundNumber = :nextRound AND (m.team1 IS NULL OR m.team2 IS NULL) ORDER BY m.matchNumber ASC")
    List<Match> findIncompleteMatchesInRound(@Param("tournament") Tournament tournament, @Param("nextRound") int nextRound);
}
