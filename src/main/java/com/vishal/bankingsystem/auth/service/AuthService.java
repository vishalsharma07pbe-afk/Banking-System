package com.vishal.bankingsystem.auth.service;

import com.vishal.bankingsystem.auth.dto.AuthResponse;
import com.vishal.bankingsystem.auth.dto.ChangePasswordRequest;
import com.vishal.bankingsystem.auth.dto.LoginRequest;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.entity.UserSession;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.config.SecurityPolicyProperties;
import com.vishal.bankingsystem.exception.BadRequestException;
import com.vishal.bankingsystem.exception.UnauthorizedException;
import com.vishal.bankingsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAccountStateService userAccountStateService;
    private final SecurityPolicyProperties securityPolicyProperties;
    private final SessionService sessionService;

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public AuthResponse login(LoginRequest request, String ipAddress, String deviceInfo) {
        String username = normalize(request.getUserName());
        UserEntity user = getUser(username);

        refreshAutomaticState(user);
        user = syncUserState(user);
        ensureLoginAllowed(user);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            registerFailedAttempt(user);
            throw new UnauthorizedException(buildInvalidCredentialsMessage(user));
        } catch (AuthenticationException ex) {
            throw new UnauthorizedException(resolveAuthenticationMessage(user, ex));
        }

        clearLoginFailures(user);
        String refreshToken = sessionService.createSession(username, ipAddress, deviceInfo);
        return new AuthResponse(jwtService.generateToken(username), refreshToken);
    }

    @Transactional(noRollbackFor = UnauthorizedException.class)
    public String changePassword(ChangePasswordRequest request) {
        String username = normalize(request.getUserName());
        UserEntity user = getUser(username);

        refreshAutomaticState(user);
        user = syncUserState(user);

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        if (user.isAdminUnlockRequired()) {
            throw new UnauthorizedException("Account is locked until an admin unlocks it");
        }
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("Account is locked. Contact support");
        }
        if (isTemporarilyLocked(user)) {
            throw new UnauthorizedException("Account is temporarily locked until " + user.getLockUntil());
        }
        if (isAccountExpired(user)) {
            throw new UnauthorizedException("Account has expired");
        }
        if (request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            throw new BadRequestException("New password must not be blank");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            registerFailedAttempt(user);
            throw new UnauthorizedException(buildInvalidCredentialsMessage(user));
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }

        LocalDate today = LocalDate.now();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangedAt(today);
        user.setPasswordExpiryDate(today.plusMonths(securityPolicyProperties.getPasswordExpiryMonths()));
        clearLoginFailures(user);
        sessionService.logoutAll(user);

        return "Password updated successfully";
    }

    @Transactional
    public AuthResponse refreshSession(String refreshToken) {
        return sessionService.refresh(refreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        sessionService.logout(refreshToken);
    }

    @Transactional
    public void logoutAll(String username) {
        sessionService.logoutAll(username);
    }

    @Transactional
    public String unlockUser(String username) {
        UserEntity user = getUser(normalize(username));
        clearLoginFailures(user);
        user.setAdminUnlockRequired(false);
        return "User unlocked successfully";
    }

    private UserEntity getUser(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
    }

    private String normalize(String userName) {
        return userName == null ? "" : userName.trim();
    }

    private UserEntity syncUserState(UserEntity user) {
        UserEntity syncedUser = userAccountStateService.syncUserState(user.getUserName());
        if (syncedUser == null) {
            throw new UnauthorizedException("Invalid username or password");
        }
        return syncedUser;
    }

    private void ensureLoginAllowed(UserEntity user) {
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        if (user.isAdminUnlockRequired()) {
            throw new UnauthorizedException("Account is locked until an admin unlocks it");
        }
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("Account is locked. Contact support");
        }
        if (isTemporarilyLocked(user)) {
            throw new UnauthorizedException("Account is temporarily locked until " + user.getLockUntil());
        }
        if (isAccountExpired(user)) {
            throw new UnauthorizedException("Account has expired");
        }
        if (isPasswordExpired(user)) {
            throw new UnauthorizedException("Password expired. Update your password to continue");
        }
    }

    private void refreshAutomaticState(UserEntity user) {
        if (user.getLockUntil() != null && !user.getLockUntil().isAfter(LocalDateTime.now())) {
            user.setLockUntil(null);
            user.setFailedLoginAttempts(0);
        }
    }

    private void registerFailedAttempt(UserEntity user) {
        if (user.isPostLockChallengeRequired() && !isTemporarilyLocked(user)) {
            user.setAdminUnlockRequired(true);
            user.setFailedLoginAttempts(securityPolicyProperties.getMaxFailedLoginAttempts());
            user.setLockUntil(null);
            return;
        }

        int failedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(failedAttempts);

        if (failedAttempts >= securityPolicyProperties.getMaxFailedLoginAttempts()) {
            user.setLockUntil(LocalDateTime.now().plusMinutes(securityPolicyProperties.getLockDurationMinutes()));
            user.setPostLockChallengeRequired(true);
        }
    }

    private void clearLoginFailures(UserEntity user) {
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        user.setPostLockChallengeRequired(false);
    }

    private boolean isTemporarilyLocked(UserEntity user) {
        return user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now());
    }

    private boolean isAccountExpired(UserEntity user) {
        return user.getAccountExpiryDate() != null && user.getAccountExpiryDate().isBefore(LocalDate.now());
    }

    private boolean isPasswordExpired(UserEntity user) {
        return user.getPasswordExpiryDate() != null && user.getPasswordExpiryDate().isBefore(LocalDate.now());
    }

    private String buildInvalidCredentialsMessage(UserEntity user) {
        if (user.isAdminUnlockRequired()) {
            return "Account is locked until an admin unlocks it";
        }
        if (isTemporarilyLocked(user)) {
            return "Account is temporarily locked until " + user.getLockUntil();
        }

        int remainingAttempts = Math.max(0,
                securityPolicyProperties.getMaxFailedLoginAttempts() - user.getFailedLoginAttempts());
        return "Invalid username or password. Remaining attempts: " + remainingAttempts;
    }

    private String resolveAuthenticationMessage(UserEntity user, AuthenticationException ex) {
        if (user.isAdminUnlockRequired()) {
            return "Account is locked until an admin unlocks it";
        }
        if (isTemporarilyLocked(user)) {
            return "Account is temporarily locked until " + user.getLockUntil();
        }
        if (isPasswordExpired(user)) {
            return "Password expired. Update your password to continue";
        }
        return ex.getMessage();
    }
}
