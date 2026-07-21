package com.ticket.ordering.system.ticket.service.domain;

import com.ticket.ordering.system.ticket.service.domain.dto.TicketReservationRequest;
import com.ticket.ordering.system.ticket.service.domain.event.TicketEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketreservation.TicketReservationRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TicketReservationRequestMessageListenerImpl implements TicketReservationRequestMessageListener {

    private final TicketReservationRequestHelper ticketReservationRequestHelper;

    public TicketReservationRequestMessageListenerImpl(TicketReservationRequestHelper ticketReservationRequestHelper) {
        this.ticketReservationRequestHelper = ticketReservationRequestHelper;
    }

    @Override
    @Transactional
    public void reserveTickets(TicketReservationRequest ticketReservationRequest) {
        TicketEvent ticketEvent = ticketReservationRequestHelper.persistTicketReservation(ticketReservationRequest);
        fireEvent(ticketEvent);
    }

    private void fireEvent(TicketEvent ticketEvent) {
        log.info("Publishing ticket reservation event for order id: {}", ticketEvent.getOrderId().getValue());
        ticketEvent.fire();
    }
}
