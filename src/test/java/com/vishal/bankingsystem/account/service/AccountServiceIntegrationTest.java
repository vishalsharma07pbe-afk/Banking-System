package com.vishal.bankingsystem.account.service;

import com.vishal.bankingsystem.account.dto.AccountDto;
import com.vishal.bankingsystem.account.enums.AccountStatus;
import com.vishal.bankingsystem.account.enums.AccountType;
import com.vishal.bankingsystem.account.repository.AccountRepository;
import com.vishal.bankingsystem.auth.dto.AuthResponse;
import com.vishal.bankingsystem.auth.dto.CustomerSignupRequest;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.repository.RoleRepository;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.auth.repository.UserSessionRepository;
import com.vishal.bankingsystem.auth.service.AuthService;
import com.vishal.bankingsystem.branch.entity.Branch;
import com.vishal.bankingsystem.branch.repository.BranchRepository;
import com.vishal.bankingsystem.customer.repository.CustomerRepository;
import com.vishal.bankingsystem.exception.ConflictException;
import com.vishal.bankingsystem.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        accountRepository.deleteAll();
        userSessionRepository.deleteAll();
        userRepository.deleteAll();
        customerRepository.deleteAll();
        branchRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createAccountPromotesPreCustomerAndBlocksDuplicateAccountType() {
        AuthResponse response = authService.registerCustomer(signupRequest("precustomer-user", "precustomer@example.com"), "127.0.0.1", "test");
        UserEntity user = userRepository.findByUserName("precustomer-user").orElseThrow();
        Branch branch = saveBranch("001");

        authenticate(user.getUserName());

        AccountDto createdAccount = accountService.createAccount(accountRequest(
                "ACC-1001",
                AccountType.SAVINGS,
                user.getCustomer().getCustomerId(),
                branch.getBranchId()
        ));

        UserEntity updatedUser = userRepository.findByUserName("precustomer-user").orElseThrow();
        assertEquals("ACC-1001", createdAccount.getAccountNumber());
        assertTrue(updatedUser.getRoles().stream().anyMatch(role -> "CUSTOMER".equals(role.getName())));
        assertTrue(updatedUser.getRoles().stream().noneMatch(role -> "PRECUSTOMER".equals(role.getName())));
        assertTrue(response.getAccessToken() != null && response.getRefreshToken() != null);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> accountService.createAccount(accountRequest(
                        "ACC-1002",
                        AccountType.SAVINGS,
                        user.getCustomer().getCustomerId(),
                        branch.getBranchId()
                ))
        );

        assertEquals("Customer already has a SAVINGS account", exception.getMessage());
    }

    @Test
    void createAccountRejectsAnotherCustomersIdForSelfServiceUser() {
        authService.registerCustomer(signupRequest("owner-user", "owner@example.com"), "127.0.0.1", "test");
        authService.registerCustomer(signupRequest("other-user", "other@example.com"), "127.0.0.1", "test");
        UserEntity ownerUser = userRepository.findByUserName("owner-user").orElseThrow();
        UserEntity otherUser = userRepository.findByUserName("other-user").orElseThrow();
        Branch branch = saveBranch("002");

        authenticate(ownerUser.getUserName());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> accountService.createAccount(accountRequest(
                        "ACC-2001",
                        AccountType.CURRENT,
                        otherUser.getCustomer().getCustomerId(),
                        branch.getBranchId()
                ))
        );

        assertEquals("You can create accounts only for your own customer profile", exception.getMessage());
    }

    private void authenticate(String userName) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userName, null, AuthorityUtils.NO_AUTHORITIES)
        );
    }

    private Branch saveBranch(String suffix) {
        Branch branch = new Branch();
        branch.setBranchCode("BR-" + suffix);
        branch.setBranchName("Main Branch " + suffix);
        branch.setIfscCode("IFSC000" + suffix);
        branch.setPhoneNumber("99999999" + suffix);
        branch.setEmail("branch" + suffix + "@example.com");
        branch.setAddressLine1("Address 1");
        branch.setCity("City");
        branch.setState("State");
        branch.setCountry("India");
        branch.setPostalCode("123456");
        return branchRepository.save(branch);
    }

    private AccountDto accountRequest(String accountNumber, AccountType accountType, Long customerId, Long branchId) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(accountNumber);
        accountDto.setAccountType(accountType);
        accountDto.setBalance(BigDecimal.ZERO);
        accountDto.setStatus(AccountStatus.ACTIVE);
        accountDto.setCustomerId(customerId);
        accountDto.setBranchId(branchId);
        return accountDto;
    }

    private CustomerSignupRequest signupRequest(String userName, String email) {
        CustomerSignupRequest request = new CustomerSignupRequest();
        request.setUserName(userName);
        request.setPassword("Correct@123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setEmail(email);
        request.setPhoneNumber("9999999999");
        request.setAddressLine1("Address 1");
        request.setCity("City");
        request.setState("State");
        request.setCountry("India");
        request.setPostalCode("123456");
        return request;
    }
}
