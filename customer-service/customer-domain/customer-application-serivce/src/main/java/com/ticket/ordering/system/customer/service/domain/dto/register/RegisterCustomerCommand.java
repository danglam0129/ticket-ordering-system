package com.ticket.ordering.system.customer.service.domain.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterCustomerCommand {
    @NotBlank
    private final String username;
    @NotBlank
    @Size(min = 8)
    private final String password;
    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
}
