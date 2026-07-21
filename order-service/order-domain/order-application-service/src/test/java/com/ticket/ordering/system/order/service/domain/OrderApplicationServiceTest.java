package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.domain.valueobject.CustomerId;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.OrderStatus;
import com.ticket.ordering.system.domain.valueobject.PaymentStatus;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ticket.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.ticket.ordering.system.order.service.domain.dto.message.TicketApprovalResponse;
import com.ticket.ordering.system.order.service.domain.dto.message.TicketReservationResponse;
import com.ticket.ordering.system.order.service.domain.entity.Customer;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.entity.OrderItem;
import com.ticket.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;
import com.ticket.ordering.system.order.service.domain.exception.OrderDomainException;
import com.ticket.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketreservation.OrderCreatedTicketReservationRequestMessagePublisher;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderIdempotencyRepository;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.ticket.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.ticket.ordering.system.order.service.domain.valueobject.TrackingId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = OrderTestConfiguration.class)
class OrderApplicationServiceTest {

    private static final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private static final UUID TICKET_ID_1 = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    private static final UUID TICKET_ID_2 = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb49");
    private static final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    private static final UUID TRACKING_ID = UUID.fromString("35b41d71-80da-4e3f-9b0f-389dc99f9b2d");
    private static final BigDecimal PRICE = new BigDecimal("200.00");
    private static final BigDecimal TICKET_PRICE = new BigDecimal("100.00");

    @Autowired
    private OrderApplicationService orderApplicationService;

    @Autowired
    private OrderReservationSaga orderReservationSaga;

    @Autowired
    private OrderPaymentSaga orderPaymentSaga;

    @Autowired
    private OrderApprovalSaga orderApprovalSaga;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderIdempotencyRepository orderIdempotencyRepository;

    @Autowired
    private OrderCreatedTicketReservationRequestMessagePublisher orderCreatedTicketReservationRequestMessagePublisher;

    @BeforeEach
    void init() {
        reset(orderRepository,
                customerRepository,
                orderIdempotencyRepository,
                orderCreatedTicketReservationRequestMessagePublisher);

        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(new Customer(new CustomerId(CUSTOMER_ID))));
        when(orderIdempotencyRepository.findOrder(anyString())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testCreateOrder() {
        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand());

        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order created successfully", createOrderResponse.getMessage());
        assertNotNull(createOrderResponse.getOrderTrackingId());
        verify(orderRepository).save(any(Order.class));
        verify(orderCreatedTicketReservationRequestMessagePublisher).publish(any());
    }

