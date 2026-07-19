package com.ticket.ordering.system.order.service.domain.ports.output.repository;

import com.ticket.ordering.system.order.service.domain.entity.Ticket;

import java.util.Optional;

public interface TicketRepository {
    Optional<Ticket> findTicketInformation(Ticket ticket);
}
