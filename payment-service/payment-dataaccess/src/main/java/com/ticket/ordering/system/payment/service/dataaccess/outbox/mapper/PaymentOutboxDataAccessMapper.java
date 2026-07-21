package com.ticket.ordering.system.payment.service.dataaccess.outbox.mapper;

import com.ticket.ordering.system.payment.service.dataaccess.outbox.entity.PaymentOutboxEntity;
import com.ticket.ordering.system.payment.service.domain.outbox.PaymentOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class PaymentOutboxDataAccessMapper {

    public PaymentOutboxEntity paymentOutboxMessageToOutboxEntity(PaymentOutboxMessage paymentOutboxMessage) {
        return PaymentOutboxEntity.builder()
                .id(paymentOutboxMessage.getId())
                .sagaId(paymentOutboxMessage.getSagaId())
                .aggregateId(paymentOutboxMessage.getAggregateId())
                .eventType(paymentOutboxMessage.getEventType())
                .topicName(paymentOutboxMessage.getTopicName())
                .messageKey(paymentOutboxMessage.getMessageKey())
                .payloadType(paymentOutboxMessage.getPayloadType())
                .payload(paymentOutboxMessage.getPayload())
                .createdAt(paymentOutboxMessage.getCreatedAt())
                .updatedAt(paymentOutboxMessage.getUpdatedAt())
                .status(paymentOutboxMessage.getStatus())
                .retryCount(paymentOutboxMessage.getRetryCount())
                .lastError(paymentOutboxMessage.getLastError())
                .build();
    }

    public PaymentOutboxMessage paymentOutboxEntityToOutboxMessage(PaymentOutboxEntity paymentOutboxEntity) {
        return PaymentOutboxMessage.builder()
                .id(paymentOutboxEntity.getId())
                .sagaId(paymentOutboxEntity.getSagaId())
                .aggregateId(paymentOutboxEntity.getAggregateId())
                .eventType(paymentOutboxEntity.getEventType())
                .topicName(paymentOutboxEntity.getTopicName())
                .messageKey(paymentOutboxEntity.getMessageKey())
                .payloadType(paymentOutboxEntity.getPayloadType())
                .payload(paymentOutboxEntity.getPayload())
                .createdAt(paymentOutboxEntity.getCreatedAt())
                .updatedAt(paymentOutboxEntity.getUpdatedAt())
                .status(paymentOutboxEntity.getStatus())
                .retryCount(paymentOutboxEntity.getRetryCount())
                .lastError(paymentOutboxEntity.getLastError())
                .build();
    }
}
