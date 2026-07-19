package com.ticket.ordering.system.order.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfigData {
    private String paymentRequestTopicName;
    private String paymentResponseTopicName;
    private String ticketReservationRequestTopicName;
    private String ticketReservationResponseTopicName;
    private String ticketApprovalRequestTopicName;
    private String ticketApprovalResponseTopicName;
}
