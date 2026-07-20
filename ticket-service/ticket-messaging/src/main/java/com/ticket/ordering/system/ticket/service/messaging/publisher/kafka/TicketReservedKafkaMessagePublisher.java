package com.ticket.ordering.system.ticket.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationResponseAvroModel;
import com.ticket.ordering.system.kafka.producer.KafkaMessageHelper;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.ticket.service.domain.config.TicketServiceConfigData;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservedEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketreservation.TicketReservedMessagePublisher;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketReservedKafkaMessagePublisher implements TicketReservedMessagePublisher {

    private final TicketMessagingDataMapper ticketMessagingDataMapper;
    private final KafkaProducer<String, TicketReservationResponseAvroModel> kafkaProducer;
    private final TicketServiceConfigData ticketServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public TicketReservedKafkaMessagePublisher(TicketMessagingDataMapper ticketMessagingDataMapper,
                                               KafkaProducer<String, TicketReservationResponseAvroModel> kafkaProducer,
                                               TicketServiceConfigData ticketServiceConfigData,
                                               KafkaMessageHelper kafkaMessageHelper) {
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.ticketServiceConfigData = ticketServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(TicketReservedEvent domainEvent) {
        String orderId = domainEvent.getOrderId().getValue().toString();
        log.info("Received TicketReservedEvent for order id: {}", orderId);

        try {
            TicketReservationResponseAvroModel response =
                    ticketMessagingDataMapper.ticketReservedEventToTicketReservationResponseAvroModel(domainEvent);
            kafkaProducer.send(ticketServiceConfigData.getTicketReservationResponseTopicName(),
                    orderId,
                    response,
                    kafkaMessageHelper.getKafkaCallback(
                            ticketServiceConfigData.getTicketReservationResponseTopicName(),
                            response,
                            orderId,
                            "TicketReservationResponseAvroModel"));
            log.info("TicketReservationResponseAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending TicketReservationResponseAvroModel for order id: {}, error: {}",
                    orderId, e.getMessage());
        }
    }
}
