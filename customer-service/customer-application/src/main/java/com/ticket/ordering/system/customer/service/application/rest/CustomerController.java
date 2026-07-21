package com.ticket.ordering.system.customer.service.application.rest;

import com.ticket.ordering.system.customer.service.domain.dto.CustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.AuthenticationResponse;
import com.ticket.ordering.system.customer.service.domain.dto.login.LoginCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.login.LogoutCommand;
import com.ticket.ordering.system.customer.service.domain.dto.login.LogoutResponse;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerCommand;
import com.ticket.ordering.system.customer.service.domain.dto.register.RegisterCustomerResponse;
import com.ticket.ordering.system.customer.service.domain.dto.token.RefreshTokenCommand;
import com.ticket.ordering.system.customer.service.domain.ports.input.service.CustomerApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/customers")
public class CustomerController {

    private final CustomerApplicationService customerApplicationService;

    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @PostMapping("/register")
    public RegisterCustomerResponse registerCustomer(
            @Valid @RequestBody RegisterCustomerCommand registerCustomerCommand) {
        return customerApplicationService.registerCustomer(registerCustomerCommand);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody LoginCustomerCommand loginCustomerCommand) {
        return customerApplicationService.login(loginCustomerCommand);
    }

    @PostMapping("/token/refresh")
    public AuthenticationResponse refreshToken(@Valid @RequestBody RefreshTokenCommand refreshTokenCommand) {
        return customerApplicationService.refreshToken(refreshTokenCommand);
    }

    @PostMapping("/logout")
    public LogoutResponse logout(@Valid @RequestBody LogoutCommand logoutCommand) {
        return customerApplicationService.logout(logoutCommand);
    }

    @GetMapping("/{customerId}")
    public CustomerResponse getCustomer(@PathVariable UUID customerId) {
        return customerApplicationService.findCustomer(customerId);
    }
}
