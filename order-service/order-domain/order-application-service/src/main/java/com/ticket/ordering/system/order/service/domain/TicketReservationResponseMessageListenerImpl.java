package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.dto.message.TicketReservationResponse;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;
import com.ticket.ordering.system.order.service.domain.ports.input.message.listener.ticketreservation.TicketReservationResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;

import static com.ticket.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class TicketReservationResponseMessageListenerImpl implements TicketReservationResponseMessageListener {

    private final OrderReservationSaga orderReservationSaga;
    private final OrderOutboxHelper orderOutboxHelper;

    public TicketReservationResponseMessageListenerImpl(OrderReservationSaga orderReservationSaga,
                                                        OrderOutboxHelper orderOutboxHelper) {
        this.orderReservationSaga = orderReservationSaga;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Override
    public void reservationApproved(TicketReservationResponse ticketReservationResponse) {
        OrderReservedEvent domainEvent = orderReservationSaga.process(ticketReservationResponse);
        orderOutboxHelper.save(domainEvent);
        log.info("Publishing OrderReservedEvent for order id: {}", ticketReservationResponse.getOrderId());
        domainEvent.fire();
    }

    @Override
    public void reservationRejected(TicketReservationResponse ticketReservationResponse) {
        orderReservationSaga.rollback(ticketReservationResponse);
        log.info("Order reservation is rejected for order id: {} with failure messages: {}",
                ticketReservationResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER,
                        ticketReservationResponse.getFailureMessages() == null ?
                                Collections.emptyList() : ticketReservationResponse.getFailureMessages()));
    }
}
