package com.ticket.ordering.system.ticket.service.dataaccess.outbox.adapter;

import com.ticket.ordering.system.ticket.service.dataaccess.outbox.mapper.TicketOutboxDataAccessMapper;
import com.ticket.ordering.system.ticket.service.dataaccess.outbox.repository.TicketOutboxJpaRepository;
import com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus;
import com.ticket.ordering.system.ticket.service.domain.outbox.TicketOutboxMessage;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketOutboxRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus.FAILED;
import static com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus.PROCESSING;
import static com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus.STARTED;

@Component
public class TicketOutboxRepositoryImpl implements TicketOutboxRepository {

    private final TicketOutboxJpaRepository ticketOutboxJpaRepository;
    private final TicketOutboxDataAccessMapper ticketOutboxDataAccessMapper;

    public TicketOutboxRepositoryImpl(TicketOutboxJpaRepository ticketOutboxJpaRepository,
                                      TicketOutboxDataAccessMapper ticketOutboxDataAccessMapper) {
        this.ticketOutboxJpaRepository = ticketOutboxJpaRepository;
        this.ticketOutboxDataAccessMapper = ticketOutboxDataAccessMapper;
    }

    @Override
    public TicketOutboxMessage save(TicketOutboxMessage ticketOutboxMessage) {
        return ticketOutboxDataAccessMapper.ticketOutboxEntityToOutboxMessage(ticketOutboxJpaRepository.save(
                ticketOutboxDataAccessMapper.ticketOutboxMessageToOutboxEntity(ticketOutboxMessage)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketOutboxMessage> findPendingMessages(int maxRetryCount, Instant processingTimeout) {
        List<TicketOutboxMessage> pendingMessages = new ArrayList<>(ticketOutboxJpaRepository
                .findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(List.of(STARTED, FAILED), maxRetryCount)
                .stream()
                .map(ticketOutboxDataAccessMapper::ticketOutboxEntityToOutboxMessage)
                .collect(Collectors.toList()));

        if (pendingMessages.size() < 20) {
            pendingMessages.addAll(ticketOutboxJpaRepository
                    .findTop20ByStatusAndRetryCountLessThanAndUpdatedAtBeforeOrderByCreatedAtAsc(
                            PROCESSING, maxRetryCount, processingTimeout)
                    .stream()
                    .map(ticketOutboxDataAccessMapper::ticketOutboxEntityToOutboxMessage)
                    .limit(20 - pendingMessages.size())
                    .collect(Collectors.toList()));
        }

        return pendingMessages;
    }

    @Override
    @Transactional
    public boolean markAsProcessing(UUID id) {
        return ticketOutboxJpaRepository.updateStatus(id, List.of(STARTED, FAILED, PROCESSING),
                PROCESSING, Instant.now()) == 1;
    }

    @Override
    @Transactional
    public void markAsCompleted(UUID id) {
        ticketOutboxJpaRepository.updateStatus(id, List.of(PROCESSING),
                OutboxStatus.COMPLETED, Instant.now());
    }

    @Override
    @Transactional
    public void markAsFailed(UUID id, String errorMessage) {
        ticketOutboxJpaRepository.updateStatusWithFailure(id, FAILED, Instant.now(), errorMessage);
    }
}
