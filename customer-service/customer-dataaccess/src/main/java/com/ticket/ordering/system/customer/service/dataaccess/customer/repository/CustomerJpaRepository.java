package com.ticket.ordering.system.customer.service.dataaccess.customer.repository;

import com.ticket.ordering.system.customer.service.dataaccess.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
