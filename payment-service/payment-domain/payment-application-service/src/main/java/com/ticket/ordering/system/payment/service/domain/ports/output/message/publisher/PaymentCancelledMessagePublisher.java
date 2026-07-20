package com.ticket.ordering.system.payment.service.domain.ports.output.message.publisher;

import com.ticket.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.ticket.ordering.system.payment.service.domain.event.PaymentCancelledEvent;

public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {
}
