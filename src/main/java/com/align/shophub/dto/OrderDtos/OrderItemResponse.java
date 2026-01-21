package com.align.shophub.dto.OrderDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}