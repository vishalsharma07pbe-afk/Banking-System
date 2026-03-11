package com.vishal.bankingsystem.auth.controller;

import com.vishal.bankingsystem.auth.dto.ChangePasswordRequest;
import com.vishal.bankingsystem.auth.dto.LoginRequest;
import com.vishal.bankingsystem.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request);
    }
}
