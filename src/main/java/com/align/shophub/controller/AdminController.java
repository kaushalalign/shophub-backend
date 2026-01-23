package com.align.shophub.controller;

import com.align.shophub.dto.DashboardStats;
import com.align.shophub.entity.Feature;

import com.align.shophub.repository.FeatureRepository;
import com.align.shophub.repository.OrderRepository;
import com.align.shophub.repository.ProductRepository;
import com.align.shophub.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserInfoRepository userRepository;
    @Autowired private FeatureRepository featureRepository; 

    // ADMIN: Dashboard Stats
    @GetMapping("/dashboard")
    public DashboardStats getDashboardStats() {
        long totalOrders = orderRepository.count();
        long totalUsers = userRepository.count();
        long lowStock = productRepository.countByTotalStockLessThan(10);
        BigDecimal revenue = orderRepository.calculateTotalRevenue();

        return DashboardStats.builder()
                .totalOrders(totalOrders)
                .totalUsers(totalUsers)
                .lowStockProducts(lowStock)
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .build();
    }

    // ADMIN: Update Feature/Banner Image
    @PostMapping("/feature")
    public Feature updateFeatureImage(@RequestParam String imageUrl) {
        // Assuming single feature row for main banner, ID = 1
        Feature feature = new Feature();
        feature.setImage(imageUrl);
        return featureRepository.save(feature);
    }
}