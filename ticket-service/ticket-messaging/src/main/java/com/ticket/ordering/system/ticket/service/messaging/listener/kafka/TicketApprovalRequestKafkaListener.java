package com.ticket.ordering.system.ticket.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketOrderStatus;
import com.ticket.ordering.system.ticket.service.domain.ports.input.message.listener.ticketapproval.TicketApprovalRequestMessageListener;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
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
    public void receive(List<TicketApprovalRequestAvroModel> messages,
                        List<String> keys,
                        List<Integer> partitions,
                        List<Long> offsets) {
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
