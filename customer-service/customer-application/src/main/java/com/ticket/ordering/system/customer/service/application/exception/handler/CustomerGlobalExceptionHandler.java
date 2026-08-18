package com.ticket.ordering.system.customer.service.application.exception.handler;

import com.ticket.ordering.system.application.handler.ErrorDTO;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerApplicationServiceException;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerAuthenticationException;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.ticket.ordering.system.customer.service.domain.exception.RefreshTokenDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class CustomerGlobalExceptionHandler {

    @ExceptionHandler(value = {CustomerApplicationServiceException.class, CustomerDomainException.class,
            RefreshTokenDomainException.class})
    public ResponseEntity<ErrorDTO> handleCustomerException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.badRequest()
                .body(ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(value = {CustomerAuthenticationException.class})
    public ResponseEntity<ErrorDTO> handleCustomerAuthenticationException(CustomerAuthenticationException exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorDTO.builder()
                .code(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(exception.getMessage())
                .build());
    }
}
