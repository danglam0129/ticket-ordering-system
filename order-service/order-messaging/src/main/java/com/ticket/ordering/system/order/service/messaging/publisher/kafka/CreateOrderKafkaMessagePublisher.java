package com.ticket.ordering.system.order.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationRequestAvroModel;
import com.ticket.ordering.system.kafka.producer.KafkaMessageHelper;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketreservation.OrderCreatedTicketReservationRequestMessagePublisher;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedTicketReservationRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final KafkaProducer<String, TicketReservationRequestAvroModel> kafkaProducer;
    private final KafkaMessageHelper orderKafkaMessageHelper;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            KafkaProducer<String, TicketReservationRequestAvroModel> kafkaProducer,
                                            KafkaMessageHelper orderKafkaMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.kafkaProducer = kafkaProducer;
        this.orderKafkaMessageHelper = orderKafkaMessageHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCreatedEvent for order id: {}", orderId);

        try {
            TicketReservationRequestAvroModel ticketReservationRequestAvroModel =
                    orderMessagingDataMapper.orderCreatedEventToTicketReservationRequestAvroModel(domainEvent);

            kafkaProducer.send(orderServiceConfigData.getTicketReservationRequestTopicName(),
                    orderId,
                    ticketReservationRequestAvroModel,
                    orderKafkaMessageHelper.getKafkaCallback(
                            orderServiceConfigData.getTicketReservationResponseTopicName(),
                            ticketReservationRequestAvroModel,
                            orderId,
                            "TicketReservationRequestAvroModel"));

            log.info("TicketReservationRequestAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending TicketReservationRequestAvroModel message to Kafka with order id: {}, error: {}",
                    orderId, e.getMessage());
        }
    }
}
