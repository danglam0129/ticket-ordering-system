package com.ticket.ordering.system.ticket.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.TicketOrderStatus;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationRequestAvroModel;
import com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketreservation.TicketReservationRequestMessageListener;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
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
    public void receive(List<TicketReservationRequestAvroModel> messages,
                        List<String> keys,
                        List<Integer> partitions,
                        List<Long> offsets) {
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
