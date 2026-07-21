package com.ticket.ordering.system.customer.service.domain.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LogoutResponse {
    private final String message;
}
