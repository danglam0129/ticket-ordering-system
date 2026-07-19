package com.ticket.ordering.system.order.service.domain.outbox;

import java.time.Instant;
import java.util.UUID;

public class OrderOutboxMessage {
    private final UUID id;
    private final String aggregateId;
    private final String eventType;
    private final String payload;
    private final Instant createdAt;
    private final OutboxStatus status;

    private OrderOutboxMessage(Builder builder) {
        id = builder.id;
        aggregateId = builder.aggregateId;
        eventType = builder.eventType;
        payload = builder.payload;
        createdAt = builder.createdAt;
        status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public static final class Builder {
        private UUID id;
        private String aggregateId;
        private String eventType;
        private String payload;
        private Instant createdAt;
        private OutboxStatus status;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder aggregateId(String aggregateId) {
            this.aggregateId = aggregateId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder status(OutboxStatus status) {
            this.status = status;
            return this;
        }

        public OrderOutboxMessage build() {
            return new OrderOutboxMessage(this);
        }
    }
}
