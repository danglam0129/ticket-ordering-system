package com.ticket.ordering.system.customer.service.domain.exception;

public class CustomerApplicationServiceException extends RuntimeException {

    public CustomerApplicationServiceException(String message) {
        super(message);
    }

    public CustomerApplicationServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
