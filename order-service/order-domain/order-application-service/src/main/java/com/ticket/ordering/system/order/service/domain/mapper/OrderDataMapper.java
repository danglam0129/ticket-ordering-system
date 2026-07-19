package com.ticket.ordering.system.order.service.domain.mapper;

import com.ticket.ordering.system.domain.valueobject.CustomerId;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ticket.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.entity.OrderItem;
import com.ticket.ordering.system.order.service.domain.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDataMapper {

    public Ticket createOrderCommandToTicket(CreateOrderCommand createOrderCommand) {
        return Ticket.builder()
                .id(new TicketId(createOrderCommand.getOrderItem().getTicketId()))
                .build();

    }

    public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
        return Order.builder()
                .customerId(new CustomerId(createOrderCommand.getCustomerId()))
                .price(new Money(createOrderCommand.getPrice()))
                .orderItem(orderItemToOrderItemEntity(createOrderCommand.getOrderItem()))
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
                .tickets(List.of(new Ticket(new TicketId(orderItem.getTicketId()))))
                .quantity(1)
                .subTotal(new Money(orderItem.getPrice()))
                .build();
    }
}
