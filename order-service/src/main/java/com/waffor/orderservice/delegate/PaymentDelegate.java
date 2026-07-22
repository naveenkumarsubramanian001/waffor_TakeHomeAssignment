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

@Component("paymentDelegate")
@RequiredArgsConstructor
@Slf4j
public class PaymentDelegate implements JavaDelegate {

    private final RestTemplate restTemplate;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long orderId = (Long) execution.getVariable("orderId");
        
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("amount", execution.getVariable("amount"));
        
        log.info("[Camunda] Order #{}: Calling Payment Service...", orderId);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "http://localhost:8081/api/payments/process",
                    request,
                    Map.class
            );
            
            String paymentStatus = (String) response.getBody().get("status");
            execution.setVariable("paymentStatus", paymentStatus);
            
            log.info("[Camunda] Order #{}: Payment Service returned {}", orderId, paymentStatus);
        } catch (Exception e) {
            log.error("[Camunda] Order #{}: Payment Service failed - {}", orderId, e.getMessage());
            execution.setVariable("paymentStatus", "FAILED");
        }
    }
}
