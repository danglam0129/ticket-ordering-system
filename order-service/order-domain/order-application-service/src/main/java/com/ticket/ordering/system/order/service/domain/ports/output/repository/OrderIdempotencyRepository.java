package com.ticket.ordering.system.order.service.domain.ports.output.repository;

import com.ticket.ordering.system.order.service.domain.entity.Order;

import java.util.Optional;

public interface OrderIdempotencyRepository {
    Optional<Order> findOrder(String idempotencyKey);

    void save(String idempotencyKey, Order order);
}
