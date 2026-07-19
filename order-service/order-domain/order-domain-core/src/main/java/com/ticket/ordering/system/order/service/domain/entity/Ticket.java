package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.AggregateRoot;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.TicketId;

public class Ticket extends AggregateRoot<TicketId> {
    private Event event;
    private Money price;
    private Seat seat;

    public Ticket(Builder builder) {
        super.setId(builder.id);
        this.event = builder.event;
        this.price = builder.price;
        this.seat = builder.seat;
    }

    public Ticket(TicketId ticketId) {
        super.setId(ticketId);
    }

    public void updateWithConfirmedInformation(Event event, Money price, Seat seat) {
        this.event = event;
        this.price = price;
        this.seat = seat;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Event getEvent() {
        return event;
    }

    public Money getPrice() {
        return price;
    }

    public Seat getSeat() {
        return seat;
    }

    public static final class Builder{
        private TicketId id;
        private Event event;
        private Money price;
        private Seat seat;

        public Builder id(TicketId id) {
            this.id = id;
            return this;
        }

        public Builder event(Event event) {
            this.event = event;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }
        public Builder seat(Seat seat) {
            this.seat = seat;
            return this;
        }

        public Ticket build(){
            return new Ticket(this);
        }
    }
}
