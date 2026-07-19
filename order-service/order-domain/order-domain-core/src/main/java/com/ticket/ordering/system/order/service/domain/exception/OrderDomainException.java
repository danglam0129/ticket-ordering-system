package com.ticket.ordering.system.order.service.domain.exception;

import com.ticket.ordering.system.domain.exception.DomainException;

public class OrderDomainException extends DomainException {
    public OrderDomainException(String message) {
        super(message);
    }
    public OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
