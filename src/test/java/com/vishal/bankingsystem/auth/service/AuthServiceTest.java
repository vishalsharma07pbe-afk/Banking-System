package com.vishal.bankingsystem.auth.service;

import com.vishal.bankingsystem.auth.dto.AuthResponse;
import com.vishal.bankingsystem.auth.dto.CustomerSignupRequest;
import com.vishal.bankingsystem.auth.dto.LoginRequest;
import com.vishal.bankingsystem.auth.entity.RoleEntity;
import com.vishal.bankingsystem.auth.entity.UserEntity;
import com.vishal.bankingsystem.auth.repository.RoleRepository;
import com.vishal.bankingsystem.auth.repository.UserRepository;
import com.vishal.bankingsystem.auth.repository.UserSessionRepository;
import com.vishal.bankingsystem.config.SecurityPolicyProperties;
import com.vishal.bankingsystem.customer.entity.Customer;
import com.vishal.bankingsystem.customer.repository.CustomerRepository;
import com.vishal.bankingsystem.exception.ConflictException;
import com.vishal.bankingsystem.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserSessionRepository userSessionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityPolicyProperties securityPolicyProperties;

    @BeforeEach
    void setUp() {
        userSessionRepository.deleteAll();
        userRepository.deleteAll();
        customerRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void registerCustomerCreatesLoginOnlyUserAndSession() {
        AuthResponse response = authService.registerCustomer(signupRequest("fresh-user", "fresh@example.com"), "127.0.0.1", "test");

        UserEntity user = userRepository.findByUserName("fresh-user").orElseThrow();
        Customer customer = customerRepository.findById(user.getCustomer().getCustomerId()).orElseThrow();
        RoleEntity role = user.getRoles().stream().findFirst().orElseThrow();

        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertNotNull(customer);
        assertTrue(customer.getCustomerNumber().startsWith("CUST-"));
        assertEquals("PRECUSTOMER", role.getName());
        assertEquals(1, role.getPermissions().size());
        assertTrue(role.getPermissions().stream().anyMatch(permission -> "CREATE_ACCOUNT".equals(permission.getName())));
        assertFalse(userSessionRepository.findByUser(user).isEmpty());
    }

    @Test
    void registerCustomerRejectsDuplicateUsername() {
        authService.registerCustomer(signupRequest("fresh-user", "fresh@example.com"), "127.0.0.1", "test");

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> authService.registerCustomer(signupRequest("fresh-user", "other@example.com"), "127.0.0.1", "test")
        );

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void loginAppliesTemporaryLockAtFailureThreshold() {
        UserEntity user = saveUser("temp-lock-user", "Correct@123");
        user.setFailedLoginAttempts(securityPolicyProperties.getMaxFailedLoginAttempts() - 1);
        userRepository.save(user);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(loginRequest("temp-lock-user", "wrong-password"), "127.0.0.1", "test")
        );

        UserEntity updatedUser = userRepository.findByUserName("temp-lock-user").orElseThrow();
        assertTrue(exception.getMessage().startsWith("Account is temporarily locked until "));
        assertNotNull(updatedUser.getLockUntil());
        assertTrue(updatedUser.isPostLockChallengeRequired());
        assertFalse(updatedUser.isAdminUnlockRequired());
    }

    @Test
    void loginEscalatesToAdminUnlockAfterPostLockFailure() {
        UserEntity user = saveUser("admin-lock-user", "Correct@123");
        user.setPostLockChallengeRequired(true);
        user.setLockUntil(LocalDateTime.now().minusMinutes(1));
        userRepository.save(user);

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> authService.login(loginRequest("admin-lock-user", "wrong-password"), "127.0.0.1", "test")
        );

        UserEntity updatedUser = userRepository.findByUserName("admin-lock-user").orElseThrow();
        assertEquals("Account is locked until an admin unlocks it", exception.getMessage());
        assertTrue(updatedUser.isAdminUnlockRequired());
        assertTrue(updatedUser.isPostLockChallengeRequired());
        assertEquals(securityPolicyProperties.getMaxFailedLoginAttempts(), updatedUser.getFailedLoginAttempts());
        assertFalse(updatedUser.getLockUntil() != null && updatedUser.getLockUntil().isAfter(LocalDateTime.now()));
    }

    @Test
    void loginClearsEscalationStateAfterSuccessfulRetryPostLock() {
        UserEntity user = saveUser("recover-user", "Correct@123");
        user.setPostLockChallengeRequired(true);
        user.setLockUntil(LocalDateTime.now().minusMinutes(1));
        userRepository.save(user);

        AuthResponse response = authService.login(loginRequest("recover-user", "Correct@123"), "127.0.0.1", "test");

        UserEntity updatedUser = userRepository.findByUserName("recover-user").orElseThrow();
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertFalse(updatedUser.isPostLockChallengeRequired());
        assertFalse(updatedUser.isAdminUnlockRequired());
        assertEquals(0, updatedUser.getFailedLoginAttempts());
        assertEquals(null, updatedUser.getLockUntil());
    }

    private UserEntity saveUser(String userName, String rawPassword) {
        Customer customer = new Customer();
        customer.setCustomerNumber("CUST-" + userName);
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setEmail(userName + "@example.com");
        customer.setPhoneNumber("9999999999");
        customer.setAddressLine1("Address 1");
        customer.setCity("City");
        customer.setState("State");
        customer.setCountry("India");
        customer.setPostalCode("123456");
        customerRepository.save(customer);

        UserEntity user = new UserEntity();
        user.setUserName(userName);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        user.setPostLockChallengeRequired(false);
        user.setAdminUnlockRequired(false);
        user.setAccountExpiryDate(LocalDate.now().plusDays(30));
        user.setPasswordChangedAt(LocalDate.now());
        user.setPasswordExpiryDate(LocalDate.now().plusDays(30));
        user.setCustomer(customer);

        return userRepository.save(user);
    }

    private static LoginRequest loginRequest(String userName, String password) {
        LoginRequest request = new LoginRequest();
        request.setUserName(userName);
        request.setPassword(password);
        return request;
    }

    private static CustomerSignupRequest signupRequest(String userName, String email) {
        CustomerSignupRequest request = new CustomerSignupRequest();
        request.setUserName(userName);
        request.setPassword("Correct@123");
        request.setFirstName("Fresh");
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
