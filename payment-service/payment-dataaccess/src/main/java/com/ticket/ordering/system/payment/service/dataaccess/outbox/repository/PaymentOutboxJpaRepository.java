package com.ticket.ordering.system.payment.service.dataaccess.outbox.repository;

import com.ticket.ordering.system.payment.service.dataaccess.outbox.entity.PaymentOutboxEntity;
import com.ticket.ordering.system.payment.service.domain.outbox.OutboxStatus;
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
public interface PaymentOutboxJpaRepository extends JpaRepository<PaymentOutboxEntity, UUID> {

    List<PaymentOutboxEntity> findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            Collection<OutboxStatus> statuses,
            int retryCount);

    List<PaymentOutboxEntity> findTop20ByStatusAndRetryCountLessThanAndUpdatedAtBeforeOrderByCreatedAtAsc(
            OutboxStatus status,
            int retryCount,
            Instant updatedAt);

    @Modifying
    @Query("update PaymentOutboxEntity p set p.status = :newStatus, p.updatedAt = :updatedAt " +
            "where p.id = :id and p.status in :currentStatuses")
    int updateStatus(@Param("id") UUID id,
                     @Param("currentStatuses") Collection<OutboxStatus> currentStatuses,
                     @Param("newStatus") OutboxStatus newStatus,
                     @Param("updatedAt") Instant updatedAt);

    @Modifying
    @Query("update PaymentOutboxEntity p set p.status = :newStatus, p.retryCount = p.retryCount + 1, " +
            "p.updatedAt = :updatedAt, p.lastError = :lastError where p.id = :id")
    int updateStatusWithFailure(@Param("id") UUID id,
                                @Param("newStatus") OutboxStatus newStatus,
                                @Param("updatedAt") Instant updatedAt,
                                @Param("lastError") String lastError);
}
