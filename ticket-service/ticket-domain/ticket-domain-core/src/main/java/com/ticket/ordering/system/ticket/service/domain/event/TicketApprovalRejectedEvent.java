package com.ticket.ordering.system.ticket.service.domain.event;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;

import java.time.ZonedDateTime;
import java.util.List;

public class TicketApprovalRejectedEvent extends TicketEvent {

    private final DomainEventPublisher<TicketApprovalRejectedEvent> ticketApprovalRejectedEventPublisher;

    public TicketApprovalRejectedEvent(OrderId orderId,
                                       List<TicketId> ticketIds,
                                       List<Ticket> tickets,
                                       List<String> failureMessages,
                                       ZonedDateTime createdAt,
                                       DomainEventPublisher<TicketApprovalRejectedEvent>
                                               ticketApprovalRejectedEventPublisher) {
        super(orderId, ticketIds, tickets, failureMessages, createdAt);
        this.ticketApprovalRejectedEventPublisher = ticketApprovalRejectedEventPublisher;
    }

    @Override
    public void fire() {
        ticketApprovalRejectedEventPublisher.publish(this);
    }
}
