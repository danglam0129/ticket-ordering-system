package com.ticket.ordering.system.ticket.service.domain;

import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.dto.TicketReservationRequest;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;
import com.ticket.ordering.system.ticket.service.domain.event.TicketEvent;
import com.ticket.ordering.system.ticket.service.domain.mapper.TicketDataMapper;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketreservation.TicketReservationRejectedMessagePublisher;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketreservation.TicketReservedMessagePublisher;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketReservationHoldRepository;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TicketReservationRequestHelper {

    private final TicketDomainService ticketDomainService;
    private final TicketDataMapper ticketDataMapper;
    private final TicketRepository ticketRepository;
    private final TicketReservationHoldRepository ticketReservationHoldRepository;
    private final TicketReservedMessagePublisher ticketReservedMessagePublisher;
    private final TicketReservationRejectedMessagePublisher ticketReservationRejectedMessagePublisher;

    public TicketReservationRequestHelper(TicketDomainService ticketDomainService,
                                          TicketDataMapper ticketDataMapper,
                                          TicketRepository ticketRepository,
                                          TicketReservationHoldRepository ticketReservationHoldRepository,
                                          TicketReservedMessagePublisher ticketReservedMessagePublisher,
                                          TicketReservationRejectedMessagePublisher
                                                  ticketReservationRejectedMessagePublisher) {
        this.ticketDomainService = ticketDomainService;
        this.ticketDataMapper = ticketDataMapper;
        this.ticketRepository = ticketRepository;
        this.ticketReservationHoldRepository = ticketReservationHoldRepository;
        this.ticketReservedMessagePublisher = ticketReservedMessagePublisher;
        this.ticketReservationRejectedMessagePublisher = ticketReservationRejectedMessagePublisher;
    }

    @Transactional
    public TicketEvent persistTicketReservation(TicketReservationRequest ticketReservationRequest) {
        log.info("Processing ticket reservation for order id: {}", ticketReservationRequest.getOrderId());
        OrderId orderId = ticketDataMapper.ticketReservationRequestToOrderId(ticketReservationRequest);
        List<TicketId> ticketIds = ticketDataMapper.ticketReservationRequestToTicketIds(ticketReservationRequest);
        List<String> failureMessages = new ArrayList<>(ticketReservationHoldRepository.holdTickets(orderId, ticketIds));
        List<Ticket> tickets = ticketRepository.findByIds(ticketDataMapper.ticketIdsToUuidList(ticketIds));

        TicketEvent ticketEvent = ticketDomainService.validateAndReserveTickets(orderId, ticketIds, tickets,
                failureMessages, ticketReservedMessagePublisher, ticketReservationRejectedMessagePublisher);
        persistTickets(tickets, failureMessages);
        releaseTicketHoldsAfterTransaction(orderId, ticketIds);
        return ticketEvent;
    }

    private void persistTickets(List<Ticket> tickets, List<String> failureMessages) {
        if (failureMessages.isEmpty()) {
            ticketRepository.saveAll(tickets);
        }
    }

    private void releaseTicketHoldsAfterTransaction(OrderId orderId, List<TicketId> ticketIds) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    ticketReservationHoldRepository.releaseTickets(orderId, ticketIds);
                }
            });
            return;
        }

        ticketReservationHoldRepository.releaseTickets(orderId, ticketIds);
    }
}
