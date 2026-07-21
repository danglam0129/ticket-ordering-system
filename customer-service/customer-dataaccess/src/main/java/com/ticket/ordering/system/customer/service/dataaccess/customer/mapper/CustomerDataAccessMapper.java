package com.ticket.ordering.system.customer.service.dataaccess.customer.mapper;

import com.ticket.ordering.system.customer.service.dataaccess.customer.entity.CustomerEntity;
import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import com.ticket.ordering.system.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public CustomerEntity customerToCustomerEntity(Customer customer) {
        return CustomerEntity.builder()
                .id(customer.getId().getValue())
                .username(customer.getUsername())
                .password(customer.getPassword())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .role(customer.getRole())
                .build();
    }

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return Customer.builder()
                .customerId(new CustomerId(customerEntity.getId()))
                .username(customerEntity.getUsername())
                .password(customerEntity.getPassword())
                .firstName(customerEntity.getFirstName())
                .lastName(customerEntity.getLastName())
                .role(customerEntity.getRole())
                .build();
    }
}
