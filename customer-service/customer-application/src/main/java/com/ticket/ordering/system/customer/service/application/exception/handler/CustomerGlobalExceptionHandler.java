package com.ticket.ordering.system.customer.service.application.exception.handler;

import com.ticket.ordering.system.application.handler.ErrorDTO;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerApplicationServiceException;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerAuthenticationException;
import com.ticket.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.ticket.ordering.system.customer.service.domain.exception.RefreshTokenDomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class CustomerGlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = {CustomerApplicationServiceException.class, CustomerDomainException.class,
            RefreshTokenDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleCustomerException(RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {CustomerAuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleCustomerAuthenticationException(CustomerAuthenticationException exception) {
        log.error(exception.getMessage(), exception);
        return ErrorDTO.builder()
                .code(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(exception.getMessage())
                .build();
    }
}
