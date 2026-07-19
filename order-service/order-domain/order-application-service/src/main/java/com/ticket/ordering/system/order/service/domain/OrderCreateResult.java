package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;

public class OrderCreateResult {
    private final Order order;
    private final OrderCreatedEvent orderCreatedEvent;
    private final boolean alreadyProcessed;

    private OrderCreateResult(Order order, OrderCreatedEvent orderCreatedEvent, boolean alreadyProcessed) {
        this.order = order;
        this.orderCreatedEvent = orderCreatedEvent;
        this.alreadyProcessed = alreadyProcessed;
    }

    public static OrderCreateResult created(OrderCreatedEvent orderCreatedEvent) {
        return new OrderCreateResult(orderCreatedEvent.getOrder(), orderCreatedEvent, false);
    }

    public static OrderCreateResult alreadyProcessed(Order order) {
        return new OrderCreateResult(order, null, true);
    }

    public Order getOrder() {
        return order;
    }

    public OrderCreatedEvent getOrderCreatedEvent() {
        return orderCreatedEvent;
    }

    public boolean isAlreadyProcessed() {
        return alreadyProcessed;
    }
}
