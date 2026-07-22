package com.waffor.kitchenservice.controller;

import com.waffor.kitchenservice.dto.KitchenRequest;
import com.waffor.kitchenservice.dto.KitchenResponse;
import com.waffor.kitchenservice.entity.KitchenTicket;
import com.waffor.kitchenservice.repository.KitchenTicketRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
@Slf4j
public class KitchenController {

    private final KitchenTicketRepository kitchenTicketRepository;

    @PostMapping("/prepare")
    public ResponseEntity<KitchenResponse> prepareFood(@Valid @RequestBody KitchenRequest request) {
        
        KitchenTicket ticket = KitchenTicket.builder()
                .orderId(request.getOrderId())
                .status("READY")
                .build();
                
        kitchenTicketRepository.save(ticket);
        
        log.info("[KitchenService] Order #{} - Kitchen ticket created, preparing food... READY", request.getOrderId());
        
        KitchenResponse response = KitchenResponse.builder()
                .orderId(request.getOrderId())
                .status("READY")
                .build();
                
        return ResponseEntity.ok(response);
    }
}
