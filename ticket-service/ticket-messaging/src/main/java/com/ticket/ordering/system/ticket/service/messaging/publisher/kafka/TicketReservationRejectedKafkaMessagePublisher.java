package com.ticket.ordering.system.ticket.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationResponseAvroModel;
import com.ticket.ordering.system.ticket.service.domain.config.TicketServiceConfigData;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservationRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketreservation.TicketReservationRejectedMessagePublisher;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import com.ticket.ordering.system.ticket.service.messaging.outbox.TicketOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketReservationRejectedKafkaMessagePublisher implements TicketReservationRejectedMessagePublisher {

    private final TicketMessagingDataMapper ticketMessagingDataMapper;
    private final TicketServiceConfigData ticketServiceConfigData;
    private final TicketOutboxMessageHelper ticketOutboxMessageHelper;

    public TicketReservationRejectedKafkaMessagePublisher(TicketMessagingDataMapper ticketMessagingDataMapper,
                                                          TicketServiceConfigData ticketServiceConfigData,
                                                          TicketOutboxMessageHelper ticketOutboxMessageHelper) {
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
        this.ticketServiceConfigData = ticketServiceConfigData;
        this.ticketOutboxMessageHelper = ticketOutboxMessageHelper;
    }

    @Override
    public void publish(TicketReservationRejectedEvent domainEvent) {
        String orderId = domainEvent.getOrderId().getValue().toString();
        log.info("Received TicketReservationRejectedEvent for order id: {}", orderId);

        try {
            TicketReservationResponseAvroModel response =
                    ticketMessagingDataMapper
                            .ticketReservationRejectedEventToTicketReservationResponseAvroModel(domainEvent);
            ticketOutboxMessageHelper.save(orderId,
                    domainEvent.getClass().getSimpleName(),
                    ticketServiceConfigData.getTicketReservationResponseTopicName(),
                    orderId,
                    response.getSagaId(),
                    response);
            log.info("TicketReservationResponseAvroModel stored in outbox for rejected order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing TicketReservationResponseAvroModel in outbox for order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing TicketReservationResponseAvroModel in outbox", e);
        }
    }
}
