package com.ticket.ordering.system.ticket.service.domain;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.entity.Ticket;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovalRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovedEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservationRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservedEvent;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ticket.ordering.system.domain.DomainConstants.UTC;

@Slf4j
public class TicketDomainServiceImpl implements TicketDomainService {

    @Override
    public TicketEvent validateAndReserveTickets(OrderId orderId,
                                                 List<TicketId> requestedTicketIds,
                                                 List<Ticket> tickets,
                                                 List<String> failureMessages,
                                                 DomainEventPublisher<TicketReservedEvent>
                                                         ticketReservedEventPublisher,
                                                 DomainEventPublisher<TicketReservationRejectedEvent>
                                                         ticketReservationRejectedEventPublisher) {
        validateTicketRequest(orderId, requestedTicketIds, tickets, failureMessages);

        if (failureMessages.isEmpty()) {
            tickets.forEach(ticket -> ticket.validateForReservation(orderId, failureMessages));
        }
        if (failureMessages.isEmpty()) {
            tickets.forEach(ticket -> ticket.reserve(orderId));
            log.info("Tickets are reserved for order id: {}", orderId.getValue());
            return new TicketReservedEvent(orderId, requestedTicketIds, tickets, failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)), ticketReservedEventPublisher);
        }

        log.info("Ticket reservation rejected for order id: {}", getOrderId(orderId));
        return new TicketReservationRejectedEvent(orderId, requestedTicketIds, tickets, failureMessages,
                ZonedDateTime.now(ZoneId.of(UTC)), ticketReservationRejectedEventPublisher);
    }

    @Override
    public TicketEvent validateAndApproveTickets(OrderId orderId,
                                                 List<TicketId> requestedTicketIds,
                                                 List<Ticket> tickets,
                                                 List<String> failureMessages,
                                                 DomainEventPublisher<TicketApprovedEvent>
                                                         ticketApprovedEventPublisher,
                                                 DomainEventPublisher<TicketApprovalRejectedEvent>
                                                         ticketApprovalRejectedEventPublisher) {
        validateTicketRequest(orderId, requestedTicketIds, tickets, failureMessages);

        if (failureMessages.isEmpty()) {
            tickets.forEach(ticket -> ticket.validateForApproval(orderId, failureMessages));
        }
        if (failureMessages.isEmpty()) {
            tickets.forEach(Ticket::approve);
            log.info("Tickets are approved for order id: {}", orderId.getValue());
            return new TicketApprovedEvent(orderId, requestedTicketIds, tickets, failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC)), ticketApprovedEventPublisher);
        }

        log.info("Ticket approval rejected for order id: {}", getOrderId(orderId));
        return new TicketApprovalRejectedEvent(orderId, requestedTicketIds, tickets, failureMessages,
                ZonedDateTime.now(ZoneId.of(UTC)), ticketApprovalRejectedEventPublisher);
    }

    private void validateTicketRequest(OrderId orderId,
                                       List<TicketId> requestedTicketIds,
                                       List<Ticket> tickets,
                                       List<String> failureMessages) {
        if (orderId == null) {
            failureMessages.add("Order id must be present!");
        }
        if (requestedTicketIds == null || requestedTicketIds.isEmpty()) {
            failureMessages.add("Ticket ids must be present!");
            return;
        }

        Set<TicketId> uniqueTicketIds = new HashSet<>(requestedTicketIds);
        if (uniqueTicketIds.size() != requestedTicketIds.size()) {
            failureMessages.add("Duplicate ticket ids are not allowed!");
        }

        Set<TicketId> foundTicketIds = tickets.stream().map(Ticket::getId).collect(Collectors.toSet());
        uniqueTicketIds.stream()
                .filter(ticketId -> !foundTicketIds.contains(ticketId))
                .forEach(ticketId -> failureMessages.add("Ticket with id=" + ticketId.getValue() + " not found!"));
    }

    private String getOrderId(OrderId orderId) {
        return orderId == null ? "unknown" : orderId.getValue().toString();
    }
}
