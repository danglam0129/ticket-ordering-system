package com.ticket.ordering.system.customer.service.domain.entity;

import com.ticket.ordering.system.customer.service.domain.exception.RefreshTokenDomainException;
import com.ticket.ordering.system.customer.service.domain.valueobject.RefreshTokenId;
import com.ticket.ordering.system.domain.entity.BaseEntity;
import com.ticket.ordering.system.domain.valueobject.CustomerId;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken extends BaseEntity<RefreshTokenId> {

    private final CustomerId customerId;
    private final String tokenHash;
    private Instant createdAt;
    private final Instant expiresAt;
    private boolean revoked;

    private RefreshToken(Builder builder) {
        setId(builder.refreshTokenId);
        customerId = builder.customerId;
        tokenHash = builder.tokenHash;
        createdAt = builder.createdAt;
        expiresAt = builder.expiresAt;
        revoked = builder.revoked;
    }

    public void initializeRefreshToken() {
        if (getId() != null) {
            throw new RefreshTokenDomainException("Refresh token is not in correct state for initialization!");
        }
        if (customerId == null) {
            throw new RefreshTokenDomainException("Customer id must be present!");
        }
        if (tokenHash == null || tokenHash.isBlank()) {
            throw new RefreshTokenDomainException("Refresh token hash must be present!");
        }
        if (expiresAt == null) {
            throw new RefreshTokenDomainException("Refresh token expiry must be present!");
        }
        setId(new RefreshTokenId(UUID.randomUUID()));
        createdAt = Instant.now();
        revoked = false;
    }

    public void validateActive(Instant now) {
        if (revoked) {
            throw new RefreshTokenDomainException("Refresh token is revoked!");
        }
        if (expiresAt.isBefore(now) || expiresAt.equals(now)) {
            throw new RefreshTokenDomainException("Refresh token is expired!");
        }
    }

    public void revoke() {
        revoked = true;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private RefreshTokenId refreshTokenId;
        private CustomerId customerId;
        private String tokenHash;
        private Instant createdAt;
        private Instant expiresAt;
        private boolean revoked;

        private Builder() {
        }

        public Builder refreshTokenId(RefreshTokenId val) {
            refreshTokenId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder tokenHash(String val) {
            tokenHash = val;
            return this;
        }

        public Builder createdAt(Instant val) {
            createdAt = val;
            return this;
        }

        public Builder expiresAt(Instant val) {
            expiresAt = val;
            return this;
        }

        public Builder revoked(boolean val) {
            revoked = val;
            return this;
        }

        public RefreshToken build() {
            return new RefreshToken(this);
        }
    }
}
