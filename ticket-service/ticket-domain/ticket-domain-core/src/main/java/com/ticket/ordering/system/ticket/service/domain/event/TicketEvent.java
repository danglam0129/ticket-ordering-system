package com.ticket.ordering.system.ticket.service.domain.event;

import com.ticket.ordering.system.domain.event.DomainEvent;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;

import java.time.ZonedDateTime;
import java.util.List;

public abstract class TicketEvent implements DomainEvent<List<Ticket>> {

    private final OrderId orderId;
    private final List<TicketId> ticketIds;
    private final List<Ticket> tickets;
    private final List<String> failureMessages;
    private final ZonedDateTime createdAt;

    protected TicketEvent(OrderId orderId,
                          List<TicketId> ticketIds,
                          List<Ticket> tickets,
                          List<String> failureMessages,
                          ZonedDateTime createdAt) {
        this.orderId = orderId;
        this.ticketIds = ticketIds;
        this.tickets = tickets;
        this.failureMessages = failureMessages;
        this.createdAt = createdAt;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public List<TicketId> getTicketIds() {
        return ticketIds;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
