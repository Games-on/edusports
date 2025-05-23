package com.example.checkscam.repository;

import com.example.checkscam.constant.TournamentStatus;
import com.example.checkscam.entity.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    @Query("SELECT t FROM Tournament t " +
            "WHERE (:status IS NULL OR t.status = :status) " +
            "AND (:sportType IS NULL OR t.sportType = :sportType) " +
            "AND (:search IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Tournament> findTournaments(
            @Param("status") TournamentStatus status,
            @Param("sportType") String sportType,
            @Param("search") String search,
            Pageable pageable);
}
