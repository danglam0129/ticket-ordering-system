package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.BaseEntity;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.ticket.ordering.system.order.service.domain.exception.OrderDomainException;

import java.util.List;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final List<Ticket> tickets;
    private final int quantity;
    private final Money subTotal;

    public OrderItem(Builder builder){
        super.setId(builder.orderItemId);
        this.orderId = builder.orderId;
        this.tickets = builder.tickets;
        this.quantity = builder.quantity;
        this.subTotal = builder.subTotal;
    }

    public void initializeOrderItem(OrderId orderId, OrderItemId orderItemId){
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    public boolean isPriceValid() {
        if (tickets == null || tickets.isEmpty() || quantity != tickets.size() ||
                subTotal == null || !subTotal.isGreaterThanZero()) {
            return false;
        }

        Money ticketTotal = Money.ZERO;
        for (Ticket ticket : tickets) {
            if (ticket.getPrice() == null || !ticket.getPrice().isGreaterThanZero()) {
                return false;
            }
            ticketTotal = ticketTotal.add(ticket.getPrice());
        }
        return subTotal.equals(ticketTotal);
    }

    public void confirmTicketInformation(Ticket confirmedTicket) {
        Ticket currentTicket = tickets.stream()
                .filter(ticket -> ticket.equals(confirmedTicket))
                .findFirst()
                .orElseThrow(() -> new OrderDomainException("Ticket with id " +
                        confirmedTicket.getId().getValue() + " is not part of this order item"));

        currentTicket.updateWithConfirmedInformation(
                confirmedTicket.getEvent(),
                confirmedTicket.getPrice(),
                confirmedTicket.getSeat());
    }

    public OrderId getOrderId() {
        return orderId;
    }
    public List<Ticket> getTickets() {
        return tickets;
    }
    public int getQuantity() {
        return quantity;
    }
    public Money getSubTotal() {
        return subTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private OrderItemId orderItemId;
        private List<Ticket> tickets;
        private int quantity;
        private Money subTotal;

        private Builder() {}

        public Builder orderItemId(OrderItemId val) {
            orderItemId = val;
            return this;
        }

        public Builder orderId(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder tickets(List<Ticket> val) {
            tickets = val;
            return this;
        }

        public Builder quantity(int val) {
            quantity = val;
            return this;
        }

        public Builder subTotal(Money val) {
            subTotal = val;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }

}
