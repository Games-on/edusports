package com.example.checkscam.repository;

import com.example.checkscam.entity.PasswordResetToken;
import com.example.checkscam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.user = :user AND prt.used = false AND prt.expiresAt > :now")
    Optional<PasswordResetToken> findValidTokenByUser(@Param("user") User user, @Param("now") Instant now);
    
    void deleteByUser(User user);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);
    
    @Modifying
    @Query("UPDATE PasswordResetToken prt SET prt.used = true WHERE prt.user = :user")
    void invalidateAllTokensForUser(@Param("user") User user);
}
