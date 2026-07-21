package com.ticket.ordering.system.payment.service.dataaccess.outbox.adapter;

import com.ticket.ordering.system.payment.service.dataaccess.outbox.mapper.PaymentOutboxDataAccessMapper;
import com.ticket.ordering.system.payment.service.dataaccess.outbox.repository.PaymentOutboxJpaRepository;
import com.ticket.ordering.system.payment.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.payment.service.domain.outbox.PaymentOutboxMessage;
import com.ticket.ordering.system.payment.service.domain.ports.output.repository.PaymentOutboxRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ticket.ordering.system.payment.service.domain.outbox.OutboxStatus.FAILED;
import static com.ticket.ordering.system.payment.service.domain.outbox.OutboxStatus.PROCESSING;
import static com.ticket.ordering.system.payment.service.domain.outbox.OutboxStatus.STARTED;

@Component
public class PaymentOutboxRepositoryImpl implements PaymentOutboxRepository {

    private final PaymentOutboxJpaRepository paymentOutboxJpaRepository;
    private final PaymentOutboxDataAccessMapper paymentOutboxDataAccessMapper;

    public PaymentOutboxRepositoryImpl(PaymentOutboxJpaRepository paymentOutboxJpaRepository,
                                       PaymentOutboxDataAccessMapper paymentOutboxDataAccessMapper) {
        this.paymentOutboxJpaRepository = paymentOutboxJpaRepository;
        this.paymentOutboxDataAccessMapper = paymentOutboxDataAccessMapper;
    }

    @Override
    public PaymentOutboxMessage save(PaymentOutboxMessage paymentOutboxMessage) {
        return paymentOutboxDataAccessMapper.paymentOutboxEntityToOutboxMessage(paymentOutboxJpaRepository.save(
                paymentOutboxDataAccessMapper.paymentOutboxMessageToOutboxEntity(paymentOutboxMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentOutboxMessage> findPendingMessages(int maxRetryCount, Instant processingTimeout) {
        List<PaymentOutboxMessage> pendingMessages = new ArrayList<>(paymentOutboxJpaRepository
                .findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List.of(STARTED, FAILED), maxRetryCount)
                .stream()
                .map(paymentOutboxDataAccessMapper::paymentOutboxEntityToOutboxMessage)
                .collect(Collectors.toList()));

        if (pendingMessages.size() < 20) {
            pendingMessages.addAll(paymentOutboxJpaRepository
                    .findTop20ByStatusAndRetryCountLessThanAndUpdatedAtBeforeOrderByCreatedAtAsc(
                            PROCESSING, maxRetryCount, processingTimeout)
                    .stream()
                    .map(paymentOutboxDataAccessMapper::paymentOutboxEntityToOutboxMessage)
                    .limit(20 - pendingMessages.size())
                    .collect(Collectors.toList()));
        }

        return pendingMessages;
    }

    @Override
    @Transactional
    public boolean markAsProcessing(UUID id) {
        return paymentOutboxJpaRepository.updateStatus(id, List.of(STARTED, FAILED, PROCESSING),
                PROCESSING, Instant.now()) == 1;
    }

    @Override
    @Transactional
    public void markAsCompleted(UUID id) {
        paymentOutboxJpaRepository.updateStatus(id, List.of(PROCESSING),
                OutboxStatus.COMPLETED, Instant.now());
    }

    @Override
    @Transactional
    public void markAsFailed(UUID id, String errorMessage) {
        paymentOutboxJpaRepository.updateStatusWithFailure(id, FAILED, Instant.now(), errorMessage);
    }
}
