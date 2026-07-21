package com.ticket.ordering.system.ticket.service.dataaccess.outbox.entity;

import com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket_outbox")
@Entity
public class TicketOutboxEntity {

    @Id
    private UUID id;
    private String sagaId;
    private String aggregateId;
    private String eventType;
    private String topicName;
    private String messageKey;
    private String payloadType;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private Instant createdAt;
    private Instant updatedAt;
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private int retryCount;
    @Column(columnDefinition = "TEXT")
    private String lastError;
}
