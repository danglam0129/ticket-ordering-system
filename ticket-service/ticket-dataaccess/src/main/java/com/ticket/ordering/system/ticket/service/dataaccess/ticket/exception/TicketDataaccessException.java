package com.ticket.ordering.system.ticket.service.dataaccess.ticket.exception;

public class TicketDataaccessException extends RuntimeException {

    public TicketDataaccessException(String message) {
        super(message);
    }

    public TicketDataaccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
