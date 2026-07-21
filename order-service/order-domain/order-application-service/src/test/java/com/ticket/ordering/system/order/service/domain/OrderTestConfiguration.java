package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderReservedPaymentRequestMessagePublisher;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketapproval.OrderPaidTicketRequestMessagePublisher;
import com.ticket.ordering.system.order.service.domain.ports.output.message.publisher.ticketreservation.OrderCreatedTicketReservationRequestMessagePublisher;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderIdempotencyRepository;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.ticket.ordering.system")
public class OrderTestConfiguration {

    @Bean
    public OrderCreatedTicketReservationRequestMessagePublisher orderCreatedTicketReservationRequestMessagePublisher() {
        return Mockito.mock(OrderCreatedTicketReservationRequestMessagePublisher.class);
    }

    @Bean
    public OrderReservedPaymentRequestMessagePublisher orderReservedPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderReservedPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderPaidTicketRequestMessagePublisher orderPaidTicketRequestMessagePublisher() {
        return Mockito.mock(OrderPaidTicketRequestMessagePublisher.class);
    }

    @Bean
    public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
        return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
    }

    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public CustomerRepository customerRepository() {
        return Mockito.mock(CustomerRepository.class);
    }

    @Bean
    public OrderIdempotencyRepository orderIdempotencyRepository() {
        return Mockito.mock(OrderIdempotencyRepository.class);
    }

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }
}
