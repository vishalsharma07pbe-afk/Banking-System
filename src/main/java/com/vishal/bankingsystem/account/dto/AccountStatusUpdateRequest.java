package com.vishal.bankingsystem.account.dto;

import com.vishal.bankingsystem.account.enums.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountStatusUpdateRequest {

    @NotNull(message = "Account status is required")
    private AccountStatus status;
}
