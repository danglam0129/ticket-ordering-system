package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.BaseEntity;
import com.ticket.ordering.system.domain.valueobject.SeatId;

public class Seat extends BaseEntity<SeatId> {
    private final String position;
    private final boolean active;

    private Seat(Builder builder) {
        super.setId(builder.seatId);
        position = builder.position;
        active = builder.active;
    }

    public String getPosition() {
        return position;
    }
    public boolean isActive() {
        return active;
    }

    public Seat(SeatId seatId, String position) {
        super.setId(seatId);
        this.position = position;
        this.active = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SeatId seatId;
        private String position;
        private boolean active;

        private Builder() {
        }

        public Builder seatId(SeatId seatId) {
            this.seatId = seatId;
            return this;
        }

        public Builder position(String position) {
            this.position = position;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Seat build() {
            return new Seat(this);
        }
    }
}
