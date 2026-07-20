package com.ticket.ordering.system.ticket.service.domain.exception;

public class TicketNotFoundException extends TicketDomainException {

    public TicketNotFoundException(String message) {
        super(message);
    }

    public TicketNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
