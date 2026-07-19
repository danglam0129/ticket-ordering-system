package com.ticket.ordering.system.kafka.producer.service.impl;

import com.ticket.ordering.system.kafka.producer.KafkaCallback;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V> implements KafkaProducer<K, V> {

    @Override
    public void send(String topicName, K key, V message, KafkaCallback<K, V> callback) {
        try {
            log.info("Sending message to Kafka topic: {}, key: {}, message: {}", topicName, key, message);
            callback.onSuccess(topicName, key, message);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
