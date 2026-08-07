package com.main.MerchantMart.payload.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryDto {

    private Long id;

    private BranchDto branch;

    @NotNull(message = "Branch is required")
    private Long branchId;

    private ProductDto product;

    @NotNull(message = "Product is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity;

    private LocalDateTime lastUpdated;
}