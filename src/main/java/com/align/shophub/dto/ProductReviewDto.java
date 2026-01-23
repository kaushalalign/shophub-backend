package com.align.shophub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReviewDto {
    private Long id;
    private String userName;
    private String reviewMessage;
    private Integer reviewValue;
    // We do NOT include the full 'Product' or 'UserInfo' objects here to break the loop
}