package com.ticket.ordering.system.ticket.service.dataaccess.ticket.adapter;

import com.ticket.ordering.system.ticket.service.dataaccess.ticket.mapper.TicketDataAccessMapper;
import com.ticket.ordering.system.ticket.service.dataaccess.ticket.repository.TicketJpaRepository;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final TicketDataAccessMapper ticketDataAccessMapper;

    public TicketRepositoryImpl(TicketJpaRepository ticketJpaRepository,
                                TicketDataAccessMapper ticketDataAccessMapper) {
        this.ticketJpaRepository = ticketJpaRepository;
        this.ticketDataAccessMapper = ticketDataAccessMapper;
    }

    @Override
    public List<Ticket> saveAll(List<Ticket> tickets) {
        return ticketJpaRepository.saveAll(tickets.stream()
                        .map(ticketDataAccessMapper::ticketToTicketEntity)
                        .collect(Collectors.toList()))
                .stream()
                .map(ticketDataAccessMapper::ticketEntityToTicket)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ticket> findByIds(List<UUID> ticketIds) {
        return ticketJpaRepository.findByIdIn(ticketIds).stream()
                .map(ticketDataAccessMapper::ticketEntityToTicket)
                .collect(Collectors.toList());
    }
}
