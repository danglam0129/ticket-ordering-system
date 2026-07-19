package com.ticket.ordering.system.order.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ticket.ordering.system.kafka.producer.KafkaMessageHelper;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.ticket.ordering.system.order.service.domain.event.OrderReservedEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderReservedPaymentRequestMessagePublisher;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReserveOrderKafkaMessagePublisher implements OrderReservedPaymentRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper orderKafkaMessageHelper;

    public ReserveOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                             OrderServiceConfigData orderServiceConfigData,
                                             KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                             KafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }

    @Override
    public void publish(OrderReservedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderReservedEvent for order id: {}", orderId);

        try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                    orderMessagingDataMapper.orderReservedEventToPaymentRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                    orderId,
                    paymentRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentResponseTopicName(),
                            paymentRequestAvroModel,
                            orderId,
                            "PaymentRequestAvroModel"));

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to Kafka with order id: {}, error: {}",
                    orderId, e.getMessage());
        }
    }
}
