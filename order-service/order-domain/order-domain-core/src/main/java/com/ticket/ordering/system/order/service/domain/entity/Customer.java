package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.AggregateRoot;
import com.ticket.ordering.system.domain.valueobject.CustomerId;

public class Customer extends AggregateRoot<CustomerId> {
    private String userName;
    private String firstName;
    private String lastName;

    public Customer(CustomerId id, String userName, String firstName, String lastName) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        super.setId(id);
    }

    public Customer(CustomerId id) {
        super.setId(id);
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
