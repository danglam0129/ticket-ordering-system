package com.ticket.ordering.system.customer.service.domain.ports.output.repository;

import com.ticket.ordering.system.customer.service.domain.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
