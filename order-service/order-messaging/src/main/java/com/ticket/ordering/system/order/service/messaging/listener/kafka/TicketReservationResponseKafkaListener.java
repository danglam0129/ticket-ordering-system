package com.ticket.ordering.system.order.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationResponseAvroModel;
import com.ticket.ordering.system.order.service.domain.ports.input.message.listener.ticketreservation.TicketReservationResponseMessageListener;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.ticket.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Component
public class TicketReservationResponseKafkaListener implements KafkaConsumer<TicketReservationResponseAvroModel> {

    private final TicketReservationResponseMessageListener ticketReservationResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public TicketReservationResponseKafkaListener(TicketReservationResponseMessageListener
                                                          ticketReservationResponseMessageListener,
                                                  OrderMessagingDataMapper orderMessagingDataMapper) {
        this.ticketReservationResponseMessageListener = ticketReservationResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.ticket-reservation-response-consumer-group-id}",
            topics = "${order-service.ticket-reservation-response-topic-name}")
    public void receive(@Payload List<TicketReservationResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of ticket reservation responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(ticketReservationResponseAvroModel -> {
            if (OrderApprovalStatus.APPROVED == ticketReservationResponseAvroModel.getReservationStatus()) {
                log.info("Processing approved ticket reservation for order id: {}",
                        ticketReservationResponseAvroModel.getOrderId());
                ticketReservationResponseMessageListener.reservationApproved(orderMessagingDataMapper
                        .reservationResponseAvroModelToReservationResponse(ticketReservationResponseAvroModel));
            } else if (OrderApprovalStatus.REJECTED == ticketReservationResponseAvroModel.getReservationStatus()) {
                log.info("Processing rejected ticket reservation for order id: {}, with failure messages: {}",
                        ticketReservationResponseAvroModel.getOrderId(),
                        String.join(FAILURE_MESSAGE_DELIMITER,
                                ticketReservationResponseAvroModel.getFailureMessages()));
                ticketReservationResponseMessageListener.reservationRejected(orderMessagingDataMapper
                        .reservationResponseAvroModelToReservationResponse(ticketReservationResponseAvroModel));
            }
        });
    }
}
