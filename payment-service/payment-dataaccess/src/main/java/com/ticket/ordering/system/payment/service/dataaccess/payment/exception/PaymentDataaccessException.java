package com.ticket.ordering.system.payment.service.dataaccess.payment.exception;

public class PaymentDataaccessException extends RuntimeException {

    public PaymentDataaccessException(String message) {
        super(message);
    }

    public PaymentDataaccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
