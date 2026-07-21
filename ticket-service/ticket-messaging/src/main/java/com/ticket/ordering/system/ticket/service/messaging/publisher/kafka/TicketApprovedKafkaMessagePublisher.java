package com.ticket.ordering.system.ticket.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.ticket.service.domain.config.TicketServiceConfigData;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovedEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketapproval.TicketApprovedMessagePublisher;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import com.ticket.ordering.system.ticket.service.messaging.outbox.TicketOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketApprovedKafkaMessagePublisher implements TicketApprovedMessagePublisher {

    private final TicketMessagingDataMapper ticketMessagingDataMapper;
    private final TicketServiceConfigData ticketServiceConfigData;
    private final TicketOutboxMessageHelper ticketOutboxMessageHelper;

    public TicketApprovedKafkaMessagePublisher(TicketMessagingDataMapper ticketMessagingDataMapper,
                                               TicketServiceConfigData ticketServiceConfigData,
                                               TicketOutboxMessageHelper ticketOutboxMessageHelper) {
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
        this.ticketServiceConfigData = ticketServiceConfigData;
        this.ticketOutboxMessageHelper = ticketOutboxMessageHelper;
    }

    @Override
    public void publish(TicketApprovedEvent domainEvent) {
        String orderId = domainEvent.getOrderId().getValue().toString();
        log.info("Received TicketApprovedEvent for order id: {}", orderId);

        try {
            TicketApprovalResponseAvroModel response =
                    ticketMessagingDataMapper.ticketApprovedEventToTicketApprovalResponseAvroModel(domainEvent);
            ticketOutboxMessageHelper.save(orderId,
                    domainEvent.getClass().getSimpleName(),
                    ticketServiceConfigData.getTicketApprovalResponseTopicName(),
                    orderId,
                    response.getSagaId(),
                    response);
            log.info("TicketApprovalResponseAvroModel stored in outbox for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing TicketApprovalResponseAvroModel in outbox for order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing TicketApprovalResponseAvroModel in outbox", e);
        }
    }
}
