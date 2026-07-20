package com.ticket.ordering.system.ticket.service.domain.ports.output.repository;

import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;

import java.util.List;
import java.util.UUID;

public interface TicketRepository {

    List<Ticket> saveAll(List<Ticket> tickets);

    List<Ticket> findByIds(List<UUID> ticketIds);
}
