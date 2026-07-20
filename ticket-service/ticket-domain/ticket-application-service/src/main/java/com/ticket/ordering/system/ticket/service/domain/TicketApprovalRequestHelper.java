package com.ticket.ordering.system.ticket.service.domain;

import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.dto.TicketApprovalRequest;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;
import com.ticket.ordering.system.ticket.service.domain.event.TicketEvent;
import com.ticket.ordering.system.ticket.service.domain.mapper.TicketDataMapper;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketapproval.TicketApprovalRejectedMessagePublisher;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketapproval.TicketApprovedMessagePublisher;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TicketApprovalRequestHelper {

    private final TicketDomainService ticketDomainService;
    private final TicketDataMapper ticketDataMapper;
    private final TicketRepository ticketRepository;
    private final TicketApprovedMessagePublisher ticketApprovedMessagePublisher;
    private final TicketApprovalRejectedMessagePublisher ticketApprovalRejectedMessagePublisher;

    public TicketApprovalRequestHelper(TicketDomainService ticketDomainService,
                                       TicketDataMapper ticketDataMapper,
                                       TicketRepository ticketRepository,
                                       TicketApprovedMessagePublisher ticketApprovedMessagePublisher,
                                       TicketApprovalRejectedMessagePublisher ticketApprovalRejectedMessagePublisher) {
        this.ticketDomainService = ticketDomainService;
        this.ticketDataMapper = ticketDataMapper;
        this.ticketRepository = ticketRepository;
        this.ticketApprovedMessagePublisher = ticketApprovedMessagePublisher;
        this.ticketApprovalRejectedMessagePublisher = ticketApprovalRejectedMessagePublisher;
    }

    @Transactional
    public TicketEvent persistTicketApproval(TicketApprovalRequest ticketApprovalRequest) {
        log.info("Processing ticket approval for order id: {}", ticketApprovalRequest.getOrderId());
        OrderId orderId = ticketDataMapper.ticketApprovalRequestToOrderId(ticketApprovalRequest);
        List<TicketId> ticketIds = ticketDataMapper.ticketApprovalRequestToTicketIds(ticketApprovalRequest);
        List<Ticket> tickets = ticketRepository.findByIds(ticketDataMapper.ticketIdsToUuidList(ticketIds));
        List<String> failureMessages = new ArrayList<>();

        TicketEvent ticketEvent = ticketDomainService.validateAndApproveTickets(orderId, ticketIds, tickets,
                failureMessages, ticketApprovedMessagePublisher, ticketApprovalRejectedMessagePublisher);
        persistTickets(tickets, failureMessages);
        return ticketEvent;
    }

    private void persistTickets(List<Ticket> tickets, List<String> failureMessages) {
        if (failureMessages.isEmpty()) {
            ticketRepository.saveAll(tickets);
        }
    }
}
