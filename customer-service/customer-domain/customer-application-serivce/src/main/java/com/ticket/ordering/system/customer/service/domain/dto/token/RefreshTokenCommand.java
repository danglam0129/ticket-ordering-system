package com.ticket.ordering.system.customer.service.domain.dto.token;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RefreshTokenCommand {
    @NotBlank
    private final String refreshToken;
}
