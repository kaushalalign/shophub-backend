package com.align.shophub.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    // Indexing category is important for your requested analytics
    @Column(nullable = false)
    private String category;

    private String brand;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer totalStock;
    private Double averageReview;
}