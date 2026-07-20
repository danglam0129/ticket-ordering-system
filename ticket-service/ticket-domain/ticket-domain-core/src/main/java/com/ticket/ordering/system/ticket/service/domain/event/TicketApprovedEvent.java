package com.ticket.ordering.system.ticket.service.domain.event;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;

import java.time.ZonedDateTime;
import java.util.List;

public class TicketApprovedEvent extends TicketEvent {

    private final DomainEventPublisher<TicketApprovedEvent> ticketApprovedEventPublisher;

    public TicketApprovedEvent(OrderId orderId,
                               List<TicketId> ticketIds,
                               List<Ticket> tickets,
                               List<String> failureMessages,
                               ZonedDateTime createdAt,
                               DomainEventPublisher<TicketApprovedEvent> ticketApprovedEventPublisher) {
        super(orderId, ticketIds, tickets, failureMessages, createdAt);
        this.ticketApprovedEventPublisher = ticketApprovedEventPublisher;
    }

    @Override
    public void fire() {
        ticketApprovedEventPublisher.publish(this);
    }
}
