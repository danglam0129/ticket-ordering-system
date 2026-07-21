package com.ticket.ordering.system.customer.service.domain;

import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.customer.service.domain.entity.RefreshToken;
import com.ticket.ordering.system.domain.valueobject.CustomerId;

import java.time.Instant;

public interface CustomerDomainService {

    Customer validateAndInitializeCustomer(Customer customer);

    RefreshToken initializeRefreshToken(CustomerId customerId, String tokenHash, Instant expiresAt);
}
