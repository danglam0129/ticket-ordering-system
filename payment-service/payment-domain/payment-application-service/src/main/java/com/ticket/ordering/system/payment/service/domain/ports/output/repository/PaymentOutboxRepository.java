package com.ticket.ordering.system.payment.service.domain.ports.output.repository;

import com.ticket.ordering.system.payment.service.domain.outbox.PaymentOutboxMessage;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentOutboxRepository {

    PaymentOutboxMessage save(PaymentOutboxMessage paymentOutboxMessage);

    List<PaymentOutboxMessage> findPendingMessages(int maxRetryCount, Instant processingTimeout);

    boolean markAsProcessing(UUID id);

    void markAsCompleted(UUID id);

    void markAsFailed(UUID id, String errorMessage);
}
