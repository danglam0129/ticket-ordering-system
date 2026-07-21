package com.ticket.ordering.system.payment.service.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ordering.system.kafka.producer.KafkaCallback;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.payment.service.domain.outbox.PaymentOutboxMessage;
import com.ticket.ordering.system.payment.service.domain.ports.output.repository.PaymentOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class PaymentOutboxScheduler {

    private final PaymentOutboxRepository paymentOutboxRepository;
    private final KafkaProducer<String, Object> kafkaProducer;
    private final ObjectMapper objectMapper;
    private final int maxAttempts;
    private final long processingTimeoutSeconds;

    public PaymentOutboxScheduler(PaymentOutboxRepository paymentOutboxRepository,
                                  KafkaProducer<String, Object> kafkaProducer,
                                  ObjectMapper objectMapper,
                                  @Value("${outbox.scheduler.max-attempts:5}") int maxAttempts,
                                  @Value("${outbox.scheduler.processing-timeout-seconds:60}")
                                  long processingTimeoutSeconds) {
        this.paymentOutboxRepository = paymentOutboxRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.maxAttempts = maxAttempts;
        this.processingTimeoutSeconds = processingTimeoutSeconds;
    }

    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms:2000}")
    public void publishPendingMessages() {
        Instant processingTimeout = Instant.now().minusSeconds(processingTimeoutSeconds);
        paymentOutboxRepository.findPendingMessages(maxAttempts, processingTimeout)
                .forEach(this::publishMessage);
    }

    private void publishMessage(PaymentOutboxMessage outboxMessage) {
        if (!paymentOutboxRepository.markAsProcessing(outboxMessage.getId())) {
            return;
        }

        try {
            Class<?> payloadClass = Class.forName(outboxMessage.getPayloadType());
            Object payload = objectMapper.readValue(outboxMessage.getPayload(), payloadClass);
            kafkaProducer.send(outboxMessage.getTopicName(), outboxMessage.getMessageKey(), payload,
                    new KafkaCallback<>() {
                        @Override
                        public void onSuccess(String topicName, String key, Object message) {
                            paymentOutboxRepository.markAsCompleted(outboxMessage.getId());
                            log.info("Payment outbox message {} sent to topic {} with key {}",
                                    outboxMessage.getId(), topicName, key);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            paymentOutboxRepository.markAsFailed(outboxMessage.getId(), errorMessage(throwable));
                            log.error("Failed to send payment outbox message {} to topic {}",
                                    outboxMessage.getId(), outboxMessage.getTopicName(), throwable);
                        }
                    });
        } catch (Exception e) {
            paymentOutboxRepository.markAsFailed(outboxMessage.getId(), errorMessage(e));
            log.error("Failed to publish payment outbox message {}", outboxMessage.getId(), e);
        }
    }

    private String errorMessage(Throwable throwable) {
        return throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
    }
}
