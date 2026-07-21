package com.ticket.ordering.system.ticket.service.dataaccess.outbox.mapper;

import com.ticket.ordering.system.ticket.service.dataaccess.outbox.entity.TicketOutboxEntity;
import com.ticket.ordering.system.ticket.service.domain.outbox.TicketOutboxMessage;
import org.springframework.stereotype.Component;

@Component
public class TicketOutboxDataAccessMapper {

    public TicketOutboxEntity ticketOutboxMessageToOutboxEntity(TicketOutboxMessage ticketOutboxMessage) {
        return TicketOutboxEntity.builder()
                .id(ticketOutboxMessage.getId())
                .sagaId(ticketOutboxMessage.getSagaId())
                .aggregateId(ticketOutboxMessage.getAggregateId())
                .eventType(ticketOutboxMessage.getEventType())
                .topicName(ticketOutboxMessage.getTopicName())
                .messageKey(ticketOutboxMessage.getMessageKey())
                .payloadType(ticketOutboxMessage.getPayloadType())
                .payload(ticketOutboxMessage.getPayload())
                .createdAt(ticketOutboxMessage.getCreatedAt())
                .updatedAt(ticketOutboxMessage.getUpdatedAt())
                .status(ticketOutboxMessage.getStatus())
                .retryCount(ticketOutboxMessage.getRetryCount())
                .lastError(ticketOutboxMessage.getLastError())
                .build();
    }

    public TicketOutboxMessage ticketOutboxEntityToOutboxMessage(TicketOutboxEntity ticketOutboxEntity) {
        return TicketOutboxMessage.builder()
                .id(ticketOutboxEntity.getId())
                .sagaId(ticketOutboxEntity.getSagaId())
                .aggregateId(ticketOutboxEntity.getAggregateId())
                .eventType(ticketOutboxEntity.getEventType())
                .topicName(ticketOutboxEntity.getTopicName())
                .messageKey(ticketOutboxEntity.getMessageKey())
                .payloadType(ticketOutboxEntity.getPayloadType())
                .payload(ticketOutboxEntity.getPayload())
                .createdAt(ticketOutboxEntity.getCreatedAt())
                .updatedAt(ticketOutboxEntity.getUpdatedAt())
                .status(ticketOutboxEntity.getStatus())
                .retryCount(ticketOutboxEntity.getRetryCount())
                .lastError(ticketOutboxEntity.getLastError())
                .build();
    }
}
