package com.ticket.ordering.system.domain.event;

public interface DomainEvent<T> {
    void fire();
}
