package com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketapproval;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;

public interface OrderPaidTicketRequestMessagePublisher extends DomainEventPublisher<OrderPaidEvent> {
}
