package com.vishal.bankingsystem.auth.controller;

import com.vishal.bankingsystem.auth.dto.ChangePasswordRequest;
import com.vishal.bankingsystem.auth.dto.LoginRequest;
import com.vishal.bankingsystem.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/sessions")
    public ResponseEntity<String> createSession(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePassword(request));
    }
}
