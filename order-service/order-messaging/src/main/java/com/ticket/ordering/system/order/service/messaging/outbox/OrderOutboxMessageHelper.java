package com.ticket.ordering.system.order.service.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;
import com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderOutboxRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderOutboxMessageHelper {

    private final ObjectMapper objectMapper;
    private final OrderOutboxRepository orderOutboxRepository;

    public OrderOutboxMessageHelper(ObjectMapper objectMapper,
                                    OrderOutboxRepository orderOutboxRepository) {
        this.objectMapper = objectMapper;
        this.orderOutboxRepository = orderOutboxRepository;
    }

    public OrderOutboxMessage save(String aggregateId,
                                   String eventType,
                                   String topicName,
                                   String messageKey,
                                   String sagaId,
                                   Object payload) {
        Instant now = Instant.now();
        try {
            return orderOutboxRepository.save(OrderOutboxMessage.builder()
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
            throw new IllegalStateException("Could not serialize order outbox payload for aggregate id: " +
                    aggregateId, e);
        }
    }

    private String resolveSagaId(String sagaId, String aggregateId) {
        return sagaId == null || sagaId.isBlank() ? aggregateId : sagaId;
    }
}
