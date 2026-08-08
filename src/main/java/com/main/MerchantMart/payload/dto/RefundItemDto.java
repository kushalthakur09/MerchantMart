package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundItemDto {

    private Long id;

    @NotNull(message = "Order item is required")
    private Long orderItemId;

    @NotNull(message = "Refund quantity is required")
    @Min(value = 1, message = "Refund quantity must be at least 1")
    private Integer quantity;

    private BigDecimal amount;
}