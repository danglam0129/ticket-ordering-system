package com.ticket.ordering.system.payment.service.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ordering.system.payment.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.payment.service.domain.outbox.PaymentOutboxMessage;
import com.ticket.ordering.system.payment.service.domain.ports.output.repository.PaymentOutboxRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PaymentOutboxMessageHelper {

    private final ObjectMapper objectMapper;
    private final PaymentOutboxRepository paymentOutboxRepository;

    public PaymentOutboxMessageHelper(ObjectMapper objectMapper,
                                      PaymentOutboxRepository paymentOutboxRepository) {
        this.objectMapper = objectMapper;
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    public PaymentOutboxMessage save(String aggregateId,
                                     String eventType,
                                     String topicName,
                                     String messageKey,
                                     String sagaId,
                                     Object payload) {
        Instant now = Instant.now();
        try {
            return paymentOutboxRepository.save(PaymentOutboxMessage.builder()
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
            throw new IllegalStateException("Could not serialize payment outbox payload for aggregate id: " +
                    aggregateId, e);
        }
    }

    private String resolveSagaId(String sagaId, String aggregateId) {
        return sagaId == null || sagaId.isBlank() ? aggregateId : sagaId;
    }
}
