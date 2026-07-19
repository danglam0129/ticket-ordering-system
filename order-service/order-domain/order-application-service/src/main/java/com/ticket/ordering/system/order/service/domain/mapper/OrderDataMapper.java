package com.ticket.ordering.system.order.service.domain.mapper;

import com.ticket.ordering.system.domain.valueobject.CustomerId;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ticket.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderDataMapper {

    public List<UUID> createOrderCommandToTicketIds(CreateOrderCommand createOrderCommand) {
        return resolveOrderItems(createOrderCommand).stream()
                .map(com.ticket.ordering.system.order.service.domain.dto.create.OrderItem::getTicketId)
                .collect(Collectors.toList());
    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .price(new Money(createOrderCommand.getPrice()))
                .items(resolveOrderItems(createOrderCommand).stream()
                        .map(this::orderItemToOrderItemEntity)
                        .collect(Collectors.toList()))
                .build();
    }

    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return CreateOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .message(message)
                .build();
    }

    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return TrackOrderResponse.builder()
                .orderTrackingId(order.getTrackingId().getValue())
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .build();
    }

    private OrderItem orderItemToOrderItemEntity(
            com.ticket.ordering.system.order.service.domain.dto.create.OrderItem orderItem) {
        return OrderItem.builder()
                .ticketId(new TicketId(orderItem.getTicketId()))
                .price(new Money(orderItem.getPrice()))
                .subTotal(new Money(orderItem.getPrice()))
                .build();
    }

    private List<com.ticket.ordering.system.order.service.domain.dto.create.OrderItem> resolveOrderItems(
            CreateOrderCommand createOrderCommand) {
        if (createOrderCommand.getItems() != null && !createOrderCommand.getItems().isEmpty()) {
            return createOrderCommand.getItems();
        }
        if (createOrderCommand.getOrderItem() == null) {
            return List.of();
        }
        return List.of(createOrderCommand.getOrderItem());
    }
}
