package com.waffor.paymentservice.controller;

import com.waffor.paymentservice.dto.PaymentRequest;
import com.waffor.paymentservice.dto.PaymentResponse;
import com.waffor.paymentservice.entity.Payment;
import com.waffor.paymentservice.repository.PaymentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentRepository paymentRepository;
    private final Random random = new Random();

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        
        // Mock 80% success rate
        boolean isSuccess = random.nextInt(100) < 80;
        String status = isSuccess ? "SUCCESS" : "FAILED";
        
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .status(status)
                .build();
                
        paymentRepository.save(payment);
        
        log.info("[PaymentService] Order #{} - Payment processing... {}", request.getOrderId(), status);
        
        PaymentResponse response = PaymentResponse.builder()
                .orderId(request.getOrderId())
                .status(status)
                .message("Payment " + status.toLowerCase())
                .build();
                
        return ResponseEntity.ok(response);
    }
}
