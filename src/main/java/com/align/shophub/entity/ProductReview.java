package com.align.shophub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_reviews")
@Data
public class ProductReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;

    @Column(nullable = false)
    private String userName; // Stored redundantly or fetched via User entity

    @Column(columnDefinition = "TEXT")
    private String reviewMessage;

    @Column(nullable = false)
    private Integer reviewValue; // e.g., 1 to 5
}