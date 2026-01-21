package com.align.shophub.dto.ProductDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private String image;
    private String category;
    private String brand;
    private BigDecimal price;
    private BigDecimal salePrice;
    private Integer totalStock;
    private Double averageReview;
}