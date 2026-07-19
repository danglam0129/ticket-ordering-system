package com.ticket.ordering.system.order.service.domain.ports.input.message.listener.ticketapproval;

import com.ticket.ordering.system.order.service.domain.dto.message.TicketApprovalResponse;

public interface TicketApprovalResponseMessageListener {
    void orderApproved(TicketApprovalResponse ticketApprovalResponse);

    void orderRejected(TicketApprovalResponse ticketApprovalResponse);
}
