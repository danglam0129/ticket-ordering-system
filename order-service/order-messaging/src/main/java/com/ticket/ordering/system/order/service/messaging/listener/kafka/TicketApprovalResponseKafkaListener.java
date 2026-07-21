package com.ticket.ordering.system.order.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.order.service.domain.ports.input.message.listener.ticketapproval.TicketApprovalResponseMessageListener;
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
public class TicketApprovalResponseKafkaListener implements KafkaConsumer<TicketApprovalResponseAvroModel> {

    private final TicketApprovalResponseMessageListener ticketApprovalResponseMessageListener;
    private final OrderMessagingDataMapper orderMessagingDataMapper;

    public TicketApprovalResponseKafkaListener(TicketApprovalResponseMessageListener ticketApprovalResponseMessageListener,
                                               OrderMessagingDataMapper orderMessagingDataMapper) {
        this.ticketApprovalResponseMessageListener = ticketApprovalResponseMessageListener;
        this.orderMessagingDataMapper = orderMessagingDataMapper;
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.ticket-approval-response-consumer-group-id}",
            topics = "${order-service.ticket-approval-response-topic-name}")
    public void receive(@Payload List<TicketApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of ticket approval responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(ticketApprovalResponseAvroModel -> {
            if (OrderApprovalStatus.APPROVED == ticketApprovalResponseAvroModel.getOrderApprovalStatus()) {
                log.info("Processing approved order for order id: {}", ticketApprovalResponseAvroModel.getOrderId());
                ticketApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper
                        .approvalResponseAvroModelToApprovalResponse(ticketApprovalResponseAvroModel));
            } else if (OrderApprovalStatus.REJECTED == ticketApprovalResponseAvroModel.getOrderApprovalStatus()) {
                log.info("Processing rejected order for order id: {}, with failure messages: {}",
                        ticketApprovalResponseAvroModel.getOrderId(),
                        String.join(FAILURE_MESSAGE_DELIMITER,
                                ticketApprovalResponseAvroModel.getFailureMessages()));
                ticketApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper
                        .approvalResponseAvroModelToApprovalResponse(ticketApprovalResponseAvroModel));
            }
        });
    }
}
