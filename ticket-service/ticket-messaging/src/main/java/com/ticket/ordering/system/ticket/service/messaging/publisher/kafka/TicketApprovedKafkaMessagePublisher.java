package com.ticket.ordering.system.ticket.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.kafka.producer.KafkaMessageHelper;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.ticket.service.domain.config.TicketServiceConfigData;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovedEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketapproval.TicketApprovedMessagePublisher;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketApprovedKafkaMessagePublisher implements TicketApprovedMessagePublisher {

    private final TicketMessagingDataMapper ticketMessagingDataMapper;
    private final KafkaProducer<String, TicketApprovalResponseAvroModel> kafkaProducer;
    private final TicketServiceConfigData ticketServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public TicketApprovedKafkaMessagePublisher(TicketMessagingDataMapper ticketMessagingDataMapper,
                                               KafkaProducer<String, TicketApprovalResponseAvroModel> kafkaProducer,
                                               TicketServiceConfigData ticketServiceConfigData,
                                               KafkaMessageHelper kafkaMessageHelper) {
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.ticketServiceConfigData = ticketServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(TicketApprovedEvent domainEvent) {
        String orderId = domainEvent.getOrderId().getValue().toString();
        log.info("Received TicketApprovedEvent for order id: {}", orderId);

        try {
            TicketApprovalResponseAvroModel response =
                    ticketMessagingDataMapper.ticketApprovedEventToTicketApprovalResponseAvroModel(domainEvent);
            kafkaProducer.send(ticketServiceConfigData.getTicketApprovalResponseTopicName(),
                    orderId,
                    response,
                    kafkaMessageHelper.getKafkaCallback(ticketServiceConfigData.getTicketApprovalResponseTopicName(),
                            response,
                            orderId,
                            "TicketApprovalResponseAvroModel"));
            log.info("TicketApprovalResponseAvroModel sent to Kafka for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending TicketApprovalResponseAvroModel for order id: {}, error: {}",
                    orderId, e.getMessage());
        }
    }
}
