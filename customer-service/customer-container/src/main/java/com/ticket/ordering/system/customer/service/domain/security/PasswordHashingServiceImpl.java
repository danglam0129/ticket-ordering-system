package com.ticket.ordering.system.customer.service.domain.security;

import com.ticket.ordering.system.customer.service.domain.ports.output.security.PasswordHashingService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHashingServiceImpl implements PasswordHashingService {

    private final PasswordEncoder passwordEncoder;

    public PasswordHashingServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
