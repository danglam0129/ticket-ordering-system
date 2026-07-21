package com.ticket.ordering.system.ticket.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.TicketOrderStatus;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationRequestAvroModel;
import com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketreservation.TicketReservationRequestMessageListener;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TicketReservationRequestKafkaListener implements KafkaConsumer<TicketReservationRequestAvroModel> {

    private final TicketReservationRequestMessageListener ticketReservationRequestMessageListener;
    private final TicketMessagingDataMapper ticketMessagingDataMapper;

    public TicketReservationRequestKafkaListener(TicketReservationRequestMessageListener
                                                         ticketReservationRequestMessageListener,
                                                 TicketMessagingDataMapper ticketMessagingDataMapper) {
        this.ticketReservationRequestMessageListener = ticketReservationRequestMessageListener;
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.ticket-reservation-request-consumer-group-id}",
            topics = "${ticket-service.ticket-reservation-request-topic-name}")
    public void receive(@Payload List<TicketReservationRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of ticket reservation requests received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(ticketReservationRequestAvroModel -> {
            if (TicketOrderStatus.PENDING == ticketReservationRequestAvroModel.getTicketOrderStatus()) {
                log.info("Processing ticket reservation for order id: {}",
                        ticketReservationRequestAvroModel.getOrderId());
                ticketReservationRequestMessageListener.reserveTickets(ticketMessagingDataMapper
                        .ticketReservationRequestAvroModelToTicketReservationRequest(
                                ticketReservationRequestAvroModel));
            } else {
                log.warn("Ignoring ticket reservation request with unsupported status: {} for order id: {}",
                        ticketReservationRequestAvroModel.getTicketOrderStatus(),
                        ticketReservationRequestAvroModel.getOrderId());
            }
        });
    }
}
