package com.example.checkscam.repository;

import com.example.checkscam.entity.TournamentRegistration;
import com.example.checkscam.entity.Tournament;
import com.example.checkscam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {
    
    Optional<TournamentRegistration> findByTournamentAndUser(Tournament tournament, User user);
    
    List<TournamentRegistration> findByTournament(Tournament tournament);
    
    List<TournamentRegistration> findByUser(User user);
    
    @Query("SELECT COUNT(tr) FROM TournamentRegistration tr WHERE tr.tournament = :tournament AND tr.status = 'APPROVED'")
    long countApprovedRegistrationsByTournament(@Param("tournament") Tournament tournament);
    
    boolean existsByTournamentAndUser(Tournament tournament, User user);
}
