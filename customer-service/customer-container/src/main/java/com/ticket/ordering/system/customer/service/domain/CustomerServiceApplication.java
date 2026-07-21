package com.ticket.ordering.system.customer.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ticket.ordering.system")
@EnableJpaRepositories(basePackages = "com.ticket.ordering.system")
@AutoConfigurationPackage(basePackages = "com.ticket.ordering.system")
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
