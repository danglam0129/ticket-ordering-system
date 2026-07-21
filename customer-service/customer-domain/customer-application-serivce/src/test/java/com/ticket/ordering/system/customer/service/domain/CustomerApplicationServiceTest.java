package com.ticket.ordering.system.customer.service.domain;

import com.ticket.ordering.system.customer.service.domain.dto.CustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.AuthenticationResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.LoginCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.login.LogoutCommand;
import com.ticket.ordering.system.customer.service.domain.dto.login.LogoutResponse;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.token.RefreshTokenCommand;
import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.customer.service.domain.entity.RefreshToken;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerApplicationServiceException;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerAuthenticationException;
import com.ticket.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.RefreshTokenRepository;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.JwtTokenService;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.PasswordHashingService;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.RefreshTokenSecurityService;
import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import com.ticket.ordering.system.customer.service.domain.valueobject.RefreshTokenId;
import com.ticket.ordering.system.domain.valueobject.CustomerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CustomerTestConfiguration.class)
class CustomerApplicationServiceTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private static final String USERNAME = "john.doe";
    private static final String RAW_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";
    private static final String REFRESH_TOKEN_HASH = "refresh-token-hash";
    private static final String NEW_REFRESH_TOKEN = "new-refresh-token";
    private static final String NEW_REFRESH_TOKEN_HASH = "new-refresh-token-hash";

    @Autowired
    private CustomerApplicationService customerApplicationService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordHashingService passwordHashingService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private RefreshTokenSecurityService refreshTokenSecurityService;

    @BeforeEach
    void init() {
        reset(customerRepository,
                refreshTokenRepository,
                passwordHashingService,
                jwtTokenService,
                refreshTokenSecurityService);

        when(passwordHashingService.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(passwordHashingService.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtTokenService.generateAccessToken(any(Customer.class), any(Instant.class), any(Instant.class)))
                .thenReturn(ACCESS_TOKEN);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testRegisterCustomer() {
        when(customerRepository.existsByUsername(USERNAME)).thenReturn(false);

        RegisterCustomerResponse response = customerApplicationService.registerCustomer(registerCustomerCommand());

        assertNotNull(response.getCustomerId());
        assertEquals(USERNAME, response.getUsername());
        assertEquals(CustomerRole.CUSTOMER, response.getRole());
        assertEquals("Customer registered successfully", response.getMessage());
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerRepository).save(customerCaptor.capture());
        assertNotNull(customerCaptor.getValue().getId());
        assertEquals(ENCODED_PASSWORD, customerCaptor.getValue().getPassword());
    }

    @Test
    void testRegisterCustomerWithExistingUsername() {
        when(customerRepository.existsByUsername(USERNAME)).thenReturn(true);

        CustomerApplicationServiceException exception = assertThrows(CustomerApplicationServiceException.class,
                () -> customerApplicationService.registerCustomer(registerCustomerCommand()));

        assertEquals("Customer with username john.doe already exists!", exception.getMessage());
        verify(passwordHashingService, never()).encode(RAW_PASSWORD);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testLoginCustomer() {
        when(customerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(customer()));
        when(refreshTokenSecurityService.generateToken()).thenReturn(REFRESH_TOKEN);
        when(refreshTokenSecurityService.hash(REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);

        AuthenticationResponse response = customerApplicationService.login(loginCustomerCommand());

        assertEquals(CUSTOMER_ID, response.getCustomerId());
        assertEquals(USERNAME, response.getUsername());
        assertEquals(CustomerRole.CUSTOMER, response.getRole());
        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(REFRESH_TOKEN, response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());
        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertEquals(new CustomerId(CUSTOMER_ID), refreshTokenCaptor.getValue().getCustomerId());
        assertEquals(REFRESH_TOKEN_HASH, refreshTokenCaptor.getValue().getTokenHash());
        assertFalse(refreshTokenCaptor.getValue().isRevoked());
    }

    @Test
    void testLoginCustomerWithInvalidPassword() {
        when(customerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(customer()));
        when(passwordHashingService.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        CustomerAuthenticationException exception = assertThrows(CustomerAuthenticationException.class,
                () -> customerApplicationService.login(loginCustomerCommand()));

        assertEquals("Username or password is invalid!", exception.getMessage());
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
        verify(jwtTokenService, never()).generateAccessToken(any(Customer.class), any(Instant.class), any(Instant.class));
    }

    @Test
    void testRefreshTokenRotatesRefreshToken() {
        CustomerId customerId = new CustomerId(CUSTOMER_ID);
        RefreshToken currentRefreshToken = refreshToken(customerId, REFRESH_TOKEN_HASH, Instant.now().plusSeconds(3600));
        when(refreshTokenSecurityService.hash(REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(currentRefreshToken));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer()));
        when(refreshTokenSecurityService.generateToken()).thenReturn(NEW_REFRESH_TOKEN);
        when(refreshTokenSecurityService.hash(NEW_REFRESH_TOKEN)).thenReturn(NEW_REFRESH_TOKEN_HASH);

        AuthenticationResponse response = customerApplicationService.refreshToken(RefreshTokenCommand.builder()
                .refreshToken(REFRESH_TOKEN)
                .build());

        assertEquals(ACCESS_TOKEN, response.getAccessToken());
        assertEquals(NEW_REFRESH_TOKEN, response.getRefreshToken());
        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(refreshTokenCaptor.capture());
        assertTrue(refreshTokenCaptor.getAllValues().get(0).isRevoked());
        assertEquals(NEW_REFRESH_TOKEN_HASH, refreshTokenCaptor.getAllValues().get(1).getTokenHash());
        assertFalse(refreshTokenCaptor.getAllValues().get(1).isRevoked());
    }

    @Test
    void testLogoutRevokesExistingRefreshToken() {
        RefreshToken currentRefreshToken = refreshToken(new CustomerId(CUSTOMER_ID),
                REFRESH_TOKEN_HASH,
                Instant.now().plusSeconds(3600));
        when(refreshTokenSecurityService.hash(REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(currentRefreshToken));

        LogoutResponse response = customerApplicationService.logout(LogoutCommand.builder()
                .refreshToken(REFRESH_TOKEN)
                .build());

        assertEquals("Customer logged out successfully", response.getMessage());
        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertTrue(refreshTokenCaptor.getValue().isRevoked());
    }

    @Test
    void testFindCustomer() {
        when(customerRepository.findById(eq(new CustomerId(CUSTOMER_ID)))).thenReturn(Optional.of(customer()));

        CustomerResponse response = customerApplicationService.findCustomer(CUSTOMER_ID);

        assertEquals(CUSTOMER_ID, response.getCustomerId());
        assertEquals(USERNAME, response.getUsername());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals(CustomerRole.CUSTOMER, response.getRole());
    }

    private RegisterCustomerCommand registerCustomerCommand() {
        return RegisterCustomerCommand.builder()
                .username(" John.Doe ")
                .password(RAW_PASSWORD)
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    private LoginCustomerCommand loginCustomerCommand() {
        return LoginCustomerCommand.builder()
                .username(" John.Doe ")
                .password(RAW_PASSWORD)
                .build();
    }

    private Customer customer() {
        return Customer.builder()
                .customerId(new CustomerId(CUSTOMER_ID))
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .firstName("John")
                .lastName("Doe")
                .role(CustomerRole.CUSTOMER)
                .build();
    }

    private RefreshToken refreshToken(CustomerId customerId, String tokenHash, Instant expiresAt) {
        return RefreshToken.builder()
                .refreshTokenId(new RefreshTokenId(UUID.randomUUID()))
                .customerId(customerId)
                .tokenHash(tokenHash)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
    }
}
