package com.ticket.ordering.system.customer.service.domain.dto;

import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CustomerResponse {
    private final UUID customerId;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final CustomerRole role;
}
