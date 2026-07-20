package com.ticket.ordering.system.ticket.service.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public TicketDomainService ticketDomainService() {
        return new TicketDomainServiceImpl();
    }
}
