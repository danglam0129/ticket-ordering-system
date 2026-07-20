package com.ticket.ordering.system.payment.service.messaging.listener.kafka;

import com.ticket.ordering.system.kafka.consumer.KafkaConsumer;
import com.ticket.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.ticket.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.ticket.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.ticket.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentMessagingDataMapper paymentMessagingDataMapper) {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }

    @Override
    public void receive(List<PaymentRequestAvroModel> messages,
                        List<String> keys,
                        List<Integer> partitions,
                        List<Long> offsets) {
        log.info("{} number of payment requests received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);

        messages.forEach(paymentRequestAvroModel -> {
            if (PaymentOrderStatus.PENDING == paymentRequestAvroModel.getPaymentOrderStatus()) {
                log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.completePayment(paymentMessagingDataMapper
                        .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            } else if (PaymentOrderStatus.CANCELLED == paymentRequestAvroModel.getPaymentOrderStatus()) {
                log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
                paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper
                        .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            } else {
                log.warn("Ignoring payment request with unsupported status: {} for order id: {}",
                        paymentRequestAvroModel.getPaymentOrderStatus(), paymentRequestAvroModel.getOrderId());
            }
        });
    }
}
