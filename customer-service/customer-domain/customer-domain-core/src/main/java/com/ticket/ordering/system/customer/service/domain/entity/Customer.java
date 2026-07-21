package com.ticket.ordering.system.customer.service.domain.entity;

import com.ticket.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.ticket.ordering.system.customer.service.domain.valueobject.CustomerRole;
import com.ticket.ordering.system.domain.entity.AggregateRoot;
import com.ticket.ordering.system.domain.valueobject.CustomerId;

import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {

    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private CustomerRole role;

    private Customer(Builder builder) {
        setId(builder.customerId);
        username = builder.username;
        password = builder.password;
        firstName = builder.firstName;
        lastName = builder.lastName;
        role = builder.role;
    }

    public void validateForRegistration() {
        if (getId() != null) {
            throw new CustomerDomainException("Customer is not in correct state for registration!");
        }
        validateRequired(username, "Username must be present!");
        validateRequired(password, "Password must be present!");
        validateRequired(firstName, "First name must be present!");
        validateRequired(lastName, "Last name must be present!");
    }

    public void initializeCustomer() {
        setId(new CustomerId(UUID.randomUUID()));
        if (role == null) {
            role = CustomerRole.CUSTOMER;
        }
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new CustomerDomainException(message);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public CustomerRole getRole() {
        return role;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CustomerId customerId;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private CustomerRole role;

        private Builder() {
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder role(CustomerRole val) {
            role = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
