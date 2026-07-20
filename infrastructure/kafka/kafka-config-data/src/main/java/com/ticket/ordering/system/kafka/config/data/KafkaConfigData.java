package com.ticket.ordering.system.kafka.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData {
    private String bootstrapServers = "localhost:19092,localhost:29092,localhost:39092";
    private Integer numOfPartitions = 3;
    private Short replicationFactor = 3;
    private List<String> topicNames = Arrays.asList(
            "payment-request",
            "payment-response",
            "ticket-reservation-request",
            "ticket-reservation-response",
            "ticket-approval-request",
            "ticket-approval-response");
}
