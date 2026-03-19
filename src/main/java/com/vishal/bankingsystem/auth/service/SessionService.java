package com.vishal.bankingsystem.auth.service;

import com.vishal.bankingsystem.auth.dto.AuthResponse;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.entity.UserSession;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.auth.repository.UserSessionRepository;
import com.vishal.bankingsystem.config.JwtProperties;
import com.vishal.bankingsystem.exception.UnauthorizedException;
import com.vishal.bankingsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final UserSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    // Create Session (Login)
    @Transactional
    public String createSession(String username, String ip, String device) {

        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        String refreshToken = UUID.randomUUID().toString();

        UserSession session = UserSession.builder()
                .user(user)
                .refreshTokenHash(hashToken(refreshToken))
                .expiryDate(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenDays()))
                .revoked(false)
                .ipAddress(ip)
                .deviceInfo(device)
                .build();

        sessionRepository.save(session);
        return refreshToken;
    }

    // 🔁 Refresh Token
    @Transactional
    public AuthResponse refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        String refreshTokenHash = hashToken(refreshToken);

        UserSession session = sessionRepository.findByRefreshTokenHash(refreshTokenHash)
                .orElseGet(() -> handleRefreshTokenReplayOrInvalid(refreshTokenHash));

        if (session.isRevoked()) {
            throw new UnauthorizedException("Session revoked");
        }

        if (session.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        UserEntity user = userRepository.findById(session.getUser().getId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        ensureSessionRefreshAllowed(user);

        // 🔥 Rotate refresh token
        String newRefreshToken = UUID.randomUUID().toString();
        session.getRotatedRefreshTokenHashes().add(refreshTokenHash);
        session.setRefreshTokenHash(hashToken(newRefreshToken));
        session.setExpiryDate(LocalDateTime.now().plusDays(jwtProperties.getRefreshTokenDays()));

        sessionRepository.save(session);

        String newAccessToken = jwtService.generateToken(user.getUserName());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    // 🚪 Logout (single session)
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UnauthorizedException("Refresh token is required");
        }

        UserSession session = sessionRepository.findByRefreshTokenHash(hashToken(refreshToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        session.setRevoked(true);
        sessionRepository.save(session);
    }

    // 🚪 Logout All Devices
    @Transactional
    public void logoutAll(UserEntity user) {
        revokeAllSessions(user);
    }

    @Transactional
    public void logoutAll(String username) {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        logoutAll(user);
    }

    @Transactional
    @Scheduled(cron = "${app.security.session-cleanup-cron:0 0 1 * * *}")
    public void purgeExpiredAndRevokedSessions() {
        sessionRepository.deleteByRevokedTrueOrExpiryDateBefore(LocalDateTime.now());
    }

    private UserSession handleRefreshTokenReplayOrInvalid(String refreshTokenHash) {
        UserSession replayedSession = sessionRepository.findByRotatedRefreshTokenHash(refreshTokenHash)
                .orElse(null);
        if (replayedSession == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        revokeAllSessions(replayedSession.getUser());
        throw new UnauthorizedException("Refresh token reuse detected. All sessions revoked");
    }

    private void ensureSessionRefreshAllowed(UserEntity user) {
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("Account is locked");
        }
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new UnauthorizedException("Account is temporarily locked until " + user.getLockUntil());
        }
        if (user.getAccountExpiryDate() != null && user.getAccountExpiryDate().isBefore(LocalDate.now())) {
            throw new UnauthorizedException("Account has expired");
        }
        if (user.getPasswordExpiryDate() != null && user.getPasswordExpiryDate().isBefore(LocalDate.now())) {
            throw new UnauthorizedException("Password expired. Update your password to continue");
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", ex);
        }
    }

    private void revokeAllSessions(UserEntity user) {
        List<UserSession> sessions = sessionRepository.findByUser(user);
        sessions.forEach(session -> session.setRevoked(true));
        sessionRepository.saveAll(sessions);
    }
}
