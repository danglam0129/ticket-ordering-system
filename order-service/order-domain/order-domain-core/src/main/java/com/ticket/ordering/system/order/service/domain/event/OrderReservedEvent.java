package com.ticket.ordering.system.order.service.domain.event;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderReservedEvent extends OrderEvent {
    private final DomainEventPublisher<OrderReservedEvent> orderReservedEventDomainEventPublisher;

    public OrderReservedEvent(Order order,
                              ZonedDateTime createdAt,
                              DomainEventPublisher<OrderReservedEvent> orderReservedEventDomainEventPublisher) {
        super(order, createdAt);
        this.orderReservedEventDomainEventPublisher = orderReservedEventDomainEventPublisher;
    }

    @Override
    public void fire() {
        orderReservedEventDomainEventPublisher.publish(this);
    }
}
