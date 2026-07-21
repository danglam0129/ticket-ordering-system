package com.ticket.ordering.system.ticket.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketOrderStatus;
import com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketapproval.TicketApprovalRequestMessageListener;
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
public class TicketApprovalRequestKafkaListener implements KafkaConsumer<TicketApprovalRequestAvroModel> {

    private final TicketApprovalRequestMessageListener ticketApprovalRequestMessageListener;
    private final TicketMessagingDataMapper ticketMessagingDataMapper;

    public TicketApprovalRequestKafkaListener(TicketApprovalRequestMessageListener ticketApprovalRequestMessageListener,
                                              TicketMessagingDataMapper ticketMessagingDataMapper) {
        this.ticketApprovalRequestMessageListener = ticketApprovalRequestMessageListener;
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.ticket-approval-request-consumer-group-id}",
            topics = "${ticket-service.ticket-approval-request-topic-name}")
    public void receive(@Payload List<TicketApprovalRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of ticket approval requests received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(ticketApprovalRequestAvroModel -> {
            if (TicketOrderStatus.PAID == ticketApprovalRequestAvroModel.getTicketOrderStatus()) {
                log.info("Processing ticket approval for order id: {}", ticketApprovalRequestAvroModel.getOrderId());
                ticketApprovalRequestMessageListener.approveTickets(ticketMessagingDataMapper
                        .ticketApprovalRequestAvroModelToTicketApprovalRequest(ticketApprovalRequestAvroModel));
            } else {
                log.warn("Ignoring ticket approval request with unsupported status: {} for order id: {}",
                        ticketApprovalRequestAvroModel.getTicketOrderStatus(),
                        ticketApprovalRequestAvroModel.getOrderId());
            }
        });
    }
}
