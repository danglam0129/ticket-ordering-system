package com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketapproval;

import com.ticket.ordering.system.ticket.service.domain.dto.TicketApprovalRequest;

public interface TicketApprovalRequestMessageListener {

    void approveTickets(TicketApprovalRequest ticketApprovalRequest);
}
