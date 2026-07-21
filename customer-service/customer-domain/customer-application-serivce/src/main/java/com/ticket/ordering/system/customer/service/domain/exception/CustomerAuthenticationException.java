package com.ticket.ordering.system.customer.service.domain.exception;

public class CustomerAuthenticationException extends RuntimeException {

    public CustomerAuthenticationException(String message) {
        super(message);
    }

    public CustomerAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
