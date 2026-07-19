package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.AggregateRoot;
import com.ticket.ordering.system.domain.valueobject.EventId;

public class Event extends AggregateRoot<EventId> {
    private final String eventName;
    private final String eventDate;
    private boolean active;

    public Event(Builder builder) {
        super.setId(builder.eventId);
        this.eventName = builder.eventName;
        this.eventDate = builder.eventDate;
        this.active = builder.active;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isActive() {
        return active;
    }
    public String getEventName() {
        return eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public static final class Builder{
        private EventId eventId;
        private String eventName;
        private String eventDate;
        private boolean active;

        public Builder eventId(EventId eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventName(String eventName) {
            this.eventName = eventName;
            return this;
        }

        public Builder eventDate(String eventDate) {
            this.eventDate = eventDate;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }
}
