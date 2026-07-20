package com.ticket.ordering.system.kafka.producer;

import com.ticket.ordering.system.kafka.config.data.KafkaConfigData;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    private final KafkaConfigData kafkaConfigData;

    public KafkaTopicConfig(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
        KafkaAdmin kafkaAdmin = new KafkaAdmin(configs);
        kafkaAdmin.setFatalIfBrokerNotAvailable(false);
        return kafkaAdmin;
    }

    @Bean
    public KafkaAdmin.NewTopics ticketOrderingTopics() {
        NewTopic[] topics = kafkaConfigData.getTopicNames().stream()
                .map(topicName -> TopicBuilder.name(topicName)
                        .partitions(kafkaConfigData.getNumOfPartitions())
                        .replicas(kafkaConfigData.getReplicationFactor())
                        .build())
                .toArray(NewTopic[]::new);
        return new KafkaAdmin.NewTopics(topics);
    }
}
