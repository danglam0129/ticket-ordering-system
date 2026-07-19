package com.ticket.ordering.system.order.service.domain.entity;

import com.ticket.ordering.system.domain.entity.AggregateRoot;
import com.ticket.ordering.system.domain.valueobject.CustomerId;
import com.ticket.ordering.system.domain.valueobject.Money;
import com.ticket.ordering.system.domain.valueobject.OrderId;
import com.ticket.ordering.system.domain.valueobject.OrderStatus;
import com.ticket.ordering.system.order.service.domain.exception.OrderDomainException;
import com.ticket.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.ticket.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final List<OrderItem> items;
    private final Money price;

    private List<String> failureMessages;
    private TrackingId trackingId;
    private OrderStatus orderStatus;

    public static final String FAILURE_MESSAGE_DELIMITER = ",";

    private Order(Builder builder) {
        super.setId(builder.orderId);
        this.customerId = builder.customerId;
        this.items = builder.items;
        this.price = builder.price;

        this.failureMessages = builder.failureMessages;
        this.trackingId = builder.trackingId;
        this.orderStatus = builder.orderStatus;
    }

    public void initializeOrder(){
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItem();
    }

    public void validateOrder(){
        validateInitialOrder();
        validateOrderItem();
        validateTotalPrice();
    }

    private void initializeOrderItem(){
        Long orderItemId = 1L;
        for (OrderItem orderItem : items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(orderItemId++));
        }
    }

    private void validateInitialOrder(){
        if(orderStatus != null || getId() != null){
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
        if(customerId == null){
            throw new OrderDomainException("Customer id must be present!");
        }
    }

    private void validateOrderItem(){
        if(items == null || items.isEmpty()){
            throw new OrderDomainException("Order items must be present!");
        }
        for (OrderItem orderItem : items) {
            if(!orderItem.isPriceValid()){
                throw new OrderDomainException("Order item price is not valid!");
            }
        }
    }

    private void validateTotalPrice(){
        if(price == null || !price.isGreaterThanZero()){
            throw new OrderDomainException("Total price must be greater than zero!");
        }
        Money orderItemTotal = items.stream()
                .map(OrderItem::getSubTotal)
                .reduce(Money.ZERO, Money::add);
        if(!price.equals(orderItemTotal)){
            throw new OrderDomainException("Total price doesn't match");
        }
    }

    public void reserve() {
        if(orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order status must be PENDING");
        }
        orderStatus = OrderStatus.RESERVED;
    }

    public void pay(){
        if(orderStatus != OrderStatus.RESERVED){
            throw new OrderDomainException("Order status must be RESERVED");
        }
        orderStatus =  OrderStatus.PAID;
    }

    public void approve(){
        if(orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order status must be PAID");
        }
        orderStatus =  OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages){
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Order is not in correct state for initCancel operation!");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages) {
        if (!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING ||
                orderStatus == OrderStatus.RESERVED)) {
            throw new OrderDomainException("Order is not in correct state for cancel operation!");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if (this.failureMessages != null && failureMessages != null) {
            this.failureMessages.addAll(failureMessages.stream().filter(message -> !message.isEmpty()).toList());
        }
        if (this.failureMessages == null) {
            this.failureMessages = failureMessages;
        }
    }

    public OrderId getId() {
        return super.getId();
    }
    public CustomerId getCustomerId() {
        return customerId;
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public Money getPrice() {
        return price;
    }
    public TrackingId getTrackingId() {
        return trackingId;
    }
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private List<OrderItem> items;
        private Money price;
        private List<String> failureMessages;
        private TrackingId trackingId;
        private OrderStatus orderStatus;

        private Builder() {
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }

    }
}
