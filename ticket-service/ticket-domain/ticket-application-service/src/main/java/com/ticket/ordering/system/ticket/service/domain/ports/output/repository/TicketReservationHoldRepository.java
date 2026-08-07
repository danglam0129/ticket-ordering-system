package com.ticket.ordering.system.ticket.service.domain.ports.output.repository;

import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;

import java.util.List;

public interface TicketReservationHoldRepository {

    List<String> holdTickets(OrderId orderId, List<TicketId> ticketIds);

    void releaseTickets(OrderId orderId, List<TicketId> ticketIds);
}
