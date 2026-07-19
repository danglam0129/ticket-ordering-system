package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;

import java.util.List;

public interface OrderDomainService {
    OrderCreatedEvent validateAndInitiateOrder(Order order, DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher);

    OrderReservedEvent reserveOrder(Order order, DomainEventPublisher<OrderReservedEvent> orderReservedEventDomainEventPublisher);

    OrderPaidEvent payOrder(Order order, DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages, DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher);

    void cancelOrder(Order order, List<String> failureMessages);
}
