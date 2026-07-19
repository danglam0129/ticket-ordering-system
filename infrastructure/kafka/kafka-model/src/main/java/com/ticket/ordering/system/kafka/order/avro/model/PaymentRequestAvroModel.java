package com.ticket.ordering.system.kafka.order.avro.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class PaymentRequestAvroModel {
    private String id;
    private String sagaId;
    private String customerId;
    private String orderId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentOrderStatus paymentOrderStatus;
}
