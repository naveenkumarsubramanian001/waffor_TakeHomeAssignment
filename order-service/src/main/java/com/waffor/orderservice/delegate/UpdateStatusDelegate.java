package com.waffor.orderservice.delegate;

import com.waffor.orderservice.entity.Order;
import com.waffor.orderservice.entity.OrderStatus;
import com.waffor.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("updateStatusDelegate")
@RequiredArgsConstructor
@Slf4j
public class UpdateStatusDelegate implements JavaDelegate {

    private final OrderRepository orderRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        // This is passed as a string literal parameter from the BPMN diagram (e.g. "PAYMENT_PROCESSING", "DELIVERED")
        String statusVariable = (String) execution.getVariable("status");
        
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(OrderStatus.valueOf(statusVariable));
        orderRepository.save(order);

        log.info("[OrderService] Order #{} - Status updated to {}", orderId, order.getStatus());
        
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            log.info("[OrderService] Order #{} - Workflow COMPLETE", orderId);
        }
    }
}
