package com.ticket.ordering.system.order.service.domain.ports.output.repository;

import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;

public interface OrderOutboxRepository {
    void save(OrderOutboxMessage orderOutboxMessage);
}
