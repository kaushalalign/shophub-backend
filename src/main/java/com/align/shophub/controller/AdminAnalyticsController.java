package com.align.shophub.controller;

import com.align.shophub.repository.OrderRepository;
import com.align.shophub.repository.ProductRepository;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminAnalyticsController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;

    @GetMapping("/overview")
    public AnalyticsResponse getAnalytics() {
        // 1. Sales Over Time (Last 30 days)
        List<Object[]> salesData = orderRepository.findDailySalesStats();
        List<DailySales> dailySalesList = new ArrayList<>();

        for (Object[] row : salesData) {
            dailySalesList.add(DailySales.builder()
                    .date(row[0].toString())
                    .orderCount(((Number) row[1]).longValue())
                    .totalRevenue((BigDecimal) row[2])
                    .build());
        }

        // 2. Sales By Category
        List<Object[]> categoryData = productRepository.findSalesByCategory();
        Map<String, Long> categoryMap = new HashMap<>();

        for (Object[] row : categoryData) {
            String category = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            categoryMap.put(category, count);
        }

        return AnalyticsResponse.builder()
                .dailySales(dailySalesList)
                .categoryDistribution(categoryMap)
                .build();
    }
}

// --- DTOs for Analytics ---

@Data
@Builder
class AnalyticsResponse {
    private List<DailySales> dailySales;
    private Map<String, Long> categoryDistribution;
}

@Data
@Builder
class DailySales {
    private String date;
    private long orderCount;
    private BigDecimal totalRevenue;
}