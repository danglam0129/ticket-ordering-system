package com.ticket.ordering.system.payment.service.messaging.mapper;

import com.ticket.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.ticket.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.ticket.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.ticket.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.ticket.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.ticket.ordering.system.payment.service.domain.event.PaymentEvent;
import com.ticket.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMessagingDataMapper {

    public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(
            PaymentCompletedEvent paymentCompletedEvent) {
        return paymentEventToPaymentResponseAvroModel(paymentCompletedEvent);
    }

    public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(
            PaymentCancelledEvent paymentCancelledEvent) {
        return paymentEventToPaymentResponseAvroModel(paymentCancelledEvent);
    }

    public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent) {
        return paymentEventToPaymentResponseAvroModel(paymentFailedEvent);
    }

    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
        return PaymentRequest.builder()
                .id(paymentRequestAvroModel.getId())
                .sagaId(paymentRequestAvroModel.getSagaId())
                .customerId(paymentRequestAvroModel.getCustomerId())
                .orderId(paymentRequestAvroModel.getOrderId())
                .price(paymentRequestAvroModel.getPrice())
                .createdAt(paymentRequestAvroModel.getCreatedAt())
                .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
                .build();
    }

    private PaymentResponseAvroModel paymentEventToPaymentResponseAvroModel(PaymentEvent paymentEvent) {
        return PaymentResponseAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .paymentId(paymentEvent.getPayment().getId().getValue().toString())
                .customerId(paymentEvent.getPayment().getCustomerId().getValue().toString())
                .orderId(paymentEvent.getPayment().getOrderId().getValue().toString())
                .price(paymentEvent.getPayment().getPrice().getAmount())
                .createdAt(paymentEvent.getCreatedAt().toInstant())
                .paymentStatus(com.ticket.ordering.system.kafka.order.avro.model.PaymentStatus.valueOf(
                        paymentEvent.getPayment().getPaymentStatus().name()))
                .failureMessages(paymentEvent.getFailureMessages())
                .build();
    }
}
