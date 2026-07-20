package com.ticket.ordering.system.payment.service.dataaccess.credithistory.exception;

public class CreditHistoryDataaccessException extends RuntimeException {

    public CreditHistoryDataaccessException(String message) {
        super(message);
    }

    public CreditHistoryDataaccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
