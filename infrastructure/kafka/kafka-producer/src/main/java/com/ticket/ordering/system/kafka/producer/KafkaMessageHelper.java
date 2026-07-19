package com.ticket.ordering.system.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaMessageHelper {

    public <T> KafkaCallback<String, T> getKafkaCallback(String responseTopicName,
                                                         T message,
                                                         String orderId,
                                                         String messageModelName) {
        return new KafkaCallback<>() {
            @Override
            public void onSuccess(String topicName, String key, T sentMessage) {
                log.info("Received successful response from Kafka for order id: {} topic: {} key: {}",
                        orderId, topicName, key);
            }

            @Override
            public void onFailure(Throwable throwable) {
                log.error("Error while sending {} message {} to topic {}",
                        messageModelName, message, responseTopicName, throwable);
            }
        };
    }
}
