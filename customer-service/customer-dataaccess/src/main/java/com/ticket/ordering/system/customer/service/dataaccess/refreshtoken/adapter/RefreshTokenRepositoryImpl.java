package com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.adapter;

import com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.mapper.RefreshTokenDataAccessMapper;
import com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.repository.RefreshTokenJpaRepository;
import com.ticket.ordering.system.customer.service.domain.entity.RefreshToken;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;
    private final RefreshTokenDataAccessMapper refreshTokenDataAccessMapper;

    public RefreshTokenRepositoryImpl(RefreshTokenJpaRepository refreshTokenJpaRepository,
                                      RefreshTokenDataAccessMapper refreshTokenDataAccessMapper) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
        this.refreshTokenDataAccessMapper = refreshTokenDataAccessMapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenDataAccessMapper.refreshTokenEntityToRefreshToken(refreshTokenJpaRepository.save(
                refreshTokenDataAccessMapper.refreshTokenToRefreshTokenEntity(refreshToken)));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenJpaRepository.findByTokenHash(tokenHash)
                .map(refreshTokenDataAccessMapper::refreshTokenEntityToRefreshToken);
    }
}
