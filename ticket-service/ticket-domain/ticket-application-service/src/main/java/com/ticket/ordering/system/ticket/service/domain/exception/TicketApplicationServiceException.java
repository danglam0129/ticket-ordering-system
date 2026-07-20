package com.ticket.ordering.system.ticket.service.domain.exception;

import com.ticket.ordering.system.domain.exception.DomainException;

public class TicketApplicationServiceException extends DomainException {

    public TicketApplicationServiceException(String message) {
        super(message);
    }

    public TicketApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
