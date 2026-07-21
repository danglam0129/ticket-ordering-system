package com.ticket.ordering.system.customer.service.dataaccess.customer.adapter;

import com.ticket.ordering.system.customer.service.dataaccess.customer.mapper.CustomerDataAccessMapper;
import com.ticket.ordering.system.customer.service.dataaccess.customer.repository.CustomerJpaRepository;
import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerRepositoryImpl implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerDataAccessMapper customerDataAccessMapper;

    public CustomerRepositoryImpl(CustomerJpaRepository customerJpaRepository,
                                  CustomerDataAccessMapper customerDataAccessMapper) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerDataAccessMapper = customerDataAccessMapper;
    }

    @Override
    public Customer save(Customer customer) {
        return customerDataAccessMapper.customerEntityToCustomer(customerJpaRepository.save(
                customerDataAccessMapper.customerToCustomerEntity(customer)));
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        return customerJpaRepository.findById(customerId.getValue())
                .map(customerDataAccessMapper::customerEntityToCustomer);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        return customerJpaRepository.findByUsername(username)
                .map(customerDataAccessMapper::customerEntityToCustomer);
    }

    @Override
    public boolean existsByUsername(String username) {
        return customerJpaRepository.existsByUsername(username);
    }
}
