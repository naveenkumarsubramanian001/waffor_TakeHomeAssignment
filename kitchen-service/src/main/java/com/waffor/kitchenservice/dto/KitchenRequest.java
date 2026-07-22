package com.waffor.kitchenservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KitchenRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Item name is required")
    private String item;
}
