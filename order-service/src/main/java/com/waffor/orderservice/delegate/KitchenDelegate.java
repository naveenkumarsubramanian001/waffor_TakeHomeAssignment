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

@Component("kitchenDelegate")
@RequiredArgsConstructor
@Slf4j
public class KitchenDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("item", execution.getVariable("item"));
        
        log.info("[Camunda] Order #{}: Calling Kitchen Service...", orderId);

        try {
            restTemplate.postForEntity(
                    "http://localhost:8082/api/kitchen/prepare",
                    request,
                    Map.class
            );
            log.info("[Camunda] Order #{}: Kitchen Service food preparation complete", orderId);
        } catch (Exception e) {
            log.error("[Camunda] Order #{}: Kitchen Service failed - {}", orderId, e.getMessage());
            throw new RuntimeException("Kitchen Service Error", e);
        }
    }
}
