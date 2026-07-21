package com.ticket.ordering.system.customer.service.domain.mapper;

import com.ticket.ordering.system.customer.service.domain.dto.CustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.AuthenticationResponse;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerResponse;
import com.ticket.ordering.system.customer.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataMapper {

    public Customer registerCommandToCustomer(RegisterCustomerCommand command,
                                              String username,
                                              String encodedPassword) {
        return Customer.builder()
                .username(username)
                .password(encodedPassword)
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .build();
    }

    public RegisterCustomerResponse customerToRegisterCustomerResponse(Customer customer, String message) {
        return RegisterCustomerResponse.builder()
                .customerId(customer.getId().getValue())
                .username(customer.getUsername())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .role(customer.getRole())
                .message(message)
                .build();
    }

    public AuthenticationResponse customerToAuthenticationResponse(Customer customer,
                                                                   String accessToken,
                                                                   String refreshToken,
                                                                   Long expiresIn) {
        return AuthenticationResponse.builder()
                .customerId(customer.getId().getValue())
                .username(customer.getUsername())
                .role(customer.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }

    public CustomerResponse customerToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getId().getValue())
                .username(customer.getUsername())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .role(customer.getRole())
                .build();
    }
}
