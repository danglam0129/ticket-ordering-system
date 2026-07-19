package com.ticket.ordering.system.order.service.dataaccess.customer.mapper;

import com.ticket.ordering.system.domain.valueobject.CustomerId;
import com.ticket.ordering.system.order.service.dataaccess.customer.entity.CustomerEntity;
import com.ticket.ordering.system.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCustomer(CustomerEntity customerEntity) {
        return new Customer(new CustomerId(customerEntity.getId()),
                customerEntity.getUserName(),
                customerEntity.getFirstName(),
                customerEntity.getLastName());
    }
}
