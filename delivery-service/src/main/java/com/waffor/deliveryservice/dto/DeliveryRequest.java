package com.waffor.deliveryservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;
}
