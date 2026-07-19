package com.ticket.ordering.system.order.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalRequestAvroModel;
import com.ticket.ordering.system.kafka.producer.KafkaMessageHelper;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketapproval.OrderPaidTicketRequestMessagePublisher;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidTicketRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, TicketApprovalRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper orderKafkaMessageHelper;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                         OrderServiceConfigData orderServiceConfigData,
                                         KafkaProducer<String, TicketApprovalRequestAvroModel> kafkaProducer,
                                         KafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderPaidEvent for order id: {}", orderId);

        try {
            TicketApprovalRequestAvroModel ticketApprovalRequestAvroModel =
                    orderMessagingDataMapper.orderPaidEventToTicketApprovalRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getTicketApprovalRequestTopicName(),
                    orderId,
                    ticketApprovalRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(
                            orderServiceConfigData.getTicketApprovalResponseTopicName(),
                            ticketApprovalRequestAvroModel,
                            orderId,
                            "TicketApprovalRequestAvroModel"));

            log.info("TicketApprovalRequestAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending TicketApprovalRequestAvroModel message to Kafka with order id: {}, error: {}",
                    orderId, e.getMessage());
        }
    }
}
