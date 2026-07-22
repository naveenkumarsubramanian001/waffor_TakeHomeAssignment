package com.waffor.deliveryservice.controller;

import com.waffor.deliveryservice.dto.DeliveryRequest;
import com.waffor.deliveryservice.dto.DeliveryResponse;
import com.waffor.deliveryservice.entity.Delivery;
import com.waffor.deliveryservice.repository.DeliveryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;

    @PostMapping("/assign")
    public ResponseEntity<DeliveryResponse> assignDelivery(@Valid @RequestBody DeliveryRequest request) {
        
        String mockDriverName = "Driver John Doe";
        
        Delivery delivery = Delivery.builder()
                .orderId(request.getOrderId())
                .driverName(mockDriverName)
                .status("DELIVERED")
                .build();
                
        deliveryRepository.save(delivery);
        
        log.info("[DeliveryService] Order #{} - Driver assigned, delivering... DELIVERED", request.getOrderId());
        
        DeliveryResponse response = DeliveryResponse.builder()
                .orderId(request.getOrderId())
                .driverName(mockDriverName)
                .status("DELIVERED")
                .build();
                
        return ResponseEntity.ok(response);
    }
}
