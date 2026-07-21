package com.ticket.ordering.system.customer.service.dataaccess.refreshtoken.exception;

public class RefreshTokenDataaccessException extends RuntimeException {

    public RefreshTokenDataaccessException(String message) {
        super(message);
    }

    public RefreshTokenDataaccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
