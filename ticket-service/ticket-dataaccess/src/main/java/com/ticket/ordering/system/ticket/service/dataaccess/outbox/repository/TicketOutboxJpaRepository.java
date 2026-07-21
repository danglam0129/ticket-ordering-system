package com.ticket.ordering.system.ticket.service.dataaccess.outbox.repository;

import com.ticket.ordering.system.ticket.service.dataaccess.outbox.entity.TicketOutboxEntity;
import com.ticket.ordering.system.ticket.service.domain.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketOutboxJpaRepository extends JpaRepository<TicketOutboxEntity, UUID> {

    List<TicketOutboxEntity> findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            Collection<OutboxStatus> statuses,
            int retryCount);

    List<TicketOutboxEntity> findTop20ByStatusAndRetryCountLessThanAndUpdatedAtBeforeOrderByCreatedAtAsc(
            OutboxStatus status,
            int retryCount,
            Instant updatedAt);

    @Modifying
    @Query("update TicketOutboxEntity t set t.status = :newStatus, t.updatedAt = :updatedAt " +
            "where t.id = :id and t.status in :currentStatuses")
    int updateStatus(@Param("id") UUID id,
                     @Param("currentStatuses") Collection<OutboxStatus> currentStatuses,
                     @Param("newStatus") OutboxStatus newStatus,
                     @Param("updatedAt") Instant updatedAt);

    @Modifying
    @Query("update TicketOutboxEntity t set t.status = :newStatus, t.retryCount = t.retryCount + 1, " +
            "t.updatedAt = :updatedAt, t.lastError = :lastError where t.id = :id")
    int updateStatusWithFailure(@Param("id") UUID id,
                                @Param("newStatus") OutboxStatus newStatus,
                                @Param("updatedAt") Instant updatedAt,
                                @Param("lastError") String lastError);
}
