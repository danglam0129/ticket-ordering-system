package com.ticket.ordering.system.ticket.service.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.ordering.system.kafka.producer.KafkaCallback;
import com.ticket.ordering.system.kafka.producer.service.KafkaProducer;
import com.ticket.ordering.system.ticket.service.domain.outbox.TicketOutboxMessage;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class TicketOutboxScheduler {

    private final TicketOutboxRepository ticketOutboxRepository;
    private final KafkaProducer<String, Object> kafkaProducer;
    private final ObjectMapper objectMapper;
    private final int maxAttempts;
    private final long processingTimeoutSeconds;

    public TicketOutboxScheduler(TicketOutboxRepository ticketOutboxRepository,
                                 KafkaProducer<String, Object> kafkaProducer,
                                 ObjectMapper objectMapper,
                                 @Value("${outbox.scheduler.max-attempts:5}") int maxAttempts,
                                 @Value("${outbox.scheduler.processing-timeout-seconds:60}")
                                 long processingTimeoutSeconds) {
        this.ticketOutboxRepository = ticketOutboxRepository;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
        this.maxAttempts = maxAttempts;
        this.processingTimeoutSeconds = processingTimeoutSeconds;
    }

    @Scheduled(fixedDelayString = "${outbox.scheduler.fixed-delay-ms:2000}")
    public void publishPendingMessages() {
        Instant processingTimeout = Instant.now().minusSeconds(processingTimeoutSeconds);
        ticketOutboxRepository.findPendingMessages(maxAttempts, processingTimeout)
                .forEach(this::publishMessage);
    }

    private void publishMessage(TicketOutboxMessage outboxMessage) {
        if (!ticketOutboxRepository.markAsProcessing(outboxMessage.getId())) {
            return;
        }

        try {
            Class<?> payloadClass = Class.forName(outboxMessage.getPayloadType());
            Object payload = objectMapper.readValue(outboxMessage.getPayload(), payloadClass);
            kafkaProducer.send(outboxMessage.getTopicName(), outboxMessage.getMessageKey(), payload,
                    new KafkaCallback<>() {
                        @Override
                        public void onSuccess(String topicName, String key, Object message) {
                            ticketOutboxRepository.markAsCompleted(outboxMessage.getId());
                            log.info("Ticket outbox message {} sent to topic {} with key {}",
                                    outboxMessage.getId(), topicName, key);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            ticketOutboxRepository.markAsFailed(outboxMessage.getId(), errorMessage(throwable));
                            log.error("Failed to send ticket outbox message {} to topic {}",
                                    outboxMessage.getId(), outboxMessage.getTopicName(), throwable);
                        }
                    });
        } catch (Exception e) {
            ticketOutboxRepository.markAsFailed(outboxMessage.getId(), errorMessage(e));
            log.error("Failed to publish ticket outbox message {}", outboxMessage.getId(), e);
        }
    }

    private String errorMessage(Throwable throwable) {
        return throwable.getMessage() == null ? throwable.getClass().getSimpleName() : throwable.getMessage();
    }
}
