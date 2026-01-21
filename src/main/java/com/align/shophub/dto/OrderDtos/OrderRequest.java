package com.align.shophub.dto.OrderDtos;

import com.align.shophub.dto.OrderDtos.OrderItemRequest;
import lombok.Data;
import java.util.List;


@Data
public class OrderRequest {
    private Long addressId;
    private String paymentMethod;
    private String paymentId; // Added this field
    private boolean fromCart;
    private List<OrderItemRequest> items;
}