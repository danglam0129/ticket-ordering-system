package com.ticket.ordering.system.kafka.producer.service.impl;

import com.ticket.ordering.system.kafka.producer.KafkaCallback;
import com.ticket.ordering.system.kafka.producer.exception.KafkaProducerException;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, K key, V message, KafkaCallback<K, V> callback) {
        log.info("Sending message to Kafka topic: {}, key: {}, message: {}", topicName, key, message);
        try {
            CompletableFuture<SendResult<K, V>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
            kafkaResultFuture.whenComplete((result, throwable) -> {
                if (throwable == null) {
                    String sentTopicName = result == null ? topicName : result.getRecordMetadata().topic();
                    log.info("Message sent to Kafka topic: {}, key: {}", sentTopicName, key);
                    callback.onSuccess(sentTopicName, key, message);
                } else {
                    log.error("Error while sending message to Kafka topic: {}, key: {}, error: {}",
                            topicName, key, throwable.getMessage());
                    callback.onFailure(throwable);
                }
            });
        } catch (KafkaException e) {
            log.error("Error on Kafka producer with key: {}, message: {}, exception: {}",
                    key, message, e.getMessage());
            callback.onFailure(e);
            throw new KafkaProducerException("Error on Kafka producer with key: " + key +
                    " and message: " + message, e);
        }
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing Kafka producer!");
            kafkaTemplate.destroy();
        }
    }
}
