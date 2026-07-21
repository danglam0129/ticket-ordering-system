package com.ticket.ordering.system.payment.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.ticket.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.ticket.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.ticket.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.ticket.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import com.ticket.ordering.system.payment.service.messaging.outbox.PaymentOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCompletedKafkaMessagePublisher implements PaymentCompletedMessagePublisher {

    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final PaymentOutboxMessageHelper paymentOutboxMessageHelper;

    public PaymentCompletedKafkaMessagePublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                                 PaymentServiceConfigData paymentServiceConfigData,
                                                 PaymentOutboxMessageHelper paymentOutboxMessageHelper) {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.paymentOutboxMessageHelper = paymentOutboxMessageHelper;
    }

    @Override
    public void publish(PaymentCompletedEvent domainEvent) {
        String orderId = domainEvent.getPayment().getOrderId().getValue().toString();
        log.info("Received PaymentCompletedEvent for order id: {}", orderId);

        try {
            PaymentResponseAvroModel paymentResponseAvroModel =
                    paymentMessagingDataMapper.paymentCompletedEventToPaymentResponseAvroModel(domainEvent);
            paymentOutboxMessageHelper.save(domainEvent.getPayment().getId().getValue().toString(),
                    domainEvent.getClass().getSimpleName(),
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    orderId,
                    paymentResponseAvroModel.getSagaId(),
                    paymentResponseAvroModel);
            log.info("PaymentResponseAvroModel stored in outbox for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing PaymentResponseAvroModel in outbox with order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing PaymentResponseAvroModel in outbox", e);
        }
    }
}
