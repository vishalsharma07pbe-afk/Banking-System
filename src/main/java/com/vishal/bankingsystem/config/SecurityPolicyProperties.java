package com.vishal.bankingsystem.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.security")
public class SecurityPolicyProperties {

    @Min(1)
    private long passwordExpiryMonths = 6;

    @Min(1)
    private int maxFailedLoginAttempts = 5;

    @Min(1)
    private long lockDurationMinutes = 30;

    @NotBlank
    private String authStateSyncCron = "0 0 * * * *";
}
