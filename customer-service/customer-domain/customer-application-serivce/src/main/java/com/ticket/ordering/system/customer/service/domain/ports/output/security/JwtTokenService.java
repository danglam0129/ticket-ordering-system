package com.ticket.ordering.system.customer.service.domain.ports.output.security;

import com.ticket.ordering.system.customer.service.domain.dto.token.JwtTokenClaims;
import com.ticket.ordering.system.customer.service.domain.entity.Customer;

import java.time.Instant;
import java.util.Optional;

public interface JwtTokenService {

    String generateAccessToken(Customer customer, Instant issuedAt, Instant expiresAt);

    Optional<JwtTokenClaims> parseAccessToken(String token);
}
