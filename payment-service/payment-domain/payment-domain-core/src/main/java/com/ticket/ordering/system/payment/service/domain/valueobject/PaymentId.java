package com.ticket.ordering.system.payment.service.domain.valueobject;

import com.ticket.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class PaymentId extends BaseId<UUID> {
    public PaymentId(UUID value) {
        super(value);
    }
}
