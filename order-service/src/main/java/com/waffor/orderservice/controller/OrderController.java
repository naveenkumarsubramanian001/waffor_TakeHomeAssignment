package com.waffor.orderservice.controller;

import com.waffor.orderservice.dto.OrderRequest;
import com.waffor.orderservice.dto.OrderResponse;
import com.waffor.orderservice.entity.Order;
import com.waffor.orderservice.entity.OrderStatus;
import com.waffor.orderservice.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow React UI
public class OrderController {

    private final OrderRepository orderRepository;
    private final JmsTemplate jmsTemplate;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .item(request.getItem())
                .amount(request.getAmount())
                .status(OrderStatus.PLACED)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        
        log.info("[OrderService] Order #{} - PLACED", savedOrder.getId());

        // Publish to ActiveMQ
        jmsTemplate.convertAndSend("order.created", savedOrder.getId());
        log.info("[OrderService] Order #{} - Published to 'order.created' queue", savedOrder.getId());

        return new ResponseEntity<>(mapToResponse(savedOrder), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Used internally by Camunda delegates to update status
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(OrderStatus.valueOf(payload.get("status")));
        orderRepository.save(order);
        
        log.info("[OrderService] Order #{} - Status updated to {}", id, order.getStatus());
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            log.info("[OrderService] Order #{} - Workflow COMPLETE", id);
        }
        return ResponseEntity.ok().build();
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .item(order.getItem())
                .amount(order.getAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
