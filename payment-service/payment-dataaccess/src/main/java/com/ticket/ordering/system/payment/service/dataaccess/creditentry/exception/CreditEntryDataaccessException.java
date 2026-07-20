package com.ticket.ordering.system.payment.service.dataaccess.creditentry.exception;

public class CreditEntryDataaccessException extends RuntimeException {

    public CreditEntryDataaccessException(String message) {
        super(message);
    }

    public CreditEntryDataaccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
