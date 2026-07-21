package com.ticket.ordering.system.ticket.service.domain.outbox;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class TicketOutboxMessage {

    private final UUID id;
    private final String sagaId;
    private final String aggregateId;
    private final String eventType;
    private final String topicName;
    private final String messageKey;
    private final String payloadType;
    private final String payload;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final OutboxStatus status;
    private final int retryCount;
    private final String lastError;
}
