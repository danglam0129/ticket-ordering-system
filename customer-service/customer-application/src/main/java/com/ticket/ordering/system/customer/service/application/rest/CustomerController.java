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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(
            @Valid @RequestBody RegisterCustomerCommand registerCustomerCommand) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerApplicationService.registerCustomer(registerCustomerCommand));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginCustomerCommand loginCustomerCommand) {
        return ResponseEntity.ok(customerApplicationService.login(loginCustomerCommand));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Valid @RequestBody RefreshTokenCommand refreshTokenCommand) {
        return ResponseEntity.ok(customerApplicationService.refreshToken(refreshTokenCommand));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@Valid @RequestBody LogoutCommand logoutCommand) {
        return ResponseEntity.ok(customerApplicationService.logout(logoutCommand));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID customerId) {
        return ResponseEntity.ok(customerApplicationService.findCustomer(customerId));
    }
}
