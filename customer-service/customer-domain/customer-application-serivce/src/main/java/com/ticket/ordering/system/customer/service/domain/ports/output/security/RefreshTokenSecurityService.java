package com.ticket.ordering.system.customer.service.domain.ports.output.security;

public interface RefreshTokenSecurityService {

    String generateToken();

    String hash(String token);
}
