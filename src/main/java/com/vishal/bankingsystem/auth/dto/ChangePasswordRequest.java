package com.vishal.bankingsystem.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    private String userName;
    private String currentPassword;
    private String newPassword;
}
