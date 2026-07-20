package com.ticket.ordering.system.ticket.service.dataaccess.ticket.mapper;

import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.SeatId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.dataaccess.ticket.entity.TicketEntity;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketDataAccessMapper {

    public TicketEntity ticketToTicketEntity(Ticket ticket) {
        return TicketEntity.builder()
                .id(ticket.getId().getValue())
                .seatId(ticket.getSeatId().getValue())
                .price(ticket.getPrice().getAmount())
                .status(ticket.getStatus())
                .reservedByOrderId(ticket.getReservedByOrderId() == null ?
                        null : ticket.getReservedByOrderId().getValue())
                .build();
    }

    public Ticket ticketEntityToTicket(TicketEntity ticketEntity) {
        return Ticket.builder()
                .ticketId(new TicketId(ticketEntity.getId()))
                .seatId(new SeatId(ticketEntity.getSeatId()))
                .price(new Money(ticketEntity.getPrice()))
                .status(ticketEntity.getStatus())
                .reservedByOrderId(ticketEntity.getReservedByOrderId() == null ?
                        null : new OrderId(ticketEntity.getReservedByOrderId()))
                .build();
    }
}
