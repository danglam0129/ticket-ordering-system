package com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketreservation;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservationRejectedEvent;

public interface TicketReservationRejectedMessagePublisher
        extends DomainEventPublisher<TicketReservationRejectedEvent> {
}
