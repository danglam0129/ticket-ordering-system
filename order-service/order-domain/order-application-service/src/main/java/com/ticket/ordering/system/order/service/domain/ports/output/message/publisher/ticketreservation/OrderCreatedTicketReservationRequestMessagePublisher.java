package com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketreservation;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;

public interface OrderCreatedTicketReservationRequestMessagePublisher extends DomainEventPublisher<OrderCreatedEvent> {
}
