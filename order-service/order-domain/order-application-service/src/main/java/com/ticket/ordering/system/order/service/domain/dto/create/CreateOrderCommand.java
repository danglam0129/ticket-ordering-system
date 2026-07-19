package com.ticket.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {
    private final String idempotencyKey;
    @NotNull
    private final UUID customerId;
    @NotNull
    private final BigDecimal price;
    private final List<OrderItem> items;
    private final OrderItem orderItem;
}
