package com.ticket.ordering.system.customer.service.domain;

import com.ticket.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
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
import com.ticket.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.ticket.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.RefreshTokenRepository;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.JwtTokenService;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.PasswordHashingService;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.RefreshTokenSecurityService;
import com.ticket.ordering.system.domain.valueobject.CustomerId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@Service
public class CustomerApplicationServiceImpl implements CustomerApplicationService {

    private final CustomerDomainService customerDomainService;
    private final CustomerDataMapper customerDataMapper;
    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordHashingService passwordHashingService;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenSecurityService refreshTokenSecurityService;
    private final CustomerServiceConfigData customerServiceConfigData;

    public CustomerApplicationServiceImpl(CustomerDomainService customerDomainService,
                                          CustomerDataMapper customerDataMapper,
                                          CustomerRepository customerRepository,
                                          RefreshTokenRepository refreshTokenRepository,
                                          PasswordHashingService passwordHashingService,
                                          JwtTokenService jwtTokenService,
                                          RefreshTokenSecurityService refreshTokenSecurityService,
                                          CustomerServiceConfigData customerServiceConfigData) {
        this.customerDomainService = customerDomainService;
        this.customerDataMapper = customerDataMapper;
        this.customerRepository = customerRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordHashingService = passwordHashingService;
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenSecurityService = refreshTokenSecurityService;
        this.customerServiceConfigData = customerServiceConfigData;
    }

    @Override
    @Transactional
    public RegisterCustomerResponse registerCustomer(RegisterCustomerCommand registerCustomerCommand) {
        String username = normalizeUsername(registerCustomerCommand.getUsername());
        if (customerRepository.existsByUsername(username)) {
            throw new CustomerApplicationServiceException("Customer with username " + username + " already exists!");
        }

        Customer customer = customerDataMapper.registerCommandToCustomer(registerCustomerCommand,
                username,
                passwordHashingService.encode(registerCustomerCommand.getPassword()));
        Customer initializedCustomer = customerDomainService.validateAndInitializeCustomer(customer);
        Customer savedCustomer = customerRepository.save(initializedCustomer);
        log.info("Customer is registered with id: {}", savedCustomer.getId().getValue());
        return customerDataMapper.customerToRegisterCustomerResponse(savedCustomer,
                "Customer registered successfully");
    }

    @Override
    @Transactional
    public AuthenticationResponse login(LoginCustomerCommand loginCustomerCommand) {
        String username = normalizeUsername(loginCustomerCommand.getUsername());
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerAuthenticationException("Username or password is invalid!"));

        if (!passwordHashingService.matches(loginCustomerCommand.getPassword(), customer.getPassword())) {
            throw new CustomerAuthenticationException("Username or password is invalid!");
        }

        log.info("Customer logged in with id: {}", customer.getId().getValue());
        return createAuthenticationResponse(customer);
    }

    @Override
    @Transactional
    public AuthenticationResponse refreshToken(RefreshTokenCommand refreshTokenCommand) {
        String tokenHash = refreshTokenSecurityService.hash(refreshTokenCommand.getRefreshToken());
        RefreshToken currentRefreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new CustomerAuthenticationException("Refresh token is invalid!"));
        currentRefreshToken.validateActive(Instant.now());

        Customer customer = customerRepository.findById(currentRefreshToken.getCustomerId())
                .orElseThrow(() -> new CustomerApplicationServiceException("Customer could not be found!"));

        currentRefreshToken.revoke();
        refreshTokenRepository.save(currentRefreshToken);
        return createAuthenticationResponse(customer);
    }

    @Override
    @Transactional
    public LogoutResponse logout(LogoutCommand logoutCommand) {
        String tokenHash = refreshTokenSecurityService.hash(logoutCommand.getRefreshToken());
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByTokenHash(tokenHash);
        refreshToken.ifPresent(token -> {
            token.revoke();
            refreshTokenRepository.save(token);
        });
        return LogoutResponse.builder()
                .message("Customer logged out successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findCustomer(UUID customerId) {
        Customer customer = customerRepository.findById(new CustomerId(customerId))
                .orElseThrow(() -> new CustomerApplicationServiceException("Customer with id " + customerId +
                        " could not be found!"));
        return customerDataMapper.customerToCustomerResponse(customer);
    }

    private AuthenticationResponse createAuthenticationResponse(Customer customer) {
        Instant issuedAt = Instant.now();
        Instant accessTokenExpiresAt = issuedAt.plus(Duration.ofMinutes(
                customerServiceConfigData.getAccessTokenExpirationMinutes()));
        Instant refreshTokenExpiresAt = issuedAt.plus(Duration.ofDays(
                customerServiceConfigData.getRefreshTokenExpirationDays()));
        String accessToken = jwtTokenService.generateAccessToken(customer, issuedAt, accessTokenExpiresAt);
        String refreshToken = refreshTokenSecurityService.generateToken();
        RefreshToken newRefreshToken = customerDomainService.initializeRefreshToken(customer.getId(),
                refreshTokenSecurityService.hash(refreshToken), refreshTokenExpiresAt);
        refreshTokenRepository.save(newRefreshToken);
        return customerDataMapper.customerToAuthenticationResponse(customer,
                accessToken,
                refreshToken,
                Duration.between(issuedAt, accessTokenExpiresAt).toSeconds());
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase();
    }
}
