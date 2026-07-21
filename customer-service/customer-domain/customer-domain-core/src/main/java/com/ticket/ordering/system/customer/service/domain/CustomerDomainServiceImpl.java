package com.ticket.ordering.system.customer.service.domain;

import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.customer.service.domain.entity.RefreshToken;
import com.ticket.ordering.system.domain.valueobject.CustomerId;

import java.time.Instant;

public class CustomerDomainServiceImpl implements CustomerDomainService {

    @Override
    public Customer validateAndInitializeCustomer(Customer customer) {
        customer.validateForRegistration();
        customer.initializeCustomer();
        return customer;
    }

    @Override
    public RefreshToken initializeRefreshToken(CustomerId customerId, String tokenHash, Instant expiresAt) {
        RefreshToken refreshToken = RefreshToken.builder()
                .customerId(customerId)
                .tokenHash(tokenHash)
                .expiresAt(expiresAt)
                .build();
        refreshToken.initializeRefreshToken();
        return refreshToken;
    }
}
