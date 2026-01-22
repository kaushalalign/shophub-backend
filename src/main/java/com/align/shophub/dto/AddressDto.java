package com.align.shophub.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressDto {
    private Long id;
    private String addressLine;
    private String city;
    private String pincode;
    private String phone;
    private String notes;
}