package com.ticket.ordering.system.customer.service.domain;

import com.ticket.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.RefreshTokenRepository;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.JwtTokenService;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.PasswordHashingService;
import com.ticket.ordering.system.customer.service.domain.ports.output.security.RefreshTokenSecurityService;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.ticket.ordering.system.customer.service.domain")
public class CustomerTestConfiguration {

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public RefreshTokenRepository refreshTokenRepository() {
        return Mockito.mock(RefreshTokenRepository.class);
    }

    @Bean
    public PasswordHashingService passwordHashingService() {
        return Mockito.mock(PasswordHashingService.class);
    }

    @Bean
    public JwtTokenService jwtTokenService() {
        return Mockito.mock(JwtTokenService.class);
    }

    @Bean
    public RefreshTokenSecurityService refreshTokenSecurityService() {
        return Mockito.mock(RefreshTokenSecurityService.class);
    }

    @Bean
    public CustomerDomainService customerDomainService() {
        return new CustomerDomainServiceImpl();
    }
}
