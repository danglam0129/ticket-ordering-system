package com.ticket.ordering.system.ticket.service.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.ticket.service.domain.outbox.TicketOutboxMessage;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketOutboxRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class TicketOutboxMessageHelper {

    private final ObjectMapper objectMapper;
    private final TicketOutboxRepository ticketOutboxRepository;

    public TicketOutboxMessageHelper(ObjectMapper objectMapper,
                                     TicketOutboxRepository ticketOutboxRepository) {
        this.objectMapper = objectMapper;
        this.ticketOutboxRepository = ticketOutboxRepository;
    }

    public TicketOutboxMessage save(String aggregateId,
                                    String eventType,
                                    String topicName,
                                    String messageKey,
                                    String sagaId,
                                    Object payload) {
        Instant now = Instant.now();
        try {
            return ticketOutboxRepository.save(TicketOutboxMessage.builder()
                    .id(UUID.randomUUID())
                    .sagaId(resolveSagaId(sagaId, aggregateId))
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .topicName(topicName)
                    .messageKey(messageKey)
                    .payloadType(payload.getClass().getName())
                    .payload(objectMapper.writeValueAsString(payload))
                    .createdAt(now)
                    .updatedAt(now)
                    .status(OutboxStatus.STARTED)
                    .retryCount(0)
                    .build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize ticket outbox payload for aggregate id: " +
                    aggregateId, e);
        }
    }

    private String resolveSagaId(String sagaId, String aggregateId) {
        return sagaId == null || sagaId.isBlank() ? aggregateId : sagaId;
    }
}
