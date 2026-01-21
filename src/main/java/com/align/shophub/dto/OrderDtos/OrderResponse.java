package com.align.shophub.dto.OrderDtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderStatus;
    private String paymentStatus;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private List<OrderItemResponse> items;
}