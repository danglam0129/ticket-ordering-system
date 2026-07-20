package com.ticket.ordering.system.ticket.service.domain.exception;

import com.ticket.ordering.system.domain.exception.DomainException;

public class TicketDomainException extends DomainException {

    public TicketDomainException(String message) {
        super(message);
    }

    public TicketDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
