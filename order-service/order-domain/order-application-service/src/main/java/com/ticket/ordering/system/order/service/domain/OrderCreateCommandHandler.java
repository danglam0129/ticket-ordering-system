package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ticket.ordering.system.order.service.domain.mapper.OrderDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final OrderOutboxHelper orderOutboxHelper;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper,
                                     OrderDataMapper orderDataMapper,
                                     OrderOutboxHelper orderOutboxHelper) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreateResult orderCreateResult = orderCreateHelper.persistOrder(createOrderCommand);
        if (orderCreateResult.isAlreadyProcessed()) {
            return orderDataMapper.orderToCreateOrderResponse(orderCreateResult.getOrder(),
                    "Order already processed");
        }

        log.info("Order is created with id: {}", orderCreateResult.getOrderCreatedEvent().getOrder().getId().getValue());
        orderOutboxHelper.save(orderCreateResult.getOrderCreatedEvent());
        orderCreateResult.getOrderCreatedEvent().fire();
        return orderDataMapper.orderToCreateOrderResponse(orderCreateResult.getOrderCreatedEvent().getOrder(),
                "Order created successfully");
    }
}
