package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemDto {

    private Long id;


    @NotNull(message = "quantity is required field")
    private Integer quantity;

    @NotNull(message = "price is required field")
    private Double price;

    private ProductDto product;

    private Long productId;

    private Long orderId;
}
