package com.ticket.ordering.system.order.service.domain.valueobject;

import com.ticket.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class TrackingId extends BaseId<UUID> {
    public TrackingId(UUID id) {
        super(id);
    }
}
