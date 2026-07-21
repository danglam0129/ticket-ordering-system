package com.ticket.ordering.system.customer.service.domain.ports.output.security;

public interface PasswordHashingService {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
