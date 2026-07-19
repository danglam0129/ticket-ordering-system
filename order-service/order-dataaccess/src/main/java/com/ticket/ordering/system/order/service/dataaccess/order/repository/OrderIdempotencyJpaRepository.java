package com.ticket.ordering.system.order.service.dataaccess.order.repository;

import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderIdempotencyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderIdempotencyJpaRepository extends JpaRepository<OrderIdempotencyEntity, String> {

    Optional<OrderIdempotencyEntity> findByIdempotencyKey(String idempotencyKey);
}
