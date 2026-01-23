package com.align.shophub.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemDto {
    private Long itemId;
    private Long productId;
    private String productName;
    private String image;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;
    private Integer availableStock;
}