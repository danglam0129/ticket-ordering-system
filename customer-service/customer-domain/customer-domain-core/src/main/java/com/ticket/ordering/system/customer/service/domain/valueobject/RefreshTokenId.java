package com.ticket.ordering.system.customer.service.domain.valueobject;

import com.ticket.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class RefreshTokenId extends BaseId<UUID> {

    public RefreshTokenId(UUID value) {
        super(value);
    }
}
