package com.ticket.ordering.system.order.service.dataaccess.order.repository;

import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {
}
