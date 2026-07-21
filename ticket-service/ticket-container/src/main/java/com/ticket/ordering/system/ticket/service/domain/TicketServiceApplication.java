package com.ticket.ordering.system.ticket.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.ticket.ordering.system")
@EnableKafka
@EnableJpaRepositories(basePackages = "com.ticket.ordering.system")
@AutoConfigurationPackage(basePackages = "com.ticket.ordering.system")
public class TicketServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketServiceApplication.class, args);
    }
}
