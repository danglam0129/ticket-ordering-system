package com.ticket.ordering.system.ticket.service.domain.event;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;

import java.time.ZonedDateTime;
import java.util.List;

public class TicketReservationRejectedEvent extends TicketEvent {

    private final DomainEventPublisher<TicketReservationRejectedEvent> ticketReservationRejectedEventPublisher;

    public TicketReservationRejectedEvent(OrderId orderId,
                                          List<TicketId> ticketIds,
                                          List<Ticket> tickets,
                                          List<String> failureMessages,
                                          ZonedDateTime createdAt,
                                          DomainEventPublisher<TicketReservationRejectedEvent>
                                                  ticketReservationRejectedEventPublisher) {
        super(orderId, ticketIds, tickets, failureMessages, createdAt);
        this.ticketReservationRejectedEventPublisher = ticketReservationRejectedEventPublisher;
    }

    @Override
    public void fire() {
        ticketReservationRejectedEventPublisher.publish(this);
    }
}
