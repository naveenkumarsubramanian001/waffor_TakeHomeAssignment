package com.waffor.orderservice.entity;

public enum OrderStatus {
    PLACED,
    PAYMENT_PROCESSING,
    KITCHEN_PREP,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED
}