    @Test
    void testCreateOrderWithWrongTotalPrice() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand(new BigDecimal("250.00"), TICKET_ID_1, TICKET_ID_2)));

        assertEquals("Total price doesn't match", orderDomainException.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithDuplicateTicket() {
        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand(PRICE, TICKET_ID_1, TICKET_ID_1)));

        assertEquals("Duplicate ticket id in order: " + TICKET_ID_1, orderDomainException.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithInvalidCustomer() {
        when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.empty());

        OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
                () -> orderApplicationService.createOrder(createOrderCommand()));

        assertEquals("Could not find customer with customer id: " + CUSTOMER_ID, orderDomainException.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void testCreateOrderWithExistingIdempotencyKey() {
        Order existingOrder = orderWithStatus(OrderStatus.PENDING);
        when(orderIdempotencyRepository.findOrder("create-order-1")).thenReturn(Optional.of(existingOrder));

        CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommandWithIdempotencyKey());

        assertEquals(TRACKING_ID, createOrderResponse.getOrderTrackingId());
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertEquals("Order already processed", createOrderResponse.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderCreatedTicketReservationRequestMessagePublisher, never()).publish(any());
    }

    @Test
    void testReservationApprovedReservesOrder() {
        Order order = orderWithStatus(OrderStatus.PENDING);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        OrderReservedEvent orderReservedEvent = orderReservationSaga.process(ticketReservationResponse(OrderApprovalStatus.APPROVED));

        assertEquals(OrderStatus.RESERVED, orderReservedEvent.getOrder().getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testReservationRejectedCancelsPendingOrder() {
        Order order = orderWithStatus(OrderStatus.PENDING);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        orderReservationSaga.rollback(ticketReservationResponse(OrderApprovalStatus.REJECTED));

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertFalse(order.getFailureMessages().isEmpty());
        verify(orderRepository).save(order);
    }

    @Test
    void testPaymentCompletedPaysReservedOrder() {
        Order order = orderWithStatus(OrderStatus.RESERVED);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        OrderPaidEvent orderPaidEvent = orderPaymentSaga.process(paymentResponse(PaymentStatus.COMPLETED));

        assertEquals(OrderStatus.PAID, orderPaidEvent.getOrder().getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testPaymentCancelledCancelsReservedOrder() {
        Order order = orderWithStatus(OrderStatus.RESERVED);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        orderPaymentSaga.rollback(paymentResponse(PaymentStatus.FAILED));

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertFalse(order.getFailureMessages().isEmpty());
        verify(orderRepository).save(order);
    }

    @Test
    void testTicketApprovalApprovedApprovesPaidOrder() {
        Order order = orderWithStatus(OrderStatus.PAID);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        orderApprovalSaga.process(ticketApprovalResponse(OrderApprovalStatus.APPROVED));

        assertEquals(OrderStatus.APPROVED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testTicketApprovalRejectedStartsPaymentCancellation() {
        Order order = orderWithStatus(OrderStatus.PAID);
        when(orderRepository.findById(any(OrderId.class))).thenReturn(Optional.of(order));

        OrderCancelledEvent orderCancelledEvent = orderApprovalSaga.rollback(ticketApprovalResponse(OrderApprovalStatus.REJECTED));

        assertEquals(OrderStatus.CANCELLING, orderCancelledEvent.getOrder().getOrderStatus());
        assertFalse(order.getFailureMessages().isEmpty());
        verify(orderRepository).save(order);
    }

    private CreateOrderCommand createOrderCommand() {
        return createOrderCommand(PRICE, TICKET_ID_1, TICKET_ID_2);
    }

    private CreateOrderCommand createOrderCommandWithIdempotencyKey() {
        return CreateOrderCommand.builder()
                .idempotencyKey("create-order-1")
                .customerId(CUSTOMER_ID)
                .price(PRICE)
                .items(orderItemCommands(TICKET_ID_1, TICKET_ID_2))
                .build();
    }

    private CreateOrderCommand createOrderCommand(BigDecimal price, UUID firstTicketId, UUID secondTicketId) {
        return CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .price(price)
                .items(orderItemCommands(firstTicketId, secondTicketId))
                .build();
    }

    private List<com.ticket.ordering.system.order.service.domain.dto.create.OrderItem> orderItemCommands(UUID firstTicketId,
                                                                                                         UUID secondTicketId) {
        return List.of(
                com.ticket.ordering.system.order.service.domain.dto.create.OrderItem.builder()
                        .ticketId(firstTicketId)
                        .price(TICKET_PRICE)
                        .build(),
                com.ticket.ordering.system.order.service.domain.dto.create.OrderItem.builder()
                        .ticketId(secondTicketId)
                        .price(TICKET_PRICE)
                        .build());
    }

    private Order orderWithStatus(OrderStatus orderStatus) {
        return Order.builder()
                .orderId(new OrderId(ORDER_ID))
                .customerId(new CustomerId(CUSTOMER_ID))
                .items(List.of(orderItem(1L, TICKET_ID_1), orderItem(2L, TICKET_ID_2)))
                .price(new Money(PRICE))
                .failureMessages(new ArrayList<>())
                .trackingId(new TrackingId(TRACKING_ID))
                .orderStatus(orderStatus)
                .build();
    }

    private OrderItem orderItem(Long orderItemId, UUID ticketId) {
        return OrderItem.builder()
                .orderItemId(new OrderItemId(orderItemId))
                .orderId(new OrderId(ORDER_ID))
                .ticketId(new TicketId(ticketId))
                .price(new Money(TICKET_PRICE))
                .subTotal(new Money(TICKET_PRICE))
                .build();
    }

    private TicketReservationResponse ticketReservationResponse(OrderApprovalStatus orderApprovalStatus) {
        return TicketReservationResponse.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(UUID.randomUUID().toString())
                .orderId(ORDER_ID.toString())
                .ticketIds(List.of(TICKET_ID_1.toString(), TICKET_ID_2.toString()))
                .createdAt(Instant.now())
                .reservationStatus(orderApprovalStatus)
                .failureMessages(List.of("ticket is not available"))
                .build();
    }

    private PaymentResponse paymentResponse(PaymentStatus paymentStatus) {
        return PaymentResponse.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(UUID.randomUUID().toString())
                .orderId(ORDER_ID.toString())
                .paymentId(UUID.randomUUID().toString())
                .customerId(CUSTOMER_ID.toString())
                .price(PRICE)
                .createdAt(Instant.now())
                .paymentStatus(paymentStatus)
                .failureMessages(List.of("payment failed"))
                .build();
    }

    private TicketApprovalResponse ticketApprovalResponse(OrderApprovalStatus orderApprovalStatus) {
        return TicketApprovalResponse.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(UUID.randomUUID().toString())
                .orderId(ORDER_ID.toString())
                .ticketId(TICKET_ID_1.toString())
                .createdAt(Instant.now())
                .orderApprovalStatus(orderApprovalStatus)
                .failureMessages(List.of("ticket approval failed"))
                .build();
    }
}
