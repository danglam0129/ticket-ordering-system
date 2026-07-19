package com.ticket.ordering.system.kafka.producer.service;

import com.ticket.ordering.system.kafka.producer.KafkaCallback;

import java.io.Serializable;

public interface KafkaProducer<K extends Serializable, V> {
    void send(String topicName, K key, V message, KafkaCallback<K, V> callback);
}
