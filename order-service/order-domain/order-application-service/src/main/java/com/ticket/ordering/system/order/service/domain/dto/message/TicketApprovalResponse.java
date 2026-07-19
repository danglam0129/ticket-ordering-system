package com.ticket.ordering.system.order.service.domain.dto.message;

import com.ticket.ordering.system.domain.valueobject.OrderApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TicketApprovalResponse {
    private String id;
    private String sagaId;
    private String orderId;
    private String ticketId;
    private Instant createdAt;
    private OrderApprovalStatus orderApprovalStatus;
    private List<String> failureMessages;
}
