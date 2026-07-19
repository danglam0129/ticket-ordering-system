package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.entity.Ticket;
import com.ticket.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.ticket.ordering.system.domain.DomainConstants.UTC;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {
    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Ticket ticket,
                                                      DomainEventPublisher<OrderCreatedEvent>
                                                              orderCreatedEventDomainEventPublisher) {
        validateTicket(ticket);
        setOrderTicketInformation(order, ticket);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderCreatedEventDomainEventPublisher);
    }

    @Override
    public OrderPaidEvent payOrder(Order order,
                                   DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
        order.pay();
        log.info("Order with id: {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTC)), orderPaidEventDomainEventPublisher);
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} is approved", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages,
                                                  DomainEventPublisher<OrderCancelledEvent>
                                                          orderCancelledEventDomainEventPublisher) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for order id: {}", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTC)),
                orderCancelledEventDomainEventPublisher);
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled", order.getId().getValue());
    }

    private void validateTicket(Ticket ticket) {
        if (ticket == null) {
            throw new OrderDomainException("Ticket information is required!");
        }
        if(ticket.getEvent() == null || !ticket.getEvent().isActive()){
            throw new OrderDomainException("Event is not active");
        }
        if(ticket.getSeat() == null || !ticket.getSeat().isActive()){
            throw new OrderDomainException("Seat is not active");
        }
    }

    private void setOrderTicketInformation(Order order, Ticket ticket) {
        if (order == null || order.getOrderItem() == null) {
            throw new OrderDomainException("Order item information is required!");
        }
        order.getOrderItem().confirmTicketInformation(ticket);
    }

}
