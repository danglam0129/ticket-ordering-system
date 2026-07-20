package com.ticket.ordering.system.ticket.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.kafka.producer.KafkaMessageHelper;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.ticket.service.domain.config.TicketServiceConfigData;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovalRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.ports.output.message.publisher.ticketapproval.TicketApprovalRejectedMessagePublisher;
import com.ticket.ordering.system.ticket.service.messaging.mapper.TicketMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TicketApprovalRejectedKafkaMessagePublisher implements TicketApprovalRejectedMessagePublisher {

    private final TicketMessagingDataMapper ticketMessagingDataMapper;
    private final KafkaProducer<String, TicketApprovalResponseAvroModel> kafkaProducer;
    private final TicketServiceConfigData ticketServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public TicketApprovalRejectedKafkaMessagePublisher(TicketMessagingDataMapper ticketMessagingDataMapper,
                                                       KafkaProducer<String, TicketApprovalResponseAvroModel>
                                                               kafkaProducer,
                                                       TicketServiceConfigData ticketServiceConfigData,
                                                       KafkaMessageHelper kafkaMessageHelper) {
        this.ticketMessagingDataMapper = ticketMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.ticketServiceConfigData = ticketServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }

    @Override
    public void publish(TicketApprovalRejectedEvent domainEvent) {
        String orderId = domainEvent.getOrderId().getValue().toString();
        log.info("Received TicketApprovalRejectedEvent for order id: {}", orderId);

        try {
            TicketApprovalResponseAvroModel response =
                    ticketMessagingDataMapper.ticketApprovalRejectedEventToTicketApprovalResponseAvroModel(domainEvent);
            kafkaProducer.send(ticketServiceConfigData.getTicketApprovalResponseTopicName(),
                    orderId,
                    response,
                    kafkaMessageHelper.getKafkaCallback(ticketServiceConfigData.getTicketApprovalResponseTopicName(),
                            response,
                            orderId,
                            "TicketApprovalResponseAvroModel"));
            log.info("TicketApprovalResponseAvroModel sent to Kafka for rejected order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while sending TicketApprovalResponseAvroModel for order id: {}, error: {}",
                    orderId, e.getMessage());
        }
    }
}
