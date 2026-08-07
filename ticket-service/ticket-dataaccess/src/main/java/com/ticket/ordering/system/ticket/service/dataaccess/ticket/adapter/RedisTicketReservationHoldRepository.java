package com.ticket.ordering.system.ticket.service.dataaccess.ticket.adapter;

import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.ticket.service.domain.ports.output.repository.TicketReservationHoldRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "ticket-service.redis-reservation-holds-enabled", havingValue = "true")
public class RedisTicketReservationHoldRepository implements TicketReservationHoldRepository {

    private static final DefaultRedisScript<Long> RELEASE_IF_OWNER_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);

    private final StringRedisTemplate redisTemplate;
    private final String keyPrefix;
    private final Duration holdTtl;

    public RedisTicketReservationHoldRepository(StringRedisTemplate redisTemplate,
                                                @Value("${ticket-service.reservation-hold-key-prefix:" +
                                                        "ticket:reservation:hold:}")
                                                String keyPrefix,
                                                @Value("${ticket-service.reservation-hold-ttl-seconds:600}")
                                                long holdTtlSeconds) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
        this.holdTtl = Duration.ofSeconds(holdTtlSeconds);
    }

    @Override
    public List<String> holdTickets(OrderId orderId, List<TicketId> ticketIds) {
        if (orderId == null || ticketIds == null || ticketIds.isEmpty()) {
            return List.of();
        }

        String orderIdValue = orderId.getValue().toString();
        List<TicketId> acquiredTicketIds = new ArrayList<>();
        List<String> failureMessages = new ArrayList<>();

        try {
            for (TicketId ticketId : ticketIds) {
                String key = key(ticketId);
                Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, orderIdValue, holdTtl);
                if (Boolean.TRUE.equals(acquired) || isHeldByOrder(key, orderIdValue)) {
                    acquiredTicketIds.add(ticketId);
                    continue;
                }

                String currentOrderId = redisTemplate.opsForValue().get(key);
                failureMessages.add("Ticket with id=" + ticketId.getValue() +
                        " is temporarily held by another order" + ownerMessage(currentOrderId) + "!");
            }
        } catch (RuntimeException e) {
            log.warn("Redis ticket reservation hold is unavailable, continuing without temporary holds", e);
            return List.of();
        }

        if (!failureMessages.isEmpty()) {
            releaseTickets(orderId, acquiredTicketIds);
        }

        return failureMessages;
    }

    @Override
    public void releaseTickets(OrderId orderId, List<TicketId> ticketIds) {
        if (orderId == null || ticketIds == null || ticketIds.isEmpty()) {
            return;
        }

        String orderIdValue = orderId.getValue().toString();
        try {
            ticketIds.forEach(ticketId -> redisTemplate.execute(RELEASE_IF_OWNER_SCRIPT,
                    List.of(key(ticketId)), orderIdValue));
        } catch (RuntimeException e) {
            log.warn("Could not release Redis ticket reservation holds for order id: {}", orderIdValue, e);
        }
    }

    private boolean isHeldByOrder(String key, String orderId) {
        if (orderId.equals(redisTemplate.opsForValue().get(key))) {
            redisTemplate.expire(key, holdTtl);
            return true;
        }

        return false;
    }

    private String key(TicketId ticketId) {
        return keyPrefix + ticketId.getValue();
    }

    private String ownerMessage(String currentOrderId) {
        return currentOrderId == null ? "" : " " + currentOrderId;
    }
}
