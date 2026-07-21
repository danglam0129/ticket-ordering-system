package com.ticket.ordering.system.order.service.dataaccess.order.adapter;

import com.ticket.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.ticket.ordering.system.order.service.dataaccess.order.repository.OrderOutboxJpaRepository;
import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;
import com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderOutboxRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus.FAILED;
import static com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus.PROCESSING;
import static com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus.STARTED;

@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderOutboxRepositoryImpl(OrderOutboxJpaRepository orderOutboxJpaRepository,
                                     OrderDataAccessMapper orderDataAccessMapper) {
        this.orderOutboxJpaRepository = orderOutboxJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage) {
        return orderDataAccessMapper.orderOutboxEntityToOutboxMessage(orderOutboxJpaRepository.save(
                orderDataAccessMapper.orderOutboxMessageToOutboxEntity(orderOutboxMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderOutboxMessage> findPendingMessages(int maxRetryCount, Instant processingTimeout) {
        List<OrderOutboxMessage> pendingMessages = new ArrayList<>(orderOutboxJpaRepository
                .findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List.of(STARTED, FAILED), maxRetryCount)
                .stream()
                .map(orderDataAccessMapper::orderOutboxEntityToOutboxMessage)
                .collect(Collectors.toList()));

        if (pendingMessages.size() < 20) {
            pendingMessages.addAll(orderOutboxJpaRepository
                    .findTop20ByStatusAndRetryCountLessThanAndUpdatedAtBeforeOrderByCreatedAtAsc(
                            PROCESSING, maxRetryCount, processingTimeout)
                    .stream()
                    .map(orderDataAccessMapper::orderOutboxEntityToOutboxMessage)
                    .limit(20 - pendingMessages.size())
                    .collect(Collectors.toList()));
        }

        return pendingMessages;
    }

    @Override
    @Transactional
    public boolean markAsProcessing(UUID id) {
        return orderOutboxJpaRepository.updateStatus(id, List.of(STARTED, FAILED, PROCESSING),
                PROCESSING, Instant.now()) == 1;
    }

    @Override
    @Transactional
    public void markAsCompleted(UUID id) {
        orderOutboxJpaRepository.updateStatus(id, List.of(PROCESSING),
                OutboxStatus.COMPLETED, Instant.now());
    }

    @Override
    @Transactional
    public void markAsFailed(UUID id, String errorMessage) {
        orderOutboxJpaRepository.updateStatusWithFailure(id, FAILED, Instant.now(), errorMessage);
    }
}
