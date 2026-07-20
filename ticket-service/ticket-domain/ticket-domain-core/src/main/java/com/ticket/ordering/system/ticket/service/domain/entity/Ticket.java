package com.ticket.ordering.system.ticket.service.domain.entity;

import com.ticket.ordering.system.domain.entity.AggregateRoot;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.SeatId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.valueobject.TicketStatus;

import java.util.List;

public class Ticket extends AggregateRoot<TicketId> {

    private final SeatId seatId;
    private final Money price;
    private TicketStatus status;
    private OrderId reservedByOrderId;

    private Ticket(Builder builder) {
        setId(builder.ticketId);
        seatId = builder.seatId;
        price = builder.price;
        status = builder.status;
        reservedByOrderId = builder.reservedByOrderId;
    }

    public void validateForReservation(OrderId orderId, List<String> failureMessages) {
        if (status != TicketStatus.AVAILABLE) {
            failureMessages.add("Ticket with id=" + getId().getValue() + " is not available for reservation!");
        }
        if (reservedByOrderId != null && !reservedByOrderId.equals(orderId)) {
            failureMessages.add("Ticket with id=" + getId().getValue() + " is reserved by another order!");
        }
    }

    public void validateForApproval(OrderId orderId, List<String> failureMessages) {
        if (status != TicketStatus.RESERVED) {
            failureMessages.add("Ticket with id=" + getId().getValue() + " is not reserved for approval!");
        }
        if (reservedByOrderId == null || !reservedByOrderId.equals(orderId)) {
            failureMessages.add("Ticket with id=" + getId().getValue() + " is not reserved by order id="
                    + orderId.getValue() + "!");
        }
    }

    public void reserve(OrderId orderId) {
        status = TicketStatus.RESERVED;
        reservedByOrderId = orderId;
    }

    public void approve() {
        status = TicketStatus.SOLD;
    }

    public SeatId getSeatId() {
        return seatId;
    }

    public Money getPrice() {
        return price;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public OrderId getReservedByOrderId() {
        return reservedByOrderId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private TicketId ticketId;
        private SeatId seatId;
        private Money price;
        private TicketStatus status;
        private OrderId reservedByOrderId;

        private Builder() {
        }

        public Builder ticketId(TicketId val) {
            ticketId = val;
            return this;
        }

        public Builder seatId(SeatId val) {
            seatId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder status(TicketStatus val) {
            status = val;
            return this;
        }

        public Builder reservedByOrderId(OrderId val) {
            reservedByOrderId = val;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}
