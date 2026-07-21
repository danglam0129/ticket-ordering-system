package com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.mapper;

import com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.entity.RefreshTokenEntity;
import com.ticket.ordering.system.customer.service.domain.entity.RefreshToken;
import com.ticket.ordering.system.customer.service.domain.valueobject.RefreshTokenId;
import com.ticket.ordering.system.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenDataAccessMapper {

    public RefreshTokenEntity refreshTokenToRefreshTokenEntity(RefreshToken refreshToken) {
        return RefreshTokenEntity.builder()
                .id(refreshToken.getId().getValue())
                .customerId(refreshToken.getCustomerId().getValue())
                .tokenHash(refreshToken.getTokenHash())
                .createdAt(refreshToken.getCreatedAt())
                .expiresAt(refreshToken.getExpiresAt())
                .revoked(refreshToken.isRevoked())
                .build();
    }

    public RefreshToken refreshTokenEntityToRefreshToken(RefreshTokenEntity refreshTokenEntity) {
        return RefreshToken.builder()
                .refreshTokenId(new RefreshTokenId(refreshTokenEntity.getId()))
                .customerId(new CustomerId(refreshTokenEntity.getCustomerId()))
                .tokenHash(refreshTokenEntity.getTokenHash())
                .createdAt(refreshTokenEntity.getCreatedAt())
                .expiresAt(refreshTokenEntity.getExpiresAt())
                .revoked(refreshTokenEntity.isRevoked())
                .build();
    }
}
