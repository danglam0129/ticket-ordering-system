package com.ticket.ordering.system.order.service.dataaccess.order.adapter;

import com.ticket.ordering.system.order.service.dataaccess.order.entity.OrderIdempotencyEntity;
import com.ticket.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.ticket.ordering.system.order.service.dataaccess.order.repository.OrderIdempotencyJpaRepository;
import com.ticket.ordering.system.order.service.domain.entity.Order;
import com.ticket.ordering.system.order.service.domain.ports.output.repository.OrderIdempotencyRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderIdempotencyRepositoryImpl implements OrderIdempotencyRepository {

    private final OrderIdempotencyJpaRepository orderIdempotencyJpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderIdempotencyRepositoryImpl(OrderIdempotencyJpaRepository orderIdempotencyJpaRepository,
                                          OrderDataAccessMapper orderDataAccessMapper) {
        this.orderIdempotencyJpaRepository = orderIdempotencyJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Optional<Order> findOrder(String idempotencyKey) {
        return orderIdempotencyJpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(OrderIdempotencyEntity::getOrder)
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public void save(String idempotencyKey, Order order) {
        orderIdempotencyJpaRepository.save(OrderIdempotencyEntity.builder()
                .idempotencyKey(idempotencyKey)
                .order(orderDataAccessMapper.orderToOrderEntity(order))
                .build());
    }
}
