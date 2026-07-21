package com.ticket.ordering.system.ticket.service.messaging.mapper;

import com.ticket.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketApprovalResponseAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationRequestAvroModel;
import com.ticket.ordering.system.kafka.order.avro.model.TicketReservationResponseAvroModel;
import com.ticket.ordering.system.ticket.service.domain.dto.TicketApprovalRequest;
import com.ticket.ordering.system.ticket.service.domain.dto.TicketReservationRequest;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovalRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketApprovedEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservationRejectedEvent;
import com.ticket.ordering.system.ticket.service.domain.event.TicketReservedEvent;
import com.ticket.ordering.system.ticket.service.domain.valueobject.TicketOrderStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TicketMessagingDataMapper {

    public TicketReservationResponseAvroModel ticketReservedEventToTicketReservationResponseAvroModel(
            TicketReservedEvent ticketReservedEvent) {
        return ticketEventToTicketReservationResponseAvroModel(ticketReservedEvent, OrderApprovalStatus.APPROVED);
    }

    public TicketReservationResponseAvroModel ticketReservationRejectedEventToTicketReservationResponseAvroModel(
            TicketReservationRejectedEvent ticketReservationRejectedEvent) {
        return ticketEventToTicketReservationResponseAvroModel(ticketReservationRejectedEvent,
                OrderApprovalStatus.REJECTED);
    }

    public TicketApprovalResponseAvroModel ticketApprovedEventToTicketApprovalResponseAvroModel(
            TicketApprovedEvent ticketApprovedEvent) {
        return ticketEventToTicketApprovalResponseAvroModel(ticketApprovedEvent, OrderApprovalStatus.APPROVED);
    }

    public TicketApprovalResponseAvroModel ticketApprovalRejectedEventToTicketApprovalResponseAvroModel(
            TicketApprovalRejectedEvent ticketApprovalRejectedEvent) {
        return ticketEventToTicketApprovalResponseAvroModel(ticketApprovalRejectedEvent, OrderApprovalStatus.REJECTED);
    }

    public TicketReservationRequest ticketReservationRequestAvroModelToTicketReservationRequest(
            TicketReservationRequestAvroModel ticketReservationRequestAvroModel) {
        return TicketReservationRequest.builder()
                .id(ticketReservationRequestAvroModel.getId())
                .sagaId(ticketReservationRequestAvroModel.getSagaId())
                .orderId(ticketReservationRequestAvroModel.getOrderId())
                .ticketIds(ticketReservationRequestAvroModel.getTicketIds())
                .createdAt(ticketReservationRequestAvroModel.getCreatedAt())
                .ticketOrderStatus(TicketOrderStatus.valueOf(
                        ticketReservationRequestAvroModel.getTicketOrderStatus().name()))
                .build();
    }

    public TicketApprovalRequest ticketApprovalRequestAvroModelToTicketApprovalRequest(
            TicketApprovalRequestAvroModel ticketApprovalRequestAvroModel) {
        return TicketApprovalRequest.builder()
                .id(ticketApprovalRequestAvroModel.getId())
                .sagaId(ticketApprovalRequestAvroModel.getSagaId())
                .orderId(ticketApprovalRequestAvroModel.getOrderId())
                .ticketIds(ticketApprovalRequestAvroModel.getTicketIds())
                .price(ticketApprovalRequestAvroModel.getPrice())
                .createdAt(ticketApprovalRequestAvroModel.getCreatedAt())
                .ticketOrderStatus(TicketOrderStatus.valueOf(
                        ticketApprovalRequestAvroModel.getTicketOrderStatus().name()))
                .build();
    }

    private TicketReservationResponseAvroModel ticketEventToTicketReservationResponseAvroModel(
            TicketEvent ticketEvent,
            OrderApprovalStatus orderApprovalStatus) {
        return TicketReservationResponseAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(ticketEvent.getOrderId().getValue().toString())
                .orderId(ticketEvent.getOrderId().getValue().toString())
                .ticketIds(ticketIds(ticketEvent))
                .createdAt(ticketEvent.getCreatedAt().toInstant())
                .reservationStatus(orderApprovalStatus)
                .failureMessages(ticketEvent.getFailureMessages())
                .build();
    }

    private TicketApprovalResponseAvroModel ticketEventToTicketApprovalResponseAvroModel(
            TicketEvent ticketEvent,
            OrderApprovalStatus orderApprovalStatus) {
        return TicketApprovalResponseAvroModel.builder()
                .id(UUID.randomUUID().toString())
                .sagaId(ticketEvent.getOrderId().getValue().toString())
                .orderId(ticketEvent.getOrderId().getValue().toString())
                .ticketId(firstTicketId(ticketEvent))
                .createdAt(ticketEvent.getCreatedAt().toInstant())
                .orderApprovalStatus(orderApprovalStatus)
                .failureMessages(ticketEvent.getFailureMessages())
                .build();
    }

    private List<String> ticketIds(TicketEvent ticketEvent) {
        if (ticketEvent.getTicketIds() == null) {
            return Collections.emptyList();
        }
        return ticketEvent.getTicketIds().stream()
                .map(ticketId -> ticketId.getValue().toString())
                .collect(Collectors.toList());
    }

    private String firstTicketId(TicketEvent ticketEvent) {
        List<String> ticketIds = ticketIds(ticketEvent);
        return ticketIds.isEmpty() ? "" : ticketIds.get(0);
    }
}
