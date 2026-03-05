package com.vishal.bankingsystem.account.service;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.dto.AccountDto;
import com.vishal.bankingsystem.account.entity.AccountEntity;
import com.vishal.bankingsystem.account.repository.AccountRepository;
import com.vishal.bankingsystem.account.mapper.AccountMapper;
import com.vishal.bankingsystem.branch.entity.Branch;
import com.vishal.bankingsystem.branch.repository.BranchRepository;
import com.vishal.bankingsystem.customer.entity.Customer;
import com.vishal.bankingsystem.exception.BadRequestException;
import com.vishal.bankingsystem.exception.ConflictException;
import com.vishal.bankingsystem.exception.ResourceNotFoundException;
import com.vishal.bankingsystem.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import com.vishal.bankingsystem.customer.repository.CustomerRepository;
import org.springframework.transaction.annotation.Transactional;
import com.vishal.bankingsystem.transaction.entity.Transaction;
import com.vishal.bankingsystem.transaction.enums.TransactionType;
import com.vishal.bankingsystem.transaction.enums.TransactionStatus;
import com.vishal.bankingsystem.transaction.dto.TransactionDto;
import com.vishal.bankingsystem.transaction.mapper.TransactionMapper;
import com.vishal.bankingsystem.transaction.request.TransferRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService{
    public final AccountRepository accountRepository;
    public final CustomerRepository customerRepository;
    public final BranchRepository branchRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, BranchRepository branchRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.branchRepository = branchRepository;
        this.transactionRepository = transactionRepository;
    }

    private String generateReferenceNumber() {
        return "TXN-" + System.currentTimeMillis();
    }

    private void createTransaction(TransactionType type,
                                   BigDecimal amount,
                                   TransactionStatus status,
                                   AccountEntity fromAccount,
                                   AccountEntity toAccount) {
        Transaction transaction = new Transaction(
                generateReferenceNumber(),
                type,
                amount,
                status,
                fromAccount,
                toAccount
        );
        transactionRepository.save(transaction);
    }

    private void createFailedTransaction(TransactionType type,
                                         BigDecimal amount,
                                         AccountEntity fromAccount,
                                         AccountEntity toAccount) {
        createTransaction(type, amount, TransactionStatus.FAILED, fromAccount, toAccount);
    }

    private void createSuccessTransaction(TransactionType type,
                                          BigDecimal amount,
                                          AccountEntity fromAccount,
                                          AccountEntity toAccount) {
        createTransaction(type, amount, TransactionStatus.SUCCESS, fromAccount, toAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found for id: " + id));
        return AccountMapper.entityToDto(account);
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Customer customer = customerRepository.findById(accountDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Branch branch = branchRepository.findById(accountDto.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));
        AccountEntity account = AccountMapper.dtoToEntity(accountDto);
        account.setCustomer(customer);
        account.setBranch(branch);
        account.setStatus(AccountStatus.ACTIVE); // default
        AccountEntity savedAccount = accountRepository.save(account);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Override
    public void deleteAccount(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    @Override
    public AccountDto updateAccount(String accountNumber, AccountDto accountDto) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setAccountType(accountDto.getAccountType());
        account.setStatus(accountDto.getStatus());
        AccountEntity savedAccount = accountRepository.save(account);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Override
    public AccountDto getAccountByNumber(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()->
                new ResourceNotFoundException("Account not found"));
        return AccountMapper.entityToDto(account);
    }

    @Override
    public List<AccountDto> findByStatus(AccountStatus status) {
        List<AccountEntity> listOfAccounts = accountRepository.findByStatus(status);
        return listOfAccounts.stream().map(AccountMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> findByBalanceBetween(BigDecimal min, BigDecimal max) {
        List<AccountEntity> listOfAccounts = accountRepository.findByBalanceBetween(min,max);
        return listOfAccounts.stream().map(AccountMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public List<AccountDto> findByCustomerEmail(String email) {
        List<AccountEntity> account = accountRepository.findByCustomerEmail(email);
        return account.stream().map(AccountMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    public AccountDto closeAccount(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new ConflictException("Account is already closed");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Cannot close account with remaining balance");
        }
        account.setStatus(AccountStatus.CLOSED);
        AccountEntity savedAccount = accountRepository.save(account);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Override
    public AccountDto blockAccount(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Closed account cannot be blocked");
        }
        account.setStatus(AccountStatus.BLOCKED);
        AccountEntity savedAccount = accountRepository.save(account);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Override
    public AccountDto activateAccount(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Closed account cannot be activated");
        }
        account.setStatus(AccountStatus.ACTIVE);
        AccountEntity savedAccount = accountRepository.save(account);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Transactional
    @Override
    public AccountDto deposit(String accountNumber, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (amount.compareTo(BigDecimal.ZERO) <=0) {
            throw new BadRequestException("Amount must be greater than 0");
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            createFailedTransaction(TransactionType.DEPOSIT, amount, null, account);
            throw new BadRequestException("Deposit is not allowed for a closed account");
        }
        if (account.getStatus() == AccountStatus.BLOCKED) {
            createFailedTransaction(TransactionType.DEPOSIT, amount, null, account);
            throw new BadRequestException("Deposit is not allowed for a blocked account");
        }
        account.setBalance(account.getBalance().add(amount));
        AccountEntity savedAccount = accountRepository.save(account);
        createSuccessTransaction(TransactionType.DEPOSIT, amount, null, account);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Transactional
    @Override
    public AccountDto withdraw(String accountNumber, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        if (amount.compareTo(BigDecimal.ZERO) <=0) {
            createFailedTransaction(TransactionType.WITHDRAW, amount, account, null);
            throw new BadRequestException("Amount must be greater than 0");
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            createFailedTransaction(TransactionType.WITHDRAW, amount, account, null);
            throw new BadRequestException("Withdrawal is not allowed for a closed account");
        }
        if (account.getStatus() == AccountStatus.BLOCKED) {
            createFailedTransaction(TransactionType.WITHDRAW, amount, account, null);
            throw new BadRequestException("Withdrawal is not allowed for a blocked account");
        }
        if(account.getBalance().compareTo(amount) < 0){
            createFailedTransaction(TransactionType.WITHDRAW, amount, account, null);
            throw new BadRequestException("Insufficient balance for withdrawal");
        }
        account.setBalance(account.getBalance().subtract(amount));
        AccountEntity savedAccount = accountRepository.save(account);
        createSuccessTransaction(TransactionType.WITHDRAW, amount, account, null);
        return AccountMapper.entityToDto(savedAccount);
    }

    @Override
    public List<TransactionDto>  transactionHistory(String accountNumber) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(account, account);
        return transactions.stream().map(TransactionMapper::entityToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void transfer(TransferRequest request) {
        if (request.getTransferAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BadRequestException("Amount must be greater than 0");
        }
        AccountEntity fromAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        AccountEntity toAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE ||
                toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Both accounts must be active");
        }
        if (fromAccount.getBalance().compareTo(request.getTransferAmount()) < 0) {
            createFailedTransaction(
                    TransactionType.TRANSFER,
                    request.getTransferAmount(),
                    fromAccount,
                    toAccount
            );
            throw new BadRequestException("Insufficient balance for transfer");
        }
        // Deduct from source
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getTransferAmount()));

        // Add to destination
        toAccount.setBalance(toAccount.getBalance().add(request.getTransferAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        createSuccessTransaction(
                TransactionType.TRANSFER,
                request.getTransferAmount(),
                fromAccount,
                toAccount
        );
    }
}
