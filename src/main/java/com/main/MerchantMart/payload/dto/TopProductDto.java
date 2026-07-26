package com.main.MerchantMart.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDto {

    private String productName;

    private Long quantitySold;

    private Double percentage;
}
