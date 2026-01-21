package com.align.shophub.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStats {
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long lowStockProducts;
    private long totalUsers;
}