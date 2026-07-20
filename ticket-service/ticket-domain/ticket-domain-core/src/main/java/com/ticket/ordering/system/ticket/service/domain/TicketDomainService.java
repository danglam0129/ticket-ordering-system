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

import java.util.List;

public interface TicketDomainService {

    TicketEvent validateAndReserveTickets(OrderId orderId,
                                          List<TicketId> requestedTicketIds,
                                          List<Ticket> tickets,
                                          List<String> failureMessages,
                                          DomainEventPublisher<TicketReservedEvent> ticketReservedEventPublisher,
                                          DomainEventPublisher<TicketReservationRejectedEvent>
                                                  ticketReservationRejectedEventPublisher);

    TicketEvent validateAndApproveTickets(OrderId orderId,
                                          List<TicketId> requestedTicketIds,
                                          List<Ticket> tickets,
                                          List<String> failureMessages,
                                          DomainEventPublisher<TicketApprovedEvent> ticketApprovedEventPublisher,
                                          DomainEventPublisher<TicketApprovalRejectedEvent>
                                                  ticketApprovalRejectedEventPublisher);
}
