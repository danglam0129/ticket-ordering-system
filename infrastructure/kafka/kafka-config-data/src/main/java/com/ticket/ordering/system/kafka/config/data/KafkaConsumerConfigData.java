package com.ticket.ordering.system.kafka.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-config")
public class KafkaConsumerConfigData {
    private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
    private String valueDeserializer = "org.springframework.kafka.support.serializer.JsonDeserializer";
    private String consumerGroupId = "ticket-ordering-system-consumer";
    private String paymentRequestConsumerGroupId = "payment-request-topic-consumer";
    private String paymentResponseConsumerGroupId = "payment-response-topic-consumer";
    private String ticketReservationRequestConsumerGroupId = "ticket-reservation-request-topic-consumer";
    private String ticketReservationResponseConsumerGroupId = "ticket-reservation-response-topic-consumer";
    private String ticketApprovalRequestConsumerGroupId = "ticket-approval-request-topic-consumer";
    private String ticketApprovalResponseConsumerGroupId = "ticket-approval-response-topic-consumer";
    private String autoOffsetReset = "earliest";
    private Boolean batchListener = true;
    private Boolean autoStartup = true;
    private Integer concurrencyLevel = 3;
    private Integer sessionTimeoutMs = 10000;
    private Integer heartbeatIntervalMs = 3000;
    private Integer maxPollIntervalMs = 300000;
    private Long pollTimeoutMs = 150L;
    private Integer maxPollRecords = 500;
    private Integer maxPartitionFetchBytesDefault = 1048576;
    private Integer maxPartitionFetchBytesBoostFactor = 1;
    private String trustedPackages = "com.ticket.ordering.system.kafka.order.avro.model";
    private Boolean useTypeHeaders = true;
}
