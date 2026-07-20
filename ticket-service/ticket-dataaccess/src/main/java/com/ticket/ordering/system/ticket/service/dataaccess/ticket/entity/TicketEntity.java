package com.ticket.ordering.system.ticket.service.dataaccess.ticket.entity;

import com.ticket.ordering.system.ticket.service.domain.valueobject.TicketStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
@Entity
public class TicketEntity {

    @Id
    private UUID id;
    private UUID seatId;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    private UUID reservedByOrderId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketEntity that = (TicketEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
