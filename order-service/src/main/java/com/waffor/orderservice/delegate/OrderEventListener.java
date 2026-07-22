package com.waffor.orderservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final RuntimeService runtimeService;

    @JmsListener(destination = "order.created")
    public void onOrderCreated(Long orderId) {
        log.info("[OrderService] Order #{} - Consumed from ActiveMQ, starting Camunda workflow", orderId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("orderId", orderId);

        // Start the BPMN process
        runtimeService.startProcessInstanceByKey("order-process", String.valueOf(orderId), variables);
    }
}
