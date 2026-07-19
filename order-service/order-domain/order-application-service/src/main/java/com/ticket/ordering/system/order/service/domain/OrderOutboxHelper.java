package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.event.OrderEvent;
import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;
import com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderOutboxRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class OrderOutboxHelper {

    private final OrderOutboxRepository orderOutboxRepository;

    public OrderOutboxHelper(OrderOutboxRepository orderOutboxRepository) {
        this.orderOutboxRepository = orderOutboxRepository;
    }

    public void save(OrderEvent orderEvent) {
        orderOutboxRepository.save(OrderOutboxMessage.builder()
                .id(UUID.randomUUID())
                .aggregateId(orderEvent.getOrder().getId().getValue().toString())
                .eventType(orderEvent.getClass().getSimpleName())
                .payload("orderId=" + orderEvent.getOrder().getId().getValue())
                .createdAt(Instant.now())
                .status(OutboxStatus.STARTED)
                .build());
    }
}
