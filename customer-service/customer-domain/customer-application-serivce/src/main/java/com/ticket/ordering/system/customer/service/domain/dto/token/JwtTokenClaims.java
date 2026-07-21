package com.ticket.ordering.system.customer.service.domain.dto.token;

import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class JwtTokenClaims {
    private final UUID customerId;
    private final String username;
    private final CustomerRole role;
    private final Instant expiresAt;
}
