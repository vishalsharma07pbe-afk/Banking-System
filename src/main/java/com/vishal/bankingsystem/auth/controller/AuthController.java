package com.vishal.bankingsystem.auth.controller;

import com.vishal.bankingsystem.auth.dto.AuthResponse;
import com.vishal.bankingsystem.auth.dto.ChangePasswordRequest;
import com.vishal.bankingsystem.auth.dto.CustomerSignupRequest;
import com.vishal.bankingsystem.auth.dto.LoginRequest;
import com.vishal.bankingsystem.auth.dto.LogoutRequest;
import com.vishal.bankingsystem.auth.dto.RefreshTokenRequest;
import com.vishal.bankingsystem.auth.dto.UnlockUserRequest;
import com.vishal.bankingsystem.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody CustomerSignupRequest request,
                                               HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.registerCustomer(
                request,
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        ));
    }

    @PostMapping("/sessions")
    public ResponseEntity<AuthResponse> createSession(@RequestBody LoginRequest request,
                                                      HttpServletRequest httpRequest){
        return ResponseEntity.ok(authService.login(
                request,
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent")
        ));
    }

    @PostMapping("/sessions/refresh")
    public ResponseEntity<AuthResponse> refreshSession(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshSession(request.getRefreshToken()));
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/sessions/all")
    public ResponseEntity<Void> logoutAll(Authentication authentication) {
        authService.logoutAll(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }

    @PreAuthorize("hasAuthority('UNLOCK_ACCOUNT')")
    @PostMapping("/users/unlock")
    public ResponseEntity<String> unlockUser(@RequestBody UnlockUserRequest request) {
        return ResponseEntity.ok(authService.unlockUser(request.getUserName()));
    }
}
