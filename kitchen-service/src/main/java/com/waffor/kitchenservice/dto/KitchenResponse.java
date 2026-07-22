package com.waffor.kitchenservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KitchenResponse {
    private Long orderId;
    private String status;
}
