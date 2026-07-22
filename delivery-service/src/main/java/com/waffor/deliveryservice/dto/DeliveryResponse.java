package com.waffor.deliveryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryResponse {
    private Long orderId;
    private String driverName;
    private String status;
}
