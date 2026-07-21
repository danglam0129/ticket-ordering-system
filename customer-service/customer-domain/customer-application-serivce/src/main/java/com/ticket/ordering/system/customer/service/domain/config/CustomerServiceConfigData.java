package com.ticket.ordering.system.customer.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "customer-service")
public class CustomerServiceConfigData {

    private String jwtIssuer = "ticket-ordering-system";
    private String jwtSecret = "ticket-ordering-system-local-development-secret-change-me-please";
    private Long accessTokenExpirationMinutes = 15L;
    private Long refreshTokenExpirationDays = 7L;
}
