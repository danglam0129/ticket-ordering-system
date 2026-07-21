package com.ticket.ordering.system.customer.service.domain.exception;

import com.ticket.ordering.system.domain.exception.DomainException;

public class RefreshTokenDomainException extends DomainException {

    public RefreshTokenDomainException(String message) {
        super(message);
    }

    public RefreshTokenDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
