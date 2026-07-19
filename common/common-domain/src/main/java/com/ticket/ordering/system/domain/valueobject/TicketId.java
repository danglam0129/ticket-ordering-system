package com.ticket.ordering.system.domain.valueobject;

import java.util.UUID;

public class TicketId extends BaseId<UUID>{
    public TicketId(UUID id) {
        super(id);
    }
}
