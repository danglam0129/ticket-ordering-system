package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.dto.message.TicketApprovalResponse;
import com.ticket.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.ticket.ordering.system.order.service.domain.ports.input.message.listener.ticketapproval.TicketApprovalResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;

import static com.ticket.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class TicketApprovalResponseMessageListenerImpl implements TicketApprovalResponseMessageListener {

    private final OrderApprovalSaga orderApprovalSaga;

    public TicketApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga) {
        this.orderApprovalSaga = orderApprovalSaga;
    }

    @Override
    public void orderApproved(TicketApprovalResponse ticketApprovalResponse) {
        orderApprovalSaga.process(ticketApprovalResponse);
        log.info("Order is approved for order id: {}", ticketApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(TicketApprovalResponse ticketApprovalResponse) {
        OrderCancelledEvent domainEvent = orderApprovalSaga.rollback(ticketApprovalResponse);
        log.info("Publishing order cancelled event for order id: {} with failure messages: {}",
                ticketApprovalResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER,
                        ticketApprovalResponse.getFailureMessages() == null ?
                                Collections.emptyList() : ticketApprovalResponse.getFailureMessages()));
        domainEvent.fire();
    }
}
