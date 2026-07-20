package com.ticket.ordering.system.ticket.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ticket-service")
public class TicketServiceConfigData {
    private String ticketReservationRequestTopicName;
    private String ticketReservationResponseTopicName;
    private String ticketApprovalRequestTopicName;
    private String ticketApprovalResponseTopicName;
}
