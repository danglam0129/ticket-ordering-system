package com.ticket.ordering.system.order.service.messaging.mapper;

import com.ticket.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.ticket.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketOrderStatus;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationResponseAvroModel;
import com.ticket.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.ticket.ordering.system.order.service.domain.dto.message.TicketApprovalResponse;
import com.ticket.ordering.system.order.service.domain.dto.message.TicketReservationResponse;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderMessagingDataMapper {

    public TicketReservationRequestAvroModel orderCreatedEventToTicketReservationRequestAvroModel(
            OrderCreatedEvent orderCreatedEvent) {
        Order order = orderCreatedEvent.getOrder();
        return TicketReservationRequestAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId("")
                .orderId(order.getId().getValue().toString())
                .ticketIds(ticketIds(order))
                .createdAt(orderCreatedEvent.getCreatedAt().toInstant())
                .ticketOrderStatus(TicketOrderStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel orderReservedEventToPaymentRequestAvroModel(OrderReservedEvent orderReservedEvent) {
        Order order = orderReservedEvent.getOrder();
        return PaymentRequestAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId("")
                .customerId(order.getCustomerId().getValue().toString())
                .orderId(order.getId().getValue().toString())
                .price(order.getPrice().getAmount())
                .createdAt(orderReservedEvent.getCreatedAt().toInstant())
                .paymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();
    }

    public TicketApprovalRequestAvroModel orderPaidEventToTicketApprovalRequestAvroModel(OrderPaidEvent orderPaidEvent) {
        Order order = orderPaidEvent.getOrder();
        return TicketApprovalRequestAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId("")
                .orderId(order.getId().getValue().toString())
                .ticketIds(ticketIds(order))
                .price(order.getPrice().getAmount())
                .createdAt(orderPaidEvent.getCreatedAt().toInstant())
                .ticketOrderStatus(TicketOrderStatus.PAID)
                .build();
    }

    public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(
            OrderCancelledEvent orderCancelledEvent) {
        Order order = orderCancelledEvent.getOrder();
        return PaymentRequestAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId("")
                .customerId(order.getCustomerId().getValue().toString())
                .orderId(order.getId().getValue().toString())
                .price(order.getPrice().getAmount())
                .createdAt(orderCancelledEvent.getCreatedAt().toInstant())
                .paymentOrderStatus(PaymentOrderStatus.CANCELLED)
                .build();
    }

    public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {
        return PaymentResponse.builder()
                .id(paymentResponseAvroModel.getId())
                .sagaId(paymentResponseAvroModel.getSagaId())
                .paymentId(paymentResponseAvroModel.getPaymentId())
                .customerId(paymentResponseAvroModel.getCustomerId())
                .orderId(paymentResponseAvroModel.getOrderId())
                .price(paymentResponseAvroModel.getPrice())
                .createdAt(paymentResponseAvroModel.getCreatedAt())
                .paymentStatus(com.ticket.ordering.system.domain.valueobject.PaymentStatus.valueOf(
                        paymentResponseAvroModel.getPaymentStatus().name()))
                .failureMessages(paymentResponseAvroModel.getFailureMessages())
                .build();
    }

    public TicketReservationResponse reservationResponseAvroModelToReservationResponse(
            TicketReservationResponseAvroModel ticketReservationResponseAvroModel) {
        return TicketReservationResponse.builder()
                .id(ticketReservationResponseAvroModel.getId())
                .sagaId(ticketReservationResponseAvroModel.getSagaId())
                .orderId(ticketReservationResponseAvroModel.getOrderId())
                .ticketIds(ticketReservationResponseAvroModel.getTicketIds())
                .createdAt(ticketReservationResponseAvroModel.getCreatedAt())
                .reservationStatus(com.ticket.ordering.system.domain.valueobject.OrderApprovalStatus.valueOf(
                        ticketReservationResponseAvroModel.getReservationStatus().name()))
                .failureMessages(ticketReservationResponseAvroModel.getFailureMessages())
                .build();
    }

    public TicketApprovalResponse approvalResponseAvroModelToApprovalResponse(
            TicketApprovalResponseAvroModel ticketApprovalResponseAvroModel) {
        return TicketApprovalResponse.builder()
                .id(ticketApprovalResponseAvroModel.getId())
                .sagaId(ticketApprovalResponseAvroModel.getSagaId())
                .orderId(ticketApprovalResponseAvroModel.getOrderId())
                .ticketId(ticketApprovalResponseAvroModel.getTicketId())
                .createdAt(ticketApprovalResponseAvroModel.getCreatedAt())
                .orderApprovalStatus(com.ticket.ordering.system.domain.valueobject.OrderApprovalStatus.valueOf(
                        ticketApprovalResponseAvroModel.getOrderApprovalStatus().name()))
                .failureMessages(ticketApprovalResponseAvroModel.getFailureMessages())
                .build();
    }

    private java.util.List<String> ticketIds(Order order) {
        return order.getItems().stream()
                .map(orderItem -> orderItem.getTicketId().getValue().toString())
                .collect(Collectors.toList());
    }
}
