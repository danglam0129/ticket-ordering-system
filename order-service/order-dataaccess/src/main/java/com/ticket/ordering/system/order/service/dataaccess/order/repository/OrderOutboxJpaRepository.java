package com.ticket.ordering.system.order.service.dataaccess.order.repository;

import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderOutboxEntity;
import com.ticket.ordering.system.order.service.domain.outbox.OutboxStatus;
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
public interface OrderOutboxJpaRepository extends JpaRepository<OrderOutboxEntity, UUID> {

    List<OrderOutboxEntity> findTop20ByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            Collection<OutboxStatus> statuses,
            int retryCount);

    List<OrderOutboxEntity> findTop20ByStatusAndRetryCountLessThanAndUpdatedAtBeforeOrderByCreatedAtAsc(
            OutboxStatus status,
            int retryCount,
            Instant updatedAt);

    @Modifying
    @Query("update OrderOutboxEntity o set o.status = :newStatus, o.updatedAt = :updatedAt " +
            "where o.id = :id and o.status in :currentStatuses")
    int updateStatus(@Param("id") UUID id,
                     @Param("currentStatuses") Collection<OutboxStatus> currentStatuses,
                     @Param("newStatus") OutboxStatus newStatus,
                     @Param("updatedAt") Instant updatedAt);

    @Modifying
    @Query("update OrderOutboxEntity o set o.status = :newStatus, o.retryCount = o.retryCount + 1, " +
            "o.updatedAt = :updatedAt, o.lastError = :lastError where o.id = :id")
    int updateStatusWithFailure(@Param("id") UUID id,
                                @Param("newStatus") OutboxStatus newStatus,
                                @Param("updatedAt") Instant updatedAt,
                                @Param("lastError") String lastError);
}
