package com.ticket.ordering.system.customer.service.domain.ports.output.repository;

import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.domain.valueobject.CustomerId;

import java.util.Optional;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId customerId);

    Optional<Customer> findByUsername(String username);

    boolean existsByUsername(String username);
}
