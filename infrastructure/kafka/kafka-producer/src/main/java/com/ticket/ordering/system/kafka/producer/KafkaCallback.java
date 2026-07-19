package com.ticket.ordering.system.kafka.producer;

public interface KafkaCallback<K, V> {
    void onSuccess(String topicName, K key, V message);

    void onFailure(Throwable throwable);
}
