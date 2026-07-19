package com.ticket.ordering.system.order.service.domain.ports.input.message.listener.ticketreservation;

import com.ticket.ordering.system.order.service.domain.dto.message.TicketReservationResponse;

public interface TicketReservationResponseMessageListener {
    void reservationApproved(TicketReservationResponse ticketReservationResponse);

    void reservationRejected(TicketReservationResponse ticketReservationResponse);
}
