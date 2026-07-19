package com.ticket.ordering.system.order.service.dataaccess.order.mapper;

import com.ticket.ordering.system.domain.valueobject.CustomerId;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderOutboxEntity;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.entity.OrderItem;
import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;
import com.ticket.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.ticket.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.ticket.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Component
public class OrderDataAccessMapper {

    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .price(order.getPrice().getAmount())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages() == null ? "" :
                        String.join(FAILURE_MESSAGE_DELIMITER, order.getFailureMessages()))
                .items(orderItemsToOrderItemEntities(order.getItems()))
                .build();
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));
        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .price(new Money(orderEntity.getPrice()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages() == null || orderEntity.getFailureMessages().isEmpty() ?
                        new ArrayList<>() :
                        new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))))
                .items(orderItemEntitiesToOrderItems(orderEntity))
                .build();
    }

    public OrderOutboxEntity orderOutboxMessageToOutboxEntity(OrderOutboxMessage orderOutboxMessage) {
        return OrderOutboxEntity.builder()
                .id(orderOutboxMessage.getId())
                .aggregateId(orderOutboxMessage.getAggregateId())
                .eventType(orderOutboxMessage.getEventType())
                .payload(orderOutboxMessage.getPayload())
                .createdAt(orderOutboxMessage.getCreatedAt())
                .status(orderOutboxMessage.getStatus())
                .build();
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .ticketId(orderItem.getTicketId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(OrderEntity orderEntity) {
        return orderEntity.getItems().stream()
                .map(orderItemEntity -> OrderItem.builder()
                        .orderItemId(new OrderItemId(orderItemEntity.getId()))
                        .orderId(new OrderId(orderEntity.getId()))
                        .ticketId(new TicketId(orderItemEntity.getTicketId()))
                        .price(new Money(orderItemEntity.getPrice()))
                        .subTotal(new Money(orderItemEntity.getSubTotal()))
                        .build())
                .collect(Collectors.toList());
    }
}
