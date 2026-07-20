package com.ticket.ordering.system.ticket.service.domain.mapper;

import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.dto.TicketApprovalRequest;
import com.ticket.ordering.system.ticket.service.domain.dto.TicketReservationRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TicketDataMapper {

    public OrderId ticketReservationRequestToOrderId(TicketReservationRequest ticketReservationRequest) {
        return new OrderId(UUID.fromString(ticketReservationRequest.getOrderId()));
    }

    public List<TicketId> ticketReservationRequestToTicketIds(TicketReservationRequest ticketReservationRequest) {
        return ticketReservationRequest.getTicketIds().stream()
                .map(ticketId -> new TicketId(UUID.fromString(ticketId)))
                .collect(Collectors.toList());
    }

    public OrderId ticketApprovalRequestToOrderId(TicketApprovalRequest ticketApprovalRequest) {
        return new OrderId(UUID.fromString(ticketApprovalRequest.getOrderId()));
    }

    public List<TicketId> ticketApprovalRequestToTicketIds(TicketApprovalRequest ticketApprovalRequest) {
        return ticketApprovalRequest.getTicketIds().stream()
                .map(ticketId -> new TicketId(UUID.fromString(ticketId)))
                .collect(Collectors.toList());
    }

    public List<UUID> ticketIdsToUuidList(List<TicketId> ticketIds) {
        return ticketIds.stream()
                .map(TicketId::getValue)
                .collect(Collectors.toList());
    }
}
