package com.align.shophub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "User_id")
    private UserInfo user;

    // Snapshot of address at time of order
    private String shippingAddress;
    private String shippingCity;
    private String shippingPincode;

    private String orderStatus; // PENDING, DELIVERED, CANCELLED
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal totalAmount;

    private LocalDateTime orderDate;
    private String paymentId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
}