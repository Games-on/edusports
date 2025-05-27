package com.example.checkscam.repository;

import com.example.checkscam.entity.Team;
import com.example.checkscam.entity.Tournament;
import com.example.checkscam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    
    @Query("SELECT t FROM Team t WHERE t.tournament = :tournament")
    List<Team> findByTournament(@Param("tournament") Tournament tournament);
    
    @Query("SELECT COUNT(t) FROM Team t WHERE t.tournament = :tournament")
    int countByTournament(@Param("tournament") Tournament tournament);
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.captain LEFT JOIN FETCH t.tournament WHERE t.tournament = :tournament")
    List<Team> findByTournamentWithDetails(@Param("tournament") Tournament tournament);
    
    @Query("SELECT t FROM Team t LEFT JOIN FETCH t.captain LEFT JOIN FETCH t.tournament WHERE t.id = :id")
    Optional<Team> findByIdWithDetails(@Param("id") Long id);
    
    boolean existsByTournamentAndName(Tournament tournament, String name);
    
    Optional<Team> findByCaptain(User captain);
    
    // Statistics methods - Use Team.TeamStatus (inner enum)
    Long countByStatus(Team.TeamStatus status);
    Long countByStatusIn(List<Team.TeamStatus> statuses);
}
