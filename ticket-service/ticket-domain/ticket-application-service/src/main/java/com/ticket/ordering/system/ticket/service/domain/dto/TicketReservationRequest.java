package com.ticket.ordering.system.ticket.service.domain.dto;

import com.ticket.ordering.system.ticket.service.domain.valueobject.TicketOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TicketReservationRequest {
    private String id;
    private String sagaId;
    private String orderId;
    private List<String> ticketIds;
    private Instant createdAt;
    private TicketOrderStatus ticketOrderStatus;
}
