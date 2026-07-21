package com.ticket.ordering.system.order.service.domain.ports.output.repository;

import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OrderOutboxRepository {

    OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage);

    List<OrderOutboxMessage> findPendingMessages(int maxRetryCount, Instant processingTimeout);

    boolean markAsProcessing(UUID id);

    void markAsCompleted(UUID id);

    void markAsFailed(UUID id, String errorMessage);
}
