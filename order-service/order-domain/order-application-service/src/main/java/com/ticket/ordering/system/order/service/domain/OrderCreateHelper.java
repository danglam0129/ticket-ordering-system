package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.entity.Customer;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.ticket.ordering.system.order.service.domain.exception.OrderDomainException;
import com.ticket.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketreservation.OrderCreatedTicketReservationRequestMessagePublisher;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderIdempotencyRepository;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateHelper {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderDataMapper orderDataMapper;
    private final OrderCreatedTicketReservationRequestMessagePublisher orderCreatedEventDomainEventPublisher;
    private final OrderIdempotencyRepository orderIdempotencyRepository;

    public OrderCreateHelper(OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             CustomerRepository customerRepository,
                             OrderDataMapper orderDataMapper,
                             OrderCreatedTicketReservationRequestMessagePublisher orderCreatedEventDomainEventPublisher,
                             OrderIdempotencyRepository orderIdempotencyRepository) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderDataMapper = orderDataMapper;
        this.orderCreatedEventDomainEventPublisher = orderCreatedEventDomainEventPublisher;
        this.orderIdempotencyRepository = orderIdempotencyRepository;
    }

    @Transactional
    public OrderCreateResult persistOrder(CreateOrderCommand createOrderCommand) {
        if (hasIdempotencyKey(createOrderCommand)) {
            Optional<Order> existingOrder = orderIdempotencyRepository.findOrder(createOrderCommand.getIdempotencyKey());
            if (existingOrder.isPresent()) {
                log.info("Returning existing order for idempotency key: {}", createOrderCommand.getIdempotencyKey());
                return OrderCreateResult.alreadyProcessed(existingOrder.get());
            }
        }

        checkOrderItems(createOrderCommand);
        checkDuplicateTickets(createOrderCommand);
        checkCustomer(createOrderCommand.getCustomerId());
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order,
                orderCreatedEventDomainEventPublisher);
        Order savedOrder = saveOrder(order);
        if (hasIdempotencyKey(createOrderCommand)) {
            orderIdempotencyRepository.save(createOrderCommand.getIdempotencyKey(), savedOrder);
        }
        log.info("Order is created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return OrderCreateResult.created(orderCreatedEvent);
    }

    private void checkDuplicateTickets(CreateOrderCommand createOrderCommand) {
        Set<UUID> requestedTicketIds = new HashSet<>();
        orderDataMapper.createOrderCommandToTicketIds(createOrderCommand).forEach(ticketId -> {
            if (!requestedTicketIds.add(ticketId)) {
                throw new OrderDomainException("Duplicate ticket id in order: " + ticketId);
            }
        });
    }

    private boolean hasIdempotencyKey(CreateOrderCommand createOrderCommand) {
        return createOrderCommand.getIdempotencyKey() != null && !createOrderCommand.getIdempotencyKey().isBlank();
    }

    private void checkOrderItems(CreateOrderCommand createOrderCommand) {
        if ((createOrderCommand.getItems() == null || createOrderCommand.getItems().isEmpty()) &&
                createOrderCommand.getOrderItem() == null) {
            throw new OrderDomainException("Order must contain at least one ticket item");
        }
    }

    private void checkCustomer(UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if (customer.isEmpty()) {
            log.warn("Could not find customer with customer id: {}", customerId);
            throw new OrderDomainException("Could not find customer with customer id: " + customerId);
        }
    }

    private Order saveOrder(Order order) {
        Order orderResult = orderRepository.save(order);
        if (orderResult == null) {
            log.error("Could not save order!");
            throw new OrderDomainException("Could not save order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId().getValue());
        return orderResult;
    }
}
