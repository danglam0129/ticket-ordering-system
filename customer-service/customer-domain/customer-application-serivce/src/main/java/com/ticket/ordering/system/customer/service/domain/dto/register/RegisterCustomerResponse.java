package com.ticket.ordering.system.customer.service.domain.dto.register;

import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RegisterCustomerResponse {
    private final UUID customerId;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final CustomerRole role;
    private final String message;
}
