package com.ticket.ordering.system.order.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ticket.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderReservedPaymentRequestMessagePublisher;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.ticket.ordering.system.order.service.messaging.outbox.OrderOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReserveOrderKafkaMessagePublisher implements OrderReservedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderOutboxMessageHelper orderOutboxMessageHelper;

    public ReserveOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                             OrderServiceConfigData orderServiceConfigData,
                                             OrderOutboxMessageHelper orderOutboxMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderOutboxMessageHelper = orderOutboxMessageHelper;
    }

    @Override
    public void publish(OrderReservedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderReservedEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                    orderMessagingDataMapper.orderReservedEventToPaymentRequestAvroModel(domainEvent);

            orderOutboxMessageHelper.save(orderId,
                    domainEvent.getClass().getSimpleName(),
                    orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel.getSagaId(),
                    paymentRequestAvroModel);

            log.info("PaymentRequestAvroModel stored in outbox for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing PaymentRequestAvroModel in outbox with order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing PaymentRequestAvroModel in outbox", e);
        }
    }
}
