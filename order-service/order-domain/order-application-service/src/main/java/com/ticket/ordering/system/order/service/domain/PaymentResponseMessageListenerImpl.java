package com.ticket.ordering.system.order.service.domain;

import com.ticket.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.ticket.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.ticket.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;

import static com.ticket.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

    private final OrderPaymentSaga orderPaymentSaga;
    private final OrderOutboxHelper orderOutboxHelper;

    public PaymentResponseMessageListenerImpl(OrderPaymentSaga orderPaymentSaga,
                                              OrderOutboxHelper orderOutboxHelper) {
        this.orderPaymentSaga = orderPaymentSaga;
        this.orderOutboxHelper = orderOutboxHelper;
    }

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        OrderPaidEvent domainEvent = orderPaymentSaga.process(paymentResponse);
        orderOutboxHelper.save(domainEvent);
        log.info("Publishing OrderPaidEvent for order id: {}", paymentResponse.getOrderId());
        domainEvent.fire();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSaga.rollback(paymentResponse);
        log.info("Order is rolled back for order id: {} with failure messages: {}",
                paymentResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER,
                        paymentResponse.getFailureMessages() == null ?
                                Collections.emptyList() : paymentResponse.getFailureMessages()));
    }
}
