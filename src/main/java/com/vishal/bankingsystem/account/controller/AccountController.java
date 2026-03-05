package com.vishal.bankingsystem.account.controller;
import com.vishal.bankingsystem.account.dto.AccountDto;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.service.AccountService;
import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.request.TransferRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id){
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto){
        AccountDto createdAccount = accountService.createAccount(accountDto);
        return ResponseEntity.ok()
                .header("X-Status-Message","Account Created Successfully")
                .body(createdAccount);
    }

    @DeleteMapping("/number/{accountNumber}")
    public ResponseEntity<String> deleteAccount(@PathVariable String accountNumber){
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.ok("Account closed Successfully");
    }

    @PatchMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDto> updateAccount(@PathVariable String accountNumber, @RequestBody AccountDto accountDto){
        AccountDto updatedAccount = accountService.updateAccount(accountNumber, accountDto);
        return ResponseEntity.ok()
                .header("X-Status-Message", "Updated Successfully")
                .body(updatedAccount);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountDto> getAccountByAccountNumber(@PathVariable String accountNumber){
        AccountDto account = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok()
                .header("X-Status-Message", "Account Fetched")
                .body(account);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountDto>> getAccountsByStatus(@PathVariable AccountStatus status){
        List<AccountDto> accounts = accountService.findByStatus(status);
        return ResponseEntity.ok()
                .header("X-Status-Message", "Account Fetched")
                .body(accounts);
    }

    @GetMapping("/balance")
    public ResponseEntity<List<AccountDto>> getAccountsByBalanceRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        List<AccountDto> accounts = accountService.findByBalanceBetween(min, max);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<AccountDto>> getAccountsByEmail(@PathVariable String email){
        List<AccountDto> accounts = accountService.findByCustomerEmail(email);
        return ResponseEntity.ok()
                .header("X-Status-Message", "Account Fetched")
                .body(accounts);
    }

    @PostMapping("/number/{accountNumber}/close")
    public ResponseEntity<AccountDto> closeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }

    @PostMapping("/number/{accountNumber}/block")
    public ResponseEntity<AccountDto> blockAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.blockAccount(accountNumber));
    }

    @PostMapping("/number/{accountNumber}/activate")
    public ResponseEntity<AccountDto> activateAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.activateAccount(accountNumber));
    }

    @PostMapping("/number/{accountNumber}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable String accountNumber, @RequestParam BigDecimal amount){
        return ResponseEntity.ok(accountService.deposit(accountNumber, amount));
    }

    @PostMapping("/number/{accountNumber}/withdraw")
    public ResponseEntity<AccountDto> withdraw(@PathVariable String accountNumber, @RequestParam BigDecimal amount){
        return ResponseEntity.ok(accountService.withdraw(accountNumber, amount));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {

        accountService.transfer(request);

        return ResponseEntity.ok("Transfer completed successfully");
    }

    @PostMapping("/number/{accountNumber}/transactionhistory")
    public ResponseEntity<List<TransactionDto> > transactionHistory(@PathVariable String accountNumber){
        return ResponseEntity.ok(accountService.transactionHistory(accountNumber));
    }
}
