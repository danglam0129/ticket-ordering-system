package com.ticket.ordering.system.domain.valueobject;

import java.util.UUID;

public class EventId extends BaseId<UUID>{
    public EventId(UUID id) {
        super(id);
    }
}
