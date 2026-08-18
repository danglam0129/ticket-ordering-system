package com.ticket.ordering.system.order.service.application.exception.handler;

import com.ticket.ordering.system.application.handler.ErrorDTO;
import com.ticket.ordering.system.application.handler.GlobalExceptionHandler;
import com.ticket.ordering.system.order.service.domain.exception.OrderDomainException;
import com.ticket.ordering.system.order.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(value = {OrderDomainException.class})
    public ResponseEntity<ErrorDTO> handleException(OrderDomainException orderDomainException) {
        log.error(orderDomainException.getMessage(), orderDomainException);
        return ResponseEntity.badRequest()
                .body(ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(orderDomainException.getMessage())
                .build());
    }

    @ExceptionHandler(value = {OrderNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleException(OrderNotFoundException orderNotFoundException) {
        log.error(orderNotFoundException.getMessage(), orderNotFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorDTO.builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(orderNotFoundException.getMessage())
                .build());
    }
}
