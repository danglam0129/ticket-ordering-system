package com.ticket.ordering.system.customer.service.domain.dto.login;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LogoutCommand {
    @NotBlank
    private final String refreshToken;
}
