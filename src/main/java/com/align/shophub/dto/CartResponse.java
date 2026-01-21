package com.align.shophub.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private BigDecimal totalAmount;
    private List<CartItemDto> items;
}

