package com.vishal.bankingsystem.auth.service;

import com.vishal.bankingsystem.auth.dto.ChangePasswordRequest;
import com.vishal.bankingsystem.auth.dto.LoginRequest;
import com.vishal.bankingsystem.auth.entity.UsersEntity;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.exception.BadRequestException;
import com.vishal.bankingsystem.exception.UnauthorizedException;
import com.vishal.bankingsystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.security.password-expiry-months:6}")
    private long passwordExpiryMonths;

    @Value("${app.security.max-failed-login-attempts:5}")
    private int maxFailedLoginAttempts;

    @Value("${app.security.lock-duration-minutes:30}")
    private long lockDurationMinutes;

    @Transactional
    public String login(LoginRequest request) {
        String username = normalize(request.getUserName());
        UsersEntity user = getUser(username);

        refreshAutomaticState(user);
        user = userAccountStateService.syncUserState(username);
        if (user == null) {
            throw new UnauthorizedException("Invalid username or password");
        }
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
        return jwtService.generateToken(username);
    }

    @Transactional
    public String changePassword(ChangePasswordRequest request) {
        String username = normalize(request.getUserName());
        UsersEntity user = getUser(username);

        refreshAutomaticState(user);
        user = userAccountStateService.syncUserState(username);
        if (user == null) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
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
        user.setPasswordExpiryDate(today.plusMonths(passwordExpiryMonths));
        clearLoginFailures(user);

        return "Password updated successfully";
    }

    private UsersEntity getUser(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));
    }

    private String normalize(String userName) {
        return userName == null ? "" : userName.trim();
    }

    private void ensureLoginAllowed(UsersEntity user) {
        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled");
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

    private void refreshAutomaticState(UsersEntity user) {
        if (user.getLockUntil() != null && !user.getLockUntil().isAfter(LocalDateTime.now())) {
            user.setLockUntil(null);
            user.setFailedLoginAttempts(0);
        }
    }

    private void registerFailedAttempt(UsersEntity user) {
        int failedAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(failedAttempts);

        if (failedAttempts >= maxFailedLoginAttempts) {
            user.setLockUntil(LocalDateTime.now().plusMinutes(lockDurationMinutes));
        }
    }

    private void clearLoginFailures(UsersEntity user) {
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
    }

    private boolean isTemporarilyLocked(UsersEntity user) {
        return user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now());
    }

    private boolean isAccountExpired(UsersEntity user) {
        return user.getAccountExpiryDate() != null && user.getAccountExpiryDate().isBefore(LocalDate.now());
    }

    private boolean isPasswordExpired(UsersEntity user) {
        return user.getPasswordExpiryDate() != null && user.getPasswordExpiryDate().isBefore(LocalDate.now());
    }

    private String buildInvalidCredentialsMessage(UsersEntity user) {
        if (isTemporarilyLocked(user)) {
            return "Account is temporarily locked until " + user.getLockUntil();
        }

        int remainingAttempts = Math.max(0, maxFailedLoginAttempts - user.getFailedLoginAttempts());
        return "Invalid username or password. Remaining attempts: " + remainingAttempts;
    }

    private String resolveAuthenticationMessage(UsersEntity user, AuthenticationException ex) {
        if (isTemporarilyLocked(user)) {
            return "Account is temporarily locked until " + user.getLockUntil();
        }
        if (isPasswordExpired(user)) {
            return "Password expired. Update your password to continue";
        }
        return ex.getMessage();
    }
}
