package com.ticket.ordering.system.customer.service.domain.security;

import com.ticket.ordering.system.customer.service.domain.ports.output.security.RefreshTokenSecurityService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenSecurityServiceImpl implements RefreshTokenSecurityService {

    private static final int REFRESH_TOKEN_BYTES = 64;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generateToken() {
        byte[] tokenBytes = new byte[REFRESH_TOKEN_BYTES];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    @Override
    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available", e);
        }
    }
}
