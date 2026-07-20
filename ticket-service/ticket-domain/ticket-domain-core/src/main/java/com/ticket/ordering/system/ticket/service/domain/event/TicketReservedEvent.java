package com.ticket.ordering.system.ticket.service.domain.event;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;

import java.time.ZonedDateTime;
import java.util.List;

public class TicketReservedEvent extends TicketEvent {

    private final DomainEventPublisher<TicketReservedEvent> ticketReservedEventPublisher;

    public TicketReservedEvent(OrderId orderId,
                               List<TicketId> ticketIds,
                               List<Ticket> tickets,
                               List<String> failureMessages,
                               ZonedDateTime createdAt,
                               DomainEventPublisher<TicketReservedEvent> ticketReservedEventPublisher) {
        super(orderId, ticketIds, tickets, failureMessages, createdAt);
        this.ticketReservedEventPublisher = ticketReservedEventPublisher;
    }

    @Override
    public void fire() {
        ticketReservedEventPublisher.publish(this);
    }
}
