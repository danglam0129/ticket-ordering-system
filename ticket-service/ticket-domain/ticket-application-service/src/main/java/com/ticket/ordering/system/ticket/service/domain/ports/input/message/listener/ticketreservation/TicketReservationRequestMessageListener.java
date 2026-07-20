package com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketreservation;

import com.ticket.ordering.system.ticket.service.domain.dto.TicketReservationRequest;

public interface TicketReservationRequestMessageListener {

    void reserveTickets(TicketReservationRequest ticketReservationRequest);
}
