package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.domain.event.EmptyEvent;
import com.ticket.ordering.system.order.service.domain.dto.message.TicketReservationResponse;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderReservedPaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class OrderReservationSaga {

    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final OrderReservedPaymentRequestMessagePublisher orderReservedPaymentRequestMessagePublisher;

    public OrderReservationSaga(OrderDomainService orderDomainService,
                                OrderSagaHelper orderSagaHelper,
                                OrderReservedPaymentRequestMessagePublisher orderReservedPaymentRequestMessagePublisher) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.orderReservedPaymentRequestMessagePublisher = orderReservedPaymentRequestMessagePublisher;
    }

    @Transactional
    public OrderReservedEvent process(TicketReservationResponse ticketReservationResponse) {
        log.info("Reserving order with id: {}", ticketReservationResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(ticketReservationResponse.getOrderId());
        OrderReservedEvent domainEvent = orderDomainService.reserveOrder(order, orderReservedPaymentRequestMessagePublisher);
        orderSagaHelper.saveOrder(order);
        log.info("Order with id: {} is reserved", order.getId().getValue());
        return domainEvent;
    }

    @Transactional
    public EmptyEvent rollback(TicketReservationResponse ticketReservationResponse) {
        log.info("Cancelling order with id: {} after ticket reservation rejection",
                ticketReservationResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(ticketReservationResponse.getOrderId());
        orderDomainService.cancelOrder(order, ticketReservationResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return EmptyEvent.INSTANCE;
    }
}
