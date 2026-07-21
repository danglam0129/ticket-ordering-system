package com.ticket.ordering.system.customer.service.domain.dto.login;

import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuthenticationResponse {
    private final UUID customerId;
    private final String username;
    private final CustomerRole role;
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final Long expiresIn;
}
