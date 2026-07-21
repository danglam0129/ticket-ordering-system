package com.ticket.ordering.system.ticket.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.ticket.service.domain.config.TicketServiceConfigData;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovalRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketapproval.TicketApprovalRejectedMessagePublisher;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import com.ticket.ordering.system.ticket.service.messaging.outbox.TicketOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketApprovalRejectedKafkaMessagePublisher implements TicketApprovalRejectedMessagePublisher {

    private final TicketMessagingDataMapper ticketMessagingDataMapper;
    private final TicketServiceConfigData ticketServiceConfigData;
    private final TicketOutboxMessageHelper ticketOutboxMessageHelper;

    public TicketApprovalRejectedKafkaMessagePublisher(TicketMessagingDataMapper ticketMessagingDataMapper,
                                                       TicketServiceConfigData ticketServiceConfigData,
                                                       TicketOutboxMessageHelper ticketOutboxMessageHelper) {
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
        this.ticketServiceConfigData = ticketServiceConfigData;
        this.ticketOutboxMessageHelper = ticketOutboxMessageHelper;
    }

    @Override
    public void publish(TicketApprovalRejectedEvent domainEvent) {
        String orderId = domainEvent.getOrderId().getValue().toString();
        log.info("Received TicketApprovalRejectedEvent for order id: {}", orderId);

        try {
            TicketApprovalResponseAvroModel response =
                    ticketMessagingDataMapper.ticketApprovalRejectedEventToTicketApprovalResponseAvroModel(domainEvent);
            ticketOutboxMessageHelper.save(orderId,
                    domainEvent.getClass().getSimpleName(),
                    ticketServiceConfigData.getTicketApprovalResponseTopicName(),
                    orderId,
                    response.getSagaId(),
                    response);
            log.info("TicketApprovalResponseAvroModel stored in outbox for rejected order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing TicketApprovalResponseAvroModel in outbox for order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing TicketApprovalResponseAvroModel in outbox", e);
        }
    }
}
