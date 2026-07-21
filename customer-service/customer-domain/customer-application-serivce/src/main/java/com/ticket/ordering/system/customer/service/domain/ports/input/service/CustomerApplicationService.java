package com.ticket.ordering.system.customer.service.domain.ports.input.service;

import com.ticket.ordering.system.customer.service.domain.dto.CustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.AuthenticationResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.LoginCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.login.LogoutCommand;
import com.ticket.ordering.system.customer.service.domain.dto.login.LogoutResponse;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.token.RefreshTokenCommand;
import jakarta.validation.Valid;

import java.util.UUID;

public interface CustomerApplicationService {

    RegisterCustomerResponse registerCustomer(@Valid RegisterCustomerCommand registerCustomerCommand);

    AuthenticationResponse login(@Valid LoginCustomerCommand loginCustomerCommand);

    AuthenticationResponse refreshToken(@Valid RefreshTokenCommand refreshTokenCommand);

    LogoutResponse logout(@Valid LogoutCommand logoutCommand);

    CustomerResponse findCustomer(UUID customerId);
}
