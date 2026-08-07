package com.ticket.ordering.system.ticket.service.domain.ports.output.repository;

import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "ticket-service.redis-reservation-holds-enabled",
        havingValue = "false", matchIfMissing = true)
public class NoOpTicketReservationHoldRepository implements TicketReservationHoldRepository {

    @Override
    public List<String> holdTickets(OrderId orderId, List<TicketId> ticketIds) {
        return List.of();
    }

    @Override
    public void releaseTickets(OrderId orderId, List<TicketId> ticketIds) {
    }
}
