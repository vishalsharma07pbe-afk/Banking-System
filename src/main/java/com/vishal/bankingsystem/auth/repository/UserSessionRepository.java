package com.vishal.bankingsystem.auth.repository;

import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);
    List<UserSession> findByUser(UserEntity user);
    void deleteByRevokedTrueOrExpiryDateBefore(LocalDateTime cutoff);

    @Query("select distinct s from UserSession s join s.rotatedRefreshTokenHashes h where h = :tokenHash")
    Optional<UserSession> findByRotatedRefreshTokenHash(@Param("tokenHash") String tokenHash);
}
