package com.ticket.ordering.system.order.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalRequestAvroModel;
import com.ticket.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketapproval.OrderPaidTicketRequestMessagePublisher;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.ticket.ordering.system.order.service.messaging.outbox.OrderOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidTicketRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderOutboxMessageHelper orderOutboxMessageHelper;

    public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                         OrderServiceConfigData orderServiceConfigData,
                                         OrderOutboxMessageHelper orderOutboxMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderOutboxMessageHelper = orderOutboxMessageHelper;
    }

    @Override
    public void publish(OrderPaidEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderPaidEvent for order id: {}", orderId);

        try {
            TicketApprovalRequestAvroModel ticketApprovalRequestAvroModel =
                    orderMessagingDataMapper.orderPaidEventToTicketApprovalRequestAvroModel(domainEvent);

            orderOutboxMessageHelper.save(orderId,
                    domainEvent.getClass().getSimpleName(),
                    orderServiceConfigData.getTicketApprovalRequestTopicName(),
                    orderId,
                    ticketApprovalRequestAvroModel.getSagaId(),
                    ticketApprovalRequestAvroModel);

            log.info("TicketApprovalRequestAvroModel stored in outbox for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing TicketApprovalRequestAvroModel in outbox with order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing TicketApprovalRequestAvroModel in outbox", e);
        }
    }
}
