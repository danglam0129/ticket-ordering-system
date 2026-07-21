package com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.repository;

import com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
}
