package com.ticket.ordering.system.ticket.service.domain.dto;

import com.ticket.ordering.system.ticket.service.domain.valueobject.TicketOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TicketApprovalRequest {
    private String id;
    private String sagaId;
    private String orderId;
    private List<String> ticketIds;
    private BigDecimal price;
    private Instant createdAt;
    private TicketOrderStatus ticketOrderStatus;
}
