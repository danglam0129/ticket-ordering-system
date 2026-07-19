package com.ticket.ordering.system.order.service.dataaccess.order.adapter;

import com.ticket.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.ticket.ordering.system.order.service.dataaccess.order.repository.OrderOutboxJpaRepository;
import com.ticket.ordering.system.order.service.domain.outbox.OrderOutboxMessage;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderOutboxRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderOutboxRepositoryImpl implements OrderOutboxRepository {

    private final OrderOutboxJpaRepository orderOutboxJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderOutboxRepositoryImpl(OrderOutboxJpaRepository orderOutboxJpaRepository,
                                     OrderDataAccessMapper orderDataAccessMapper) {
        this.orderOutboxJpaRepository = orderOutboxJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public void save(OrderOutboxMessage orderOutboxMessage) {
        orderOutboxJpaRepository.save(orderDataAccessMapper.orderOutboxMessageToOutboxEntity(orderOutboxMessage));
    }
}
