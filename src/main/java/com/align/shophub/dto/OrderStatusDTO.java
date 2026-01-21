package com.align.shophub.dto;

import lombok.Data;

@Data
public class OrderStatusDTO {
    private String orderStatus;    // e.g., "SHIPPED", "DELIVERED", "CANCELLED"
    private String paymentStatus;  // e.g., "PAID", "REFUNDED"
}