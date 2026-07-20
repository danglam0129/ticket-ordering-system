package com.ticket.ordering.system.ticket.service.dataaccess.ticket.repository;

import com.ticket.ordering.system.ticket.service.dataaccess.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, UUID> {

    List<TicketEntity> findByIdIn(List<UUID> ticketIds);
}
