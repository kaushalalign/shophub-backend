package com.align.shophub.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long productId;
    private String reviewMessage;
    private Integer reviewValue; // 1-5
}