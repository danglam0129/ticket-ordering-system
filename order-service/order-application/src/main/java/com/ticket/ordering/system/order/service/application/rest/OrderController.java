package com.ticket.ordering.system.order.service.application.rest;

import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.ticket.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.ticket.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.ticket.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.ticket.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    @PostMapping
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderCommand createOrderCommand) {
        return orderApplicationService.createOrder(createOrderCommand);
    }

    @GetMapping("/{trackingId}")
    public TrackOrderResponse trackOrder(@PathVariable UUID trackingId) {
        return orderApplicationService.trackOrder(TrackOrderQuery.builder()
                .orderTrackingId(trackingId)
                .build());
    }
}
