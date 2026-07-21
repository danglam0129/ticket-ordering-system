package com.ticket.ordering.system.ticket.service.domain;

import com.ticket.ordering.system.ticket.service.domain.dto.TicketApprovalRequest;
import com.ticket.ordering.system.ticket.service.domain.event.TicketEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketapproval.TicketApprovalRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TicketApprovalRequestMessageListenerImpl implements TicketApprovalRequestMessageListener {

    private final TicketApprovalRequestHelper ticketApprovalRequestHelper;

    public TicketApprovalRequestMessageListenerImpl(TicketApprovalRequestHelper ticketApprovalRequestHelper) {
        this.ticketApprovalRequestHelper = ticketApprovalRequestHelper;
    }

    @Override
    @Transactional
    public void approveTickets(TicketApprovalRequest ticketApprovalRequest) {
        TicketEvent ticketEvent = ticketApprovalRequestHelper.persistTicketApproval(ticketApprovalRequest);
        fireEvent(ticketEvent);
    }

    private void fireEvent(TicketEvent ticketEvent) {
        log.info("Publishing ticket approval event for order id: {}", ticketEvent.getOrderId().getValue());
        ticketEvent.fire();
    }
}
