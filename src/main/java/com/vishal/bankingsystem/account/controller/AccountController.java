package com.vishal.bankingsystem.account.controller;
import com.vishal.bankingsystem.account.dto.AccountDto;
import com.vishal.bankingsystem.account.dto.AccountStatusUpdateRequest;
import com.vishal.bankingsystem.account.dto.AmountRequest;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.service.AccountService;
import com.vishal.bankingsystem.exception.BadRequestException;
import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.request.TransferRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasAuthority('VIEW_ACCOUNT')")
    @GetMapping("/internal/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id){
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    @PreAuthorize("hasAuthority('CREATE_ACCOUNT')")
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountDto accountDto){
        AccountDto createdAccount = accountService.createAccount(accountDto);
        return ResponseEntity.ok()
                .header("X-Status-Message","Account Created Successfully")
                .body(createdAccount);
    }

    @PreAuthorize("hasAuthority('FREEZE_ACCOUNT')")
    @PatchMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable String accountNumber, @Valid @RequestBody AccountDto accountDto){
        AccountDto updatedAccount = accountService.updateAccount(accountNumber, accountDto);
        return ResponseEntity.ok()
                .header("X-Status-Message", "Updated Successfully")
                .body(updatedAccount);
    }

    @PreAuthorize("hasAuthority('VIEW_ACCOUNT')")
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber){
        AccountDto account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok()
                .header("X-Status-Message", "Account Fetched")
                .body(account);
    }

    @PreAuthorize("hasAuthority('VIEW_ACCOUNT')")
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAccounts(
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance) {
        if (status != null) {
            return ResponseEntity.ok(accountService.getAccountsByStatus(status));
        }
        if (customerId != null) {
            return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId));
        }
        if (minBalance != null && maxBalance != null) {
            return ResponseEntity.ok(accountService.getAccountsByBalanceRange(minBalance, maxBalance));
        }
        throw new BadRequestException("Provide status, customerId, or both minBalance and maxBalance");
    }

    @PreAuthorize("hasAnyAuthority('FREEZE_ACCOUNT', 'UNFREEZE_ACCOUNT')")
    @PatchMapping("/{accountNumber}/status")
    public ResponseEntity<AccountDto> updateAccountStatus(@PathVariable String accountNumber,
                                                          @Valid @RequestBody AccountStatusUpdateRequest request) {
        return ResponseEntity.ok(switch (request.getStatus()) {
            case CLOSED -> accountService.closeAccount(accountNumber);
            case BLOCKED -> accountService.blockAccount(accountNumber);
            case ACTIVE -> accountService.activateAccount(accountNumber);
        });
    }

    @PreAuthorize("hasAuthority('DEPOSIT')")
    @PostMapping("/{accountNumber}/deposits")
    public ResponseEntity<AccountDto> deposit(@PathVariable String accountNumber,
                                              @Valid @RequestBody AmountRequest request){
        return ResponseEntity.ok(accountService.deposit(accountNumber, request.getAmount()));
    }

    @PreAuthorize("hasAuthority('WITHDRAW')")
    @PostMapping("/{accountNumber}/withdrawals")
    public ResponseEntity<AccountDto> withdraw(@PathVariable String accountNumber,
                                               @Valid @RequestBody AmountRequest request){
        return ResponseEntity.ok(accountService.withdraw(accountNumber, request.getAmount()));
    }

    @PreAuthorize("hasAuthority('TRANSFER')")
    @PostMapping("/transfers")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request) {

        accountService.transferFunds(request);

        return ResponseEntity.ok("Transfer completed successfully");
    }

    @PreAuthorize("hasAuthority('VIEW_TRANSACTION_HISTORY')")
    @GetMapping("/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionDto> > getAccountTransactions(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountService.getAccountTransactions(accountNumber));
    }
}
