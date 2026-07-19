package com.ticket.ordering.system.kafka.order.avro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class TicketApprovalRequestAvroModel {
    private String id;
    private String sagaId;
    private String orderId;
    private List<String> ticketIds;
    private BigDecimal price;
    private Instant createdAt;
    private TicketOrderStatus ticketOrderStatus;
}
