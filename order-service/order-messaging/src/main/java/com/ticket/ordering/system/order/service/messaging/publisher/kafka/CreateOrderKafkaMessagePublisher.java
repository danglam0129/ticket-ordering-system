package com.ticket.ordering.system.order.service.messaging.publisher.kafka;

import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationRequestAvroModel;
import com.ticket.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.ticket.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketreservation.OrderCreatedTicketReservationRequestMessagePublisher;
import com.ticket.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import com.ticket.ordering.system.order.service.messaging.outbox.OrderOutboxMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedTicketReservationRequestMessagePublisher {

    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final OrderServiceConfigData orderServiceConfigData;
    private final OrderOutboxMessageHelper orderOutboxMessageHelper;

    public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                            OrderServiceConfigData orderServiceConfigData,
                                            OrderOutboxMessageHelper orderOutboxMessageHelper) {
        this.orderMessagingDataMapper = orderMessagingDataMapper;
        this.orderServiceConfigData = orderServiceConfigData;
        this.orderOutboxMessageHelper = orderOutboxMessageHelper;
    }

    @Override
    public void publish(OrderCreatedEvent domainEvent) {
        String orderId = domainEvent.getOrder().getId().getValue().toString();
        log.info("Received OrderCreatedEvent for order id: {}", orderId);

        try {
            TicketReservationRequestAvroModel ticketReservationRequestAvroModel =
                    orderMessagingDataMapper.orderCreatedEventToTicketReservationRequestAvroModel(domainEvent);

            orderOutboxMessageHelper.save(orderId,
                    domainEvent.getClass().getSimpleName(),
                    orderServiceConfigData.getTicketReservationRequestTopicName(),
                    orderId,
                    ticketReservationRequestAvroModel.getSagaId(),
                    ticketReservationRequestAvroModel);

            log.info("TicketReservationRequestAvroModel stored in outbox for order id: {}", orderId);
        } catch (Exception e) {
            log.error("Error while storing TicketReservationRequestAvroModel in outbox with order id: {}, error: {}",
                    orderId, e.getMessage());
            throw new IllegalStateException("Error while storing TicketReservationRequestAvroModel in outbox", e);
        }
    }
}
