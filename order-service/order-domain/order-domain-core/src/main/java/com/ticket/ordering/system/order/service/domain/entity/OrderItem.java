package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.BaseEntity;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.TicketId;
import com.ticket.ordering.system.order.service.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {
    private OrderId orderId;
    private final TicketId ticketId;
    private final Money price;
    private final Money subTotal;

    public OrderItem(Builder builder){
        super.setId(builder.orderItemId);
        this.orderId = builder.orderId;
        this.ticketId = builder.ticketId;
        this.price = builder.price;
        this.subTotal = builder.subTotal;
    }

    public void initializeOrderItem(OrderId orderId, OrderItemId orderItemId){
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    public boolean isPriceValid() {
        if (ticketId == null || price == null || !price.isGreaterThanZero() ||
                subTotal == null || !subTotal.isGreaterThanZero()) {
            return false;
        }
        return subTotal.equals(price);
    }

    public OrderId getOrderId() {
        return orderId;
    }
    public TicketId getTicketId() {
        return ticketId;
    }
    public Money getPrice() {
        return price;
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
        private TicketId ticketId;
        private Money price;
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

        public Builder ticketId(TicketId val) {
            ticketId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
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
