package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ticket.ordering.system.order.service.domain.mapper.OrderDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderCreateCommandHandler {

    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper,
                                     OrderDataMapper orderDataMapper) {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        OrderCreateResult orderCreateResult = orderCreateHelper.persistOrder(createOrderCommand);
        if (orderCreateResult.isAlreadyProcessed()) {
            return orderDataMapper.orderToCreateOrderResponse(orderCreateResult.getOrder(),
                    "Order already processed");
        }

        log.info("Order is created with id: {}", orderCreateResult.getOrderCreatedEvent().getOrder().getId().getValue());
        orderCreateResult.getOrderCreatedEvent().fire();
        return orderDataMapper.orderToCreateOrderResponse(orderCreateResult.getOrderCreatedEvent().getOrder(),
                "Order created successfully");
    }
}
