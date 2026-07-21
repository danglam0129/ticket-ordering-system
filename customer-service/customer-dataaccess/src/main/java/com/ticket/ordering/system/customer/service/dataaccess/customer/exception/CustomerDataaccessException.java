package com.ticket.ordering.system.customer.service.dataaccess.customer.exception;

public class CustomerDataaccessException extends RuntimeException {

    public CustomerDataaccessException(String message) {
        super(message);
    }

    public CustomerDataaccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
