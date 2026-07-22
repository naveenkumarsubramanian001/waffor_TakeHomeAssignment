package com.waffor.orderservice.delegate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.HashMap;

@Component("deliveryDelegate")
@RequiredArgsConstructor
@Slf4j
public class DeliveryDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        
        log.info("[Camunda] Order #{}: Calling Delivery Service...", orderId);

        try {
            restTemplate.postForEntity(
                    "http://localhost:8083/api/delivery/assign",
                    request,
                    Map.class
            );
            log.info("[Camunda] Order #{}: Delivery Service driver assigned", orderId);
        } catch (Exception e) {
            log.error("[Camunda] Order #{}: Delivery Service failed - {}", orderId, e.getMessage());
            throw new RuntimeException("Delivery Service Error", e);
        }
    }
}
