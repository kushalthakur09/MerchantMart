package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto {

    private Long id;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal price;

    private ProductDto product;

    private Long productId;

    private Long orderId;
}
